package DAL.BD;

import DAL.IGestorDAL;
import Model.Gestor;
import Utils.PasswordUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Implementação da persistência de Gestor em base de dados (SQL Server).
 *
 * Esquema esperado:
 *   CREATE TABLE Gestor (
 *       nif              VARCHAR(9)   NOT NULL,
 *       nome             VARCHAR(100) NOT NULL,
 *       dataNascimento   DATE         NOT NULL,
 *       morada           VARCHAR(200) NOT NULL,
 *       email            VARCHAR(100) NOT NULL,
 *       password         VARCHAR(200) NOT NULL,
 *       primeiroLogin    BIT          NOT NULL DEFAULT 1,
 *       CONSTRAINT PK_Gestor PRIMARY KEY (nif),
 *       CONSTRAINT UQ_Gestor_Email UNIQUE (email)
 *   );
 */
public class GestorDAL_BD implements IGestorDAL {

    private static final RowMapper<Gestor> MAPPER = rs -> {
        String nome            = rs.getString("nome");
        LocalDate dataNasc     = rs.getDate("dataNascimento").toLocalDate();
        String nif             = rs.getString("nif");
        String morada          = rs.getString("morada");
        String email           = rs.getString("email");
        String password        = rs.getString("password");
        boolean primeiroLogin  = rs.getBoolean("primeiroLogin");

        Gestor g = new Gestor(nome, dataNasc, nif, morada, email, password);
        g.setPrimeiroLogin(primeiroLogin);
        return g;
    };

    private final ConexaoBD conexao;

    public GestorDAL_BD() {
        this.conexao = new ConexaoBD();
    }

    @Override
    public void adicionarGestor(Gestor gestor) {
        String pw = PasswordUtils.estaHasheada(gestor.getPassword())
                ? gestor.getPassword()
                : PasswordUtils.hashPassword(gestor.getPassword());
        conexao.execute(
                "INSERT INTO Gestor (nif, nome, dataNascimento, morada, email, password, primeiroLogin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                gestor.getNif(), gestor.getNome(),
                Date.valueOf(gestor.getDataNascimento()),
                gestor.getMorada(), gestor.getEmail(), pw,
                gestor.isPrimeiroLogin()
        );
    }

    @Override
    public boolean atualizarGestor(Gestor gestorAtualizado) {
        int linhas = conexao.execute(
                "UPDATE Gestor SET nome = ?, dataNascimento = ?, morada = ?, email = ?, " +
                "password = ?, primeiroLogin = ? WHERE nif = ?",
                gestorAtualizado.getNome(),
                Date.valueOf(gestorAtualizado.getDataNascimento()),
                gestorAtualizado.getMorada(),
                gestorAtualizado.getEmail(),
                gestorAtualizado.getPassword(),
                gestorAtualizado.isPrimeiroLogin(),
                gestorAtualizado.getNif()
        );
        return linhas > 0;
    }

    @Override
    public ArrayList<Gestor> listarGestores() {
        return conexao.select(
                "SELECT nif, nome, dataNascimento, morada, email, password, primeiroLogin FROM Gestor",
                MAPPER
        );
    }

    @Override
    public void removerGestor(Gestor gestor) {
        conexao.execute("DELETE FROM Gestor WHERE nif = ?", gestor.getNif());
    }

    @Override
    public Gestor procurarPorNif(String nif) {
        ArrayList<Gestor> r = conexao.select(
                "SELECT nif, nome, dataNascimento, morada, email, password, primeiroLogin " +
                "FROM Gestor WHERE nif = ?", MAPPER, nif);
        return r.isEmpty() ? null : r.get(0);
    }

    @Override
    public Gestor procurarPorEmail(String email) {
        ArrayList<Gestor> r = conexao.select(
                "SELECT nif, nome, dataNascimento, morada, email, password, primeiroLogin " +
                "FROM Gestor WHERE email = ?", MAPPER, email);
        return r.isEmpty() ? null : r.get(0);
    }
}
