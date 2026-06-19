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
        if (avaliacoes.isEmpty()) return avaliacoes;

        // Carregar todas as UCs uma única vez (em vez de 1 query de UC + 1 de momentos POR avaliação)
        java.util.Map<String, UnidadeCurricular> ucsPorNome = new java.util.HashMap<>();
        for (UnidadeCurricular uc : ucDAL.listarUnidades()) ucsPorNome.put(uc.getNome(), uc);

        // Uma única query para todas as associações Avaliacao-UC
        ArrayList<Object[]> linhas = conexao.select(
                "SELECT idAvaliacao, nomeUC FROM AvaliacaoUC",
                rs -> new Object[]{ rs.getInt("idAvaliacao"), rs.getString("nomeUC") }
        );
        java.util.Map<Integer, List<UnidadeCurricular>> ucsPorAvaliacao = new java.util.HashMap<>();
        for (Object[] linha : linhas) {
            int idAvaliacao = (int) linha[0];
            UnidadeCurricular uc = ucsPorNome.get((String) linha[1]);
            if (uc != null) {
                ucsPorAvaliacao.computeIfAbsent(idAvaliacao, k -> new ArrayList<>()).add(uc);
            }
        }

        for (Avaliacao av : avaliacoes) {
            List<UnidadeCurricular> ucs = ucsPorAvaliacao.getOrDefault(av.getId(), new ArrayList<>());
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

}
