package DAL.BD;

import DAL.IUnidadeCurricularDAL;
import Model.MomentoAvaliacao;
import Model.UnidadeCurricular;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da persistência de UnidadeCurricular em base de dados (SQL Server).
 *
 * Esquema esperado:
 *   CREATE TABLE UnidadeCurricular (
 *       nome              VARCHAR(100) NOT NULL,
 *       anoCurricular     INT          NOT NULL,
 *       ects              INT          NOT NULL,
 *       docenteResponsavel VARCHAR(10) NULL,
 *       momentos          VARCHAR(MAX) NULL,   -- serializado: nome:peso:anoLetivo|...
 *       ativa             BIT          NOT NULL DEFAULT 0,
 *       CONSTRAINT PK_UnidadeCurricular PRIMARY KEY (nome)
 *   );
 */
public class UnidadeCurricularDAL_BD implements IUnidadeCurricularDAL {

    private static final RowMapper<UnidadeCurricular> MAPPER = rs -> {
        String nome           = rs.getString("nome");
        int anoCurricular     = rs.getInt("anoCurricular");
        int ects              = rs.getInt("ects");
        String siglaDocente   = rs.getString("docenteResponsavel");
        String momentosStr    = rs.getString("momentos");
        boolean ativa         = rs.getBoolean("ativa");

        UnidadeCurricular uc = new UnidadeCurricular(nome, anoCurricular, ects,
                new ArrayList<>(), siglaDocente != null ? siglaDocente : "");
        if (siglaDocente != null && !siglaDocente.isBlank())
            uc.setDocenteResponsavel(siglaDocente);

        if (momentosStr != null && !momentosStr.isBlank()) {
            for (String parte : momentosStr.split("\\|")) {
                String[] mv = parte.split(":");
                try {
                    if (mv.length == 3) {
                        uc.adicionarMomento(new MomentoAvaliacao(
                                mv[0], Double.parseDouble(mv[1]), Integer.parseInt(mv[2])));
                    } else if (mv.length == 2) {
                        uc.adicionarMomento(new MomentoAvaliacao(
                                mv[0], Double.parseDouble(mv[1]), 0));
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        uc.setAtiva(ativa);
        return uc;
    };

    private final ConexaoBD conexao;

    public UnidadeCurricularDAL_BD() {
        this.conexao = new ConexaoBD();
    }

    @Override
    public void adicionarUnidade(UnidadeCurricular unidade) {
        conexao.execute(
                "INSERT INTO UnidadeCurricular (nome, anoCurricular, ects, docenteResponsavel, momentos, ativa) " +
                "VALUES (?, ?, ?, ?, ?, ?)",
                unidade.getNome(),
                unidade.getAnoCurricular(),
                unidade.getEts(),
                unidade.getDocenteResponsavel(),
                serializarMomentos(unidade.getMomentosAvaliacao()),
                unidade.isAtiva()
        );
    }

    @Override
    public boolean atualizarUnidade(UnidadeCurricular unidadeAtualizada) {
        int linhas = conexao.execute(
                "UPDATE UnidadeCurricular SET anoCurricular = ?, ects = ?, docenteResponsavel = ?, " +
                "momentos = ?, ativa = ? WHERE nome = ?",
                unidadeAtualizada.getAnoCurricular(),
                unidadeAtualizada.getEts(),
                unidadeAtualizada.getDocenteResponsavel(),
                serializarMomentos(unidadeAtualizada.getMomentosAvaliacao()),
                unidadeAtualizada.isAtiva(),
                unidadeAtualizada.getNome()
        );
        return linhas > 0;
    }

    @Override
    public ArrayList<UnidadeCurricular> listarUnidades() {
        return conexao.select(
                "SELECT nome, anoCurricular, ects, docenteResponsavel, momentos, ativa " +
                "FROM UnidadeCurricular",
                MAPPER
        );
    }

    @Override
    public void removerUnidade(UnidadeCurricular unidade) {
        conexao.execute("DELETE FROM UnidadeCurricular WHERE nome = ?", unidade.getNome());
    }

    @Override
    public UnidadeCurricular procurarPorNome(String nome) {
        ArrayList<UnidadeCurricular> resultados = conexao.select(
                "SELECT nome, anoCurricular, ects, docenteResponsavel, momentos, ativa " +
                "FROM UnidadeCurricular WHERE nome = ?",
                MAPPER, nome
        );
        return resultados.isEmpty() ? null : resultados.get(0);
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

    private String serializarMomentos(List<MomentoAvaliacao> momentos) {
        if (momentos == null || momentos.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < momentos.size(); i++) {
            MomentoAvaliacao m = momentos.get(i);
            sb.append(m.getNome().replace("|", "-").replace(":", "-"))
              .append(":").append(m.getPeso())
              .append(":").append(m.getAnoLetivo());
            if (i < momentos.size() - 1) sb.append("|");
        }
        return sb.toString();
    }
}
