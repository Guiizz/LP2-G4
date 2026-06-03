package DAL.BD;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.util.ArrayList;

public class ConexaoBD {

    private String serverName;
    private String databaseName;
    private String username;
    private String password;
    private Connection connection;

    public ConexaoBD() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        this.serverName   = dotenv.get("DB_SERVER");
        this.databaseName = dotenv.get("DB_DATABASE");
        this.username     = dotenv.get("DB_USER");
        this.password     = dotenv.get("DB_PASSWORD");
    }

    private Connection connect() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                String connectionUrl = "jdbc:sqlserver://" + serverName
                        + ";databaseName=" + databaseName
                        + ";user=" + username
                        + ";password=" + password
                        + ";encrypt=true;trustServerCertificate=true;sslProtocol=TLSv1.2;";
                connection = DriverManager.getConnection(connectionUrl);
            }
            return connection;
        } catch (Exception ex) {
            System.out.println("Erro ao conectar: " + ex.getMessage());
        }
        return null;
    }

    private void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception ex) {
            System.out.println("Erro ao desligar: " + ex.getMessage());
        }
    }

    private void beginTransaction() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.setAutoCommit(false);
            }
        } catch (Exception ex) {
            System.out.println("Erro ao iniciar transação: " + ex.getMessage());
        }
    }

    private void commitTransaction() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.commit();
            }
        } catch (Exception ex) {
            System.out.println("Erro ao fazer commit: " + ex.getMessage());
        }
    }

    private void rollbackTransaction() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
            }
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
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    results.add(mapper.mapRow(rs));
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

    public int execute(String sql, Object... params) {
        int rowsAffected = 0;
        try {
            connect();
            beginTransaction();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        stmt.setObject(i + 1, params[i]);
                    }
                }
                rowsAffected = stmt.executeUpdate();
                commitTransaction();
            }
        } catch (SQLException e) {
            rollbackTransaction();
            System.out.println("Erro no execute: " + e.getMessage());
        } finally {
            disconnect();
        }
        return rowsAffected;
    }
}