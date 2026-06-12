package DAL.BD;

import DAL.IDocenteDAL;
import DAL.IUnidadeCurricularDAL;
import Model.Docente;
import Model.UnidadeCurricular;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da persistência de Docente em base de dados (SQL Server).
 * As UCs lecionadas são serializadas como string por enquanto (mesmo padrão do CSV).
 *
 * Esquema esperado:
 *   CREATE TABLE Docente (
 *       sigla            VARCHAR(10)  NOT NULL,
 *       nome             VARCHAR(100) NOT NULL,
 *       dataNascimento   DATE         NOT NULL,
 *       nif              VARCHAR(9)   NOT NULL,
 *       morada           VARCHAR(200) NOT NULL,
 *       ucsLecionadas    VARCHAR(MAX) NULL,
 *       password         VARCHAR(200) NOT NULL,
 *       primeiroLogin    BIT          NOT NULL DEFAULT 1,
 *       CONSTRAINT PK_Docente PRIMARY KEY (sigla),
 *       CONSTRAINT UQ_Docente_Nif   UNIQUE (nif)
 *   );
 */
public class DocenteDAL_BD implements IDocenteDAL {

    private final ConexaoBD conexao;
    private final IUnidadeCurricularDAL ucDAL;

    public DocenteDAL_BD(IUnidadeCurricularDAL ucDAL) {
        this.conexao = new ConexaoBD();
        this.ucDAL   = ucDAL;
    }

    @Override
    public void adicionarDocente(Docente docente) {
        conexao.execute(
                "INSERT INTO Docente (sigla, nome, dataNascimento, nif, morada, ucsLecionadas, password, primeiroLogin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                docente.getSigla(), docente.getNome(),
                Date.valueOf(docente.getDataNascimento()),
                docente.getNif(), docente.getMorada(),
                serializarUCs(docente.getUnidadesLecionadas()),
                docente.getPassword(), docente.isPrimeiroLogin()
        );
    }

    @Override
    public boolean atualizarDocente(Docente docenteAtualizado) {
        int linhas = conexao.execute(
                "UPDATE Docente SET nome = ?, dataNascimento = ?, nif = ?, morada = ?, " +
                "ucsLecionadas = ?, password = ?, primeiroLogin = ? WHERE sigla = ?",
                docenteAtualizado.getNome(),
                Date.valueOf(docenteAtualizado.getDataNascimento()),
                docenteAtualizado.getNif(), docenteAtualizado.getMorada(),
                serializarUCs(docenteAtualizado.getUnidadesLecionadas()),
                docenteAtualizado.getPassword(), docenteAtualizado.isPrimeiroLogin(),
                docenteAtualizado.getSigla()
        );
        return linhas > 0;
    }

    @Override
    public ArrayList<Docente> listarDocentes() {
        return conexao.select(
                "SELECT sigla, nome, dataNascimento, nif, morada, ucsLecionadas, password, primeiroLogin FROM Docente",
                rs -> mapDocente(rs.getString("sigla"), rs.getString("nome"),
                        rs.getDate("dataNascimento").toLocalDate(), rs.getString("nif"),
                        rs.getString("morada"), rs.getString("ucsLecionadas"),
                        rs.getString("password"), rs.getBoolean("primeiroLogin"))
        );
    }

    @Override
    public void removerDocente(Docente docente) {
        conexao.execute("DELETE FROM Docente WHERE sigla = ?", docente.getSigla());
    }

    @Override
    public Docente procurarPorSigla(String sigla) {
        ArrayList<Docente> r = conexao.select(
                "SELECT sigla, nome, dataNascimento, nif, morada, ucsLecionadas, password, primeiroLogin " +
                "FROM Docente WHERE sigla = ?",
                rs -> mapDocente(rs.getString("sigla"), rs.getString("nome"),
                        rs.getDate("dataNascimento").toLocalDate(), rs.getString("nif"),
                        rs.getString("morada"), rs.getString("ucsLecionadas"),
                        rs.getString("password"), rs.getBoolean("primeiroLogin")),
                sigla);
        return r.isEmpty() ? null : r.get(0);
    }

    @Override
    public Docente procurarPorNif(String nif) {
        ArrayList<Docente> r = conexao.select(
                "SELECT sigla, nome, dataNascimento, nif, morada, ucsLecionadas, password, primeiroLogin " +
                "FROM Docente WHERE nif = ?",
                rs -> mapDocente(rs.getString("sigla"), rs.getString("nome"),
                        rs.getDate("dataNascimento").toLocalDate(), rs.getString("nif"),
                        rs.getString("morada"), rs.getString("ucsLecionadas"),
                        rs.getString("password"), rs.getBoolean("primeiroLogin")),
                nif);
        return r.isEmpty() ? null : r.get(0);
    }

    @Override
    public Docente procurarPorEmail(String email) {
        ArrayList<Docente> r = conexao.select(
                "SELECT sigla, nome, dataNascimento, nif, morada, ucsLecionadas, password, primeiroLogin " +
                "FROM Docente WHERE email = ?",
                rs -> mapDocente(rs.getString("sigla"), rs.getString("nome"),
                        rs.getDate("dataNascimento").toLocalDate(), rs.getString("nif"),
                        rs.getString("morada"), rs.getString("ucsLecionadas"),
                        rs.getString("password"), rs.getBoolean("primeiroLogin")),
                email);
        return r.isEmpty() ? null : r.get(0);
    }

    // -------------------------------------------------------------------------
    // Auxiliares
    // -------------------------------------------------------------------------

    private Docente mapDocente(String sigla, String nome, LocalDate dataNasc, String nif,
                                String morada, String nomesUCs, String password, boolean primeiroLogin) {
        List<UnidadeCurricular> unidades = new ArrayList<>();
        if (nomesUCs != null && !nomesUCs.isBlank()) {
            for (String nomeUC : nomesUCs.split(",")) {
                UnidadeCurricular uc = ucDAL.procurarPorNome(nomeUC.trim());
                if (uc != null) unidades.add(uc);
            }
        }
        Docente d = new Docente(nome, dataNasc, nif, morada, sigla, unidades);
        d.setPassword(password);
        d.setPrimeiroLogin(primeiroLogin);
        return d;
    }

    private String serializarUCs(List<UnidadeCurricular> ucs) {
        if (ucs == null || ucs.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ucs.size(); i++) {
            sb.append(ucs.get(i).getNome());
            if (i < ucs.size() - 1) sb.append(",");
        }
        return sb.toString();
    }
}
