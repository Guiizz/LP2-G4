package DAL.BD;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 * Gestor de acesso à base de dados SQL Server.
 *
 * Mantém o padrão de abrir a ligação no início de cada operação e fechá-la
 * no fim (connect()/disconnect() por select/execute/create), mas o custo de
 * "abrir" e "fechar" é otimizado através de um pool de ligações:
 *   - connect()    tira uma ligação já viva do pool em vez de criar uma nova
 *                  (só cria de raiz se o pool estiver vazio);
 *   - disconnect() devolve a ligação ao pool em vez de destruir o socket TCP
 *                  (só fecha de facto se o pool já estiver no limite).
 *
 * Do ponto de vista do chamador nada muda — continua a "abrir e fechar por
 * operação". O que se evita é o custo de handshake TCP + autenticação ao
 * SQL Server repetido em cada query.
 */
public class ConexaoBD {

    private static final int POOL_MAX = 8;
    private static final Deque<Connection> pool = new ArrayDeque<>();
    private static final Object POOL_LOCK = new Object();

    private static String connectionUrl;
    private static volatile boolean driverCarregado = false;

    private Connection connection;

    static {
        // O SQL Server do ISEP usa TLS antigo (1.0/1.1), desativado por defeito no Java moderno
        java.security.Security.setProperty("jdk.tls.disabledAlgorithms",
                "SSLv3, RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224, anon, NULL");
        Runtime.getRuntime().addShutdownHook(new Thread(ConexaoBD::fecharPool));
    }

    public ConexaoBD() {
        if (connectionUrl == null) {
            synchronized (POOL_LOCK) {
                if (connectionUrl == null) {
                    Dotenv dotenv = Dotenv.configure()
                            .directory("./")
                            .ignoreIfMalformed()
                            .ignoreIfMissing()
                            .load();

                    String serverName   = dotenv.get("DB_SERVER");
                    String databaseName = dotenv.get("DB_DATABASE");
                    String username     = dotenv.get("DB_USER");
                    String password     = dotenv.get("DB_PASSWORD");

                    connectionUrl = "jdbc:sqlserver://" + serverName
                            + ";databaseName=" + databaseName
                            + ";user=" + username
                            + ";password=" + password
                            + ";encrypt=false;trustServerCertificate=true";
                }
            }
        }
    }

    /** "Abre" a ligação: reutiliza uma do pool se houver uma válida, senão cria de raiz. */
    private Connection connect() {
        synchronized (POOL_LOCK) {
            Connection reutilizada;
            while ((reutilizada = pool.poll()) != null) {
                try {
                    if (reutilizada.isValid(1)) {
                        connection = reutilizada;
                        return connection;
                    }
                } catch (SQLException ignored) {}
                fecharSilenciosamente(reutilizada);
            }
        }
        try {
            if (!driverCarregado) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                driverCarregado = true;
            }
            connection = DriverManager.getConnection(connectionUrl);
        } catch (Exception ex) {
            System.out.println("Erro ao conectar: " + ex.getMessage());
            connection = null;
        }
        return connection;
    }

    /** "Fecha" a ligação: devolve-a ao pool em vez de destruir o socket TCP. */
    private void disconnect() {
        if (connection == null) return;
        Connection c = connection;
        connection = null;
        synchronized (POOL_LOCK) {
            try {
                if (!c.isClosed() && pool.size() < POOL_MAX) {
                    c.setAutoCommit(true);
                    pool.offer(c);
                    return;
                }
            } catch (SQLException ignored) {}
        }
        fecharSilenciosamente(c);
    }

    private static void fecharSilenciosamente(Connection c) {
        try {
            if (c != null && !c.isClosed()) c.close();
        } catch (Exception ignored) {}
    }

    private static void fecharPool() {
        synchronized (POOL_LOCK) {
            Connection c;
            while ((c = pool.poll()) != null) fecharSilenciosamente(c);
        }
    }

    private void beginTransaction() {
        try {
            if (connection != null) connection.setAutoCommit(false);
        } catch (Exception ex) {
            System.out.println("Erro ao iniciar transação: " + ex.getMessage());
        }
    }

    private void commitTransaction() {
        try {
            if (connection != null && !connection.isClosed()) connection.commit();
        } catch (Exception ex) {
            System.out.println("Erro ao fazer commit: " + ex.getMessage());
        }
    }

    private void rollbackTransaction() {
        try {
            if (connection != null && !connection.isClosed()) connection.rollback();
        } catch (Exception ex) {
            System.out.println("Erro ao fazer rollback: " + ex.getMessage());
        }
    }

    public <T> ArrayList<T> select(String sql, RowMapper<T> mapper, Object... params) {
        ArrayList<T> results = new ArrayList<>();
        try {
            connect();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        stmt.setObject(i + 1, params[i]);
                    }
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        results.add(mapper.mapRow(rs));
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Erro no select: " + ex.getMessage());
        } finally {
            disconnect();
        }
        return results;
    }

    public int create(String sql, Object... params) {
        int result = 0;
        try {
            connect();
            beginTransaction();
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        stmt.setObject(i + 1, params[i]);
                    }
                }
                stmt.executeUpdate();
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        result = generatedKeys.getInt(1);
                    }
                }
                commitTransaction();
            }
        } catch (SQLException e) {
            rollbackTransaction();
            System.out.println("Erro no create: " + e.getMessage());
        } finally {
            disconnect();
        }
        return result;
    }

    /**
     * Executa um bloco de operações numa única transação atómica.
     * Os execute() chamados dentro do bloco reutilizam esta ligação
     * sem fazer commit/disconnect intermédios.
     */
    public void executarEmTransacao(Runnable bloco) {
        try {
            connect();
            beginTransaction();
            bloco.run();
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            System.out.println("Erro na transação: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Executa um statement de escrita.
     * Se já existir uma ligação aberta (dentro de executarEmTransacao),
     * reutiliza-a sem commit/disconnect próprios — o controlo fica no chamador.
     * Caso contrário, abre, executa, faz commit e fecha sozinho.
     */
    public int execute(String sql, Object... params) {
        boolean gerirConexao;
        try {
            gerirConexao = (connection == null || connection.isClosed());
        } catch (SQLException e) {
            gerirConexao = true;
        }

        try {
            if (gerirConexao) {
                connect();
                beginTransaction();
            }
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        stmt.setObject(i + 1, params[i]);
                    }
                }
                int rows = stmt.executeUpdate();
                if (gerirConexao) commitTransaction();
                return rows;
            }
        } catch (SQLException e) {
            if (gerirConexao) rollbackTransaction();
            System.out.println("Erro no execute: " + e.getMessage());
            return 0;
        } finally {
            if (gerirConexao) disconnect();
        }
    }
}
