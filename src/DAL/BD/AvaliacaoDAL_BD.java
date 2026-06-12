package DAL.BD;

import DAL.IAvaliacaoDAL;
import DAL.IUnidadeCurricularDAL;
import Model.Avaliacao;
import Model.UnidadeCurricular;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da persistência de Avaliacao em base de dados (SQL Server).
 *
 * Esquema esperado:
 *   CREATE TABLE Avaliacao (
 *       id       INT IDENTITY  NOT NULL,
 *       peso     FLOAT         NOT NULL,
 *       data     DATE          NOT NULL,
 *       nota     FLOAT         NOT NULL DEFAULT 0,
 *       aprovado BIT           NOT NULL DEFAULT 0,
 *       lancada  BIT           NOT NULL DEFAULT 0,
 *       CONSTRAINT PK_Avaliacao PRIMARY KEY (id)
 *   );
 *
 *   CREATE TABLE AvaliacaoUC (
 *       idAvaliacao  INT          NOT NULL,
 *       nomeUC       VARCHAR(100) NOT NULL,
 *       CONSTRAINT PK_AvaliacaoUC PRIMARY KEY (idAvaliacao, nomeUC),
 *       CONSTRAINT FK_AvaliacaoUC_Avaliacao FOREIGN KEY (idAvaliacao) REFERENCES Avaliacao(id),
 *       CONSTRAINT FK_AvaliacaoUC_UC        FOREIGN KEY (nomeUC)      REFERENCES UnidadeCurricular(nome)
 *   );
 */
public class AvaliacaoDAL_BD implements IAvaliacaoDAL {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    private final ConexaoBD conexao;
    private final IUnidadeCurricularDAL ucDAL;

    public AvaliacaoDAL_BD(IUnidadeCurricularDAL ucDAL) {
        this.conexao = new ConexaoBD();
        this.ucDAL   = ucDAL;
    }

    @Override
    public void adicionarAvaliacao(Avaliacao avaliacao) {
        int id = conexao.create(
                "INSERT INTO Avaliacao (peso, data, nota, aprovado, lancada) VALUES (?, ?, ?, ?, ?)",
                avaliacao.getPeso(),
                avaliacao.getData() != null ? new Date(avaliacao.getData().getTime()) : null,
                avaliacao.getNota(),
                avaliacao.isAprovado(),
                avaliacao.isLancada()
        );
        avaliacao.setId(id);
        guardarUCsAvaliacao(id, avaliacao.getUc());
    }

    @Override
    public ArrayList<Avaliacao> listarAvaliacoes() {
        ArrayList<Avaliacao> avaliacoes = conexao.select(
                "SELECT id, peso, data, nota, aprovado, lancada FROM Avaliacao",
                rs -> {
                    int id       = rs.getInt("id");
                    double peso  = rs.getDouble("peso");
                    Date data    = rs.getDate("data");
                    double nota  = rs.getDouble("nota");
                    boolean lanc = rs.getBoolean("lancada");

                    Avaliacao av = lanc
                            ? new Avaliacao(new ArrayList<>(), peso,
                                    data != null ? new java.util.Date(data.getTime()) : null,
                                    nota, nota >= 10)
                            : new Avaliacao(new ArrayList<>(), peso,
                                    data != null ? new java.util.Date(data.getTime()) : null);
                    av.setId(id);
                    return av;
                }
        );
        for (Avaliacao av : avaliacoes) {
            List<UnidadeCurricular> ucs = carregarUCsAvaliacao(av.getId());
            av.getUc().addAll(ucs);
            for (UnidadeCurricular uc : ucs) {
                if (uc.getAvaliacoes() != null && !uc.getAvaliacoes().contains(av))
                    uc.getAvaliacoes().add(av);
            }
        }
        return avaliacoes;
    }

    @Override
    public boolean atualizarAvaliacao(Avaliacao avaliacaoAntiga, Avaliacao avaliacaoNova) {
        int id = avaliacaoAntiga.getId();
        if (id == 0) return false;

        int linhas = conexao.execute(
                "UPDATE Avaliacao SET peso = ?, data = ?, nota = ?, aprovado = ?, lancada = ? WHERE id = ?",
                avaliacaoNova.getPeso(),
                avaliacaoNova.getData() != null ? new Date(avaliacaoNova.getData().getTime()) : null,
                avaliacaoNova.getNota(),
                avaliacaoNova.isAprovado(),
                avaliacaoNova.isLancada(),
                id
        );
        if (linhas > 0) {
            conexao.execute("DELETE FROM AvaliacaoUC WHERE idAvaliacao = ?", id);
            guardarUCsAvaliacao(id, avaliacaoNova.getUc());
            avaliacaoNova.setId(id);
        }
        return linhas > 0;
    }

    @Override
    public void removerAvaliacao(Avaliacao avaliacao) {
        if (avaliacao.getId() == 0) return;
        conexao.execute("DELETE FROM AvaliacaoUC WHERE idAvaliacao = ?", avaliacao.getId());
        conexao.execute("DELETE FROM Avaliacao WHERE id = ?", avaliacao.getId());
    }

    @Override
    public ArrayList<Avaliacao> procurarPorUC(UnidadeCurricular uc) {
        ArrayList<Avaliacao> todas = listarAvaliacoes();
        ArrayList<Avaliacao> resultado = new ArrayList<>();
        for (Avaliacao av : todas) {
            if (av.getUc() != null && av.getUc().contains(uc)) resultado.add(av);
        }
        return resultado;
    }

    @Override
    public ArrayList<Avaliacao> procurarPorData(java.util.Date data) {
        if (data == null) return new ArrayList<>();
        String dataAlvo = SDF.format(data);
        ArrayList<Avaliacao> todas = listarAvaliacoes();
        ArrayList<Avaliacao> resultado = new ArrayList<>();
        for (Avaliacao av : todas) {
            if (av.getData() != null && SDF.format(av.getData()).equals(dataAlvo))
                resultado.add(av);
        }
        return resultado;
    }

    // -------------------------------------------------------------------------
    // Auxiliares
    // -------------------------------------------------------------------------

    private void guardarUCsAvaliacao(int idAvaliacao, List<UnidadeCurricular> ucs) {
        if (ucs == null) return;
        for (UnidadeCurricular uc : ucs) {
            conexao.execute(
                    "INSERT INTO AvaliacaoUC (idAvaliacao, nomeUC) VALUES (?, ?)",
                    idAvaliacao, uc.getNome()
            );
        }
    }

    private List<UnidadeCurricular> carregarUCsAvaliacao(int idAvaliacao) {
        ArrayList<UnidadeCurricular> ucs = new ArrayList<>();
        ArrayList<String> nomes = conexao.select(
                "SELECT nomeUC FROM AvaliacaoUC WHERE idAvaliacao = ?",
                rs -> rs.getString("nomeUC"),
                idAvaliacao
        );
        for (String nome : nomes) {
            UnidadeCurricular uc = ucDAL.procurarPorNome(nome);
            if (uc != null) ucs.add(uc);
        }
        return ucs;
    }
}
