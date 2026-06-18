package DAL.BD;

import DAL.ICursoDAL;
import DAL.IEstudanteDAL;
import DAL.IInscricaoDAL;
import Model.Estudante;
import Utils.PasswordUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Implementação da persistência de Estudante em base de dados (SQL Server).
 * As inscrições são geridas pelo IInscricaoDAL injectado.
 *
 * Esquema esperado:
 *   CREATE TABLE Estudante (
 *       numMecanografico VARCHAR(20)  NOT NULL,
 *       nome             VARCHAR(100) NOT NULL,
 *       dataNascimento   DATE         NOT NULL,
 *       nif              VARCHAR(9)   NOT NULL,
 *       morada           VARCHAR(200) NOT NULL,
 *       anoAtual         INT          NOT NULL DEFAULT 1,
 *       email            VARCHAR(100) NOT NULL,
 *       password         VARCHAR(200) NOT NULL,
 *       primeiroLogin    BIT          NOT NULL DEFAULT 1,
 *       estado           VARCHAR(20)  NOT NULL DEFAULT 'ATIVO',
 *       CONSTRAINT PK_Estudante PRIMARY KEY (numMecanografico),
 *       CONSTRAINT UQ_Estudante_Nif   UNIQUE (nif),
 *       CONSTRAINT UQ_Estudante_Email UNIQUE (email)
 *   );
 */
public class EstudanteDAL_BD implements IEstudanteDAL {

    private final ConexaoBD conexao;
    private final IInscricaoDAL inscricaoDAL;
    private final ICursoDAL cursoDAL;

    public EstudanteDAL_BD(ICursoDAL cursoDAL, IInscricaoDAL inscricaoDAL) {
        this.conexao      = new ConexaoBD();
        this.cursoDAL     = cursoDAL;
        this.inscricaoDAL = inscricaoDAL;
        sincronizarContador();
    }

    @Override
    public void adicionarEstudante(Estudante estudante) {
        String pw = PasswordUtils.estaHasheada(estudante.getPassword())
                ? estudante.getPassword()
                : PasswordUtils.hashPassword(estudante.getPassword());
        conexao.execute(
                "INSERT INTO Estudante (numMecanografico, nome, dataNascimento, nif, morada, " +
                "anoAtual, email, password, primeiroLogin, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                estudante.getNumMecanografico(), estudante.getNome(),
                Date.valueOf(estudante.getDataNascimento()),
                estudante.getNif(), estudante.getMorada(),
                estudante.getAnoAtual(), estudante.getEmail(), pw,
                estudante.isPrimeiroLogin(), estudante.getEstado()
        );
        inscricaoDAL.guardarInscricoes(new ArrayList<>() {{ add(estudante); }});
    }

    @Override
    public boolean atualizarEstudante(Estudante estudanteAtualizado) {
        int linhas = conexao.execute(
                "UPDATE Estudante SET nome = ?, dataNascimento = ?, nif = ?, morada = ?, " +
                "anoAtual = ?, email = ?, password = ?, primeiroLogin = ?, estado = ? " +
                "WHERE numMecanografico = ?",
                estudanteAtualizado.getNome(),
                Date.valueOf(estudanteAtualizado.getDataNascimento()),
                estudanteAtualizado.getNif(), estudanteAtualizado.getMorada(),
                estudanteAtualizado.getAnoAtual(), estudanteAtualizado.getEmail(),
                estudanteAtualizado.getPassword(), estudanteAtualizado.isPrimeiroLogin(),
                estudanteAtualizado.getEstado(), estudanteAtualizado.getNumMecanografico()
        );
        if (linhas > 0) {
            inscricaoDAL.guardarInscricoes(new ArrayList<>() {{ add(estudanteAtualizado); }});
        }
        return linhas > 0;
    }

    @Override
    public ArrayList<Estudante> listarEstudantes() {
        ArrayList<Estudante> estudantes = conexao.select(
                "SELECT numMecanografico, nome, dataNascimento, nif, morada, anoAtual, " +
                "email, password, primeiroLogin, estado FROM Estudante",
                rs -> {
                    String numMec   = rs.getString("numMecanografico");
                    String nome     = rs.getString("nome");
                    LocalDate nasc  = rs.getDate("dataNascimento").toLocalDate();
                    String nif      = rs.getString("nif");
                    String morada   = rs.getString("morada");
                    int anoAtual    = rs.getInt("anoAtual");
                    String email    = rs.getString("email");
                    String password = rs.getString("password");
                    boolean primLog = rs.getBoolean("primeiroLogin");
                    String estado   = rs.getString("estado");

                    Estudante e = new Estudante(nome, nasc, nif, morada, true);
                    e.setNumMecanografico(numMec);
                    e.setAnoAtual(anoAtual);
                    e.setEmail(email);
                    e.setPassword(password);
                    e.setPrimeiroLogin(primLog);
                    e.setEstado(estado);
                    return e;
                }
        );
        inscricaoDAL.carregarInscricoes(estudantes, cursoDAL);
        return estudantes;
    }

    @Override
    public void removerEstudante(String numMecanografico) {
        conexao.execute("DELETE FROM Estudante WHERE numMecanografico = ?", numMecanografico);
    }

    @Override
    public Estudante procurarPorNumMecanografico(String numMecanografico) {
        ArrayList<Estudante> r = conexao.select(
                "SELECT numMecanografico, nome, dataNascimento, nif, morada, anoAtual, " +
                "email, password, primeiroLogin, estado FROM Estudante WHERE numMecanografico = ?",
                rs -> mapEstudante(rs), numMecanografico);
        if (r.isEmpty()) return null;
        inscricaoDAL.carregarInscricoes(r, cursoDAL);
        return r.get(0);
    }

    @Override
    public Estudante procurarPorNif(String nif) {
        ArrayList<Estudante> r = conexao.select(
                "SELECT numMecanografico, nome, dataNascimento, nif, morada, anoAtual, " +
                "email, password, primeiroLogin, estado FROM Estudante WHERE nif = ?",
                rs -> mapEstudante(rs), nif);
        if (r.isEmpty()) return null;
        inscricaoDAL.carregarInscricoes(r, cursoDAL);
        return r.get(0);
    }

    @Override
    public Estudante procurarPorEmail(String email) {
        ArrayList<Estudante> r = conexao.select(
                "SELECT numMecanografico, nome, dataNascimento, nif, morada, anoAtual, " +
                "email, password, primeiroLogin, estado FROM Estudante WHERE email = ?",
                rs -> mapEstudante(rs), email);
        if (r.isEmpty()) return null;
        inscricaoDAL.carregarInscricoes(r, cursoDAL);
        return r.get(0);
    }

    // -------------------------------------------------------------------------
    // Auxiliares
    // -------------------------------------------------------------------------

    /**
     * Ajusta o contador sequencial de Model.Estudante para o maior número
     * mecanográfico já existente na base de dados + 1, evitando que um novo
     * estudante seja criado a partir do valor inicial (260001) quando já
     * existem registos. Espelha o comportamento de EstudanteDAL.carregarDoCSV.
     */
    private void sincronizarContador() {
        int maiorNumero = Estudante.getContadorSequencial();
        ArrayList<String> numeros = conexao.select(
                "SELECT numMecanografico FROM Estudante",
                rs -> rs.getString("numMecanografico"));
        for (String numMec : numeros) {
            try {
                int numero = Integer.parseInt(numMec);
                if (numero >= maiorNumero) maiorNumero = numero + 1;
            } catch (NumberFormatException ignored) {}
        }
        Estudante.setContadorSequencial(maiorNumero);
    }

    private Estudante mapEstudante(java.sql.ResultSet rs) throws java.sql.SQLException {
        String numMec   = rs.getString("numMecanografico");
        String nome     = rs.getString("nome");
        LocalDate nasc  = rs.getDate("dataNascimento").toLocalDate();
        String nif      = rs.getString("nif");
        String morada   = rs.getString("morada");
        int anoAtual    = rs.getInt("anoAtual");
        String email    = rs.getString("email");
        String password = rs.getString("password");
        boolean primLog = rs.getBoolean("primeiroLogin");
        String estado   = rs.getString("estado");

        Estudante e = new Estudante(nome, nasc, nif, morada, true);
        e.setNumMecanografico(numMec);
        e.setAnoAtual(anoAtual);
        e.setEmail(email);
        e.setPassword(password);
        e.setPrimeiroLogin(primLog);
        e.setEstado(estado);
        return e;
    }
}
