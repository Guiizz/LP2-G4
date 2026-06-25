package DAL.BD;

import DAL.IMomentoAvaliacaoDAL;
import DAL.IUnidadeCurricularDAL;
import Model.MomentoAvaliacao;
import Model.UnidadeCurricular;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da persistência de UnidadeCurricular em base de dados (SQL Server).
 * Os momentos de avaliação são guardados numa tabela própria (MomentoAvaliacao),
 * gerida através de IMomentoAvaliacaoDAL.
 *
 * Esquema esperado:
 *   CREATE TABLE UnidadeCurricular (
 *       nome               VARCHAR(100) NOT NULL,
 *       anoCurricular      INT          NOT NULL,
 *       ects               INT          NOT NULL,
 *       docenteResponsavel VARCHAR(10)  NULL,
 *       ativa              BIT          NOT NULL DEFAULT 0,
 *       CONSTRAINT PK_UnidadeCurricular PRIMARY KEY (nome)
 *   );
 */
public class UnidadeCurricularDAL_BD implements IUnidadeCurricularDAL {

    private final ConexaoBD conexao;
    private final IMomentoAvaliacaoDAL momentoDAL;

    public UnidadeCurricularDAL_BD(IMomentoAvaliacaoDAL momentoDAL) {
        this.conexao     = new ConexaoBD();
        this.momentoDAL  = momentoDAL;
    }

    @Override
    public void adicionarUnidade(UnidadeCurricular unidade) {
        conexao.execute(
                "INSERT INTO UnidadeCurricular (nome, ects, docenteResponsavel, ativa) " +
                "VALUES (?, ?, ?, ?)",
                unidade.getNome(),
                unidade.getEts(),
                unidade.getDocenteResponsavel(),
                unidade.isAtiva()
        );
        momentoDAL.guardarMomentos(unidade.getNome(), unidade.getMomentosAvaliacao());
    }

    @Override
    public boolean atualizarUnidade(UnidadeCurricular unidadeAtualizada) {
        int linhas = conexao.execute(
                "UPDATE UnidadeCurricular SET ects = ?, docenteResponsavel = ?, ativa = ? " +
                "WHERE nome = ?",
                unidadeAtualizada.getEts(),
                unidadeAtualizada.getDocenteResponsavel(),
                unidadeAtualizada.isAtiva(),
                unidadeAtualizada.getNome()
        );
        if (linhas > 0) {
            momentoDAL.guardarMomentos(unidadeAtualizada.getNome(), unidadeAtualizada.getMomentosAvaliacao());
        }
        return linhas > 0;
    }

    @Override
    public ArrayList<UnidadeCurricular> listarUnidades() {
        // LEFT JOIN com Docente: obtém sigla e nome do docente na mesma query,
        // sem query separada por UC.
        ArrayList<UnidadeCurricular> unidades = conexao.select(
                "SELECT uc.nome, uc.ects, uc.docenteResponsavel, uc.ativa " +
                "FROM   UnidadeCurricular uc " +
                "LEFT   JOIN Docente d ON d.sigla = uc.docenteResponsavel",
                rs -> mapUC(rs.getString("nome"),
                            rs.getInt("ects"), rs.getString("docenteResponsavel"), rs.getBoolean("ativa"))
        );
        if (unidades.isEmpty()) return unidades;

        // Carregar TODOS os momentos de uma só vez (evita N queries — uma por UC).
        java.util.Map<String, UnidadeCurricular> porNome = new java.util.LinkedHashMap<>();
        for (UnidadeCurricular uc : unidades) porNome.put(uc.getNome(), uc);

        java.util.Map<String, List<MomentoAvaliacao>> momentosPorUC = momentoDAL.listarTodosPorUC();
        for (java.util.Map.Entry<String, List<MomentoAvaliacao>> entry : momentosPorUC.entrySet()) {
            UnidadeCurricular uc = porNome.get(entry.getKey());
            if (uc != null) {
                for (MomentoAvaliacao m : entry.getValue()) uc.adicionarMomento(m);
            }
        }
        return unidades;
    }

    @Override
    public void removerUnidade(UnidadeCurricular unidade) {
        momentoDAL.removerPorUC(unidade.getNome());
        conexao.execute("DELETE FROM UnidadeCurricular WHERE nome = ?", unidade.getNome());
    }

    @Override
    public UnidadeCurricular procurarPorNome(String nome) {
        ArrayList<UnidadeCurricular> resultados = conexao.select(
                "SELECT nome, ects, docenteResponsavel, ativa " +
                "FROM UnidadeCurricular WHERE nome = ?",
                rs -> mapUC(rs.getString("nome"),
                            rs.getInt("ects"), rs.getString("docenteResponsavel"), rs.getBoolean("ativa")),
                nome
        );
        if (resultados.isEmpty()) return null;
        UnidadeCurricular uc = resultados.get(0);
        carregarMomentos(uc);
        return uc;
    }

    @Override
    public boolean atribuirDocenteResponsavel(String nomeUC, String siglaDocente) {
        int linhas = conexao.execute(
                "UPDATE UnidadeCurricular SET docenteResponsavel = ? WHERE nome = ?",
                siglaDocente, nomeUC
        );
        return linhas > 0;
    }

    // -------------------------------------------------------------------------
    // Auxiliares
    // -------------------------------------------------------------------------

    private UnidadeCurricular mapUC(String nome, int ects,
                                    String siglaDocente, boolean ativa) {
        UnidadeCurricular uc = new UnidadeCurricular(nome, ects,
                new ArrayList<>(), siglaDocente != null ? siglaDocente : "");
        if (siglaDocente != null && !siglaDocente.isBlank())
            uc.setDocenteResponsavel(siglaDocente);
        uc.setAtiva(ativa);
        return uc;
    }

    private void carregarMomentos(UnidadeCurricular uc) {
        List<MomentoAvaliacao> momentos = momentoDAL.listarPorUC(uc.getNome());
        for (MomentoAvaliacao m : momentos) {
            uc.adicionarMomento(m);
        }
    }
}
