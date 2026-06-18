package DAL.BD;

import DAL.IMomentoAvaliacaoDAL;
import Model.MomentoAvaliacao;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da persistência de MomentoAvaliacao em base de dados (SQL Server).
 *
 * Esquema esperado:
 *   CREATE TABLE MomentoAvaliacao (
 *       id          INT IDENTITY     NOT NULL,
 *       nomeUC      VARCHAR(100)     NOT NULL,
 *       nome        VARCHAR(100)     NOT NULL,
 *       peso        FLOAT            NOT NULL,
 *       anoLetivo   INT              NOT NULL DEFAULT 0,
 *       CONSTRAINT PK_MomentoAvaliacao PRIMARY KEY (id),
 *       CONSTRAINT FK_Momento_UC FOREIGN KEY (nomeUC)
 *           REFERENCES UnidadeCurricular(nome)
 *   );
 */
public class MomentoAvaliacaoDAL_BD implements IMomentoAvaliacaoDAL {

    private static final RowMapper<MomentoAvaliacao> MAPPER = rs ->
            new MomentoAvaliacao(
                    rs.getString("nome"),
                    rs.getDouble("peso"),
                    rs.getInt("anoLetivo")
            );

    private final ConexaoBD conexao;

    public MomentoAvaliacaoDAL_BD() {
        this.conexao = new ConexaoBD();
    }

    @Override
    public void guardarMomentos(String nomeUC, List<MomentoAvaliacao> momentos) {
        removerPorUC(nomeUC);
        if (momentos == null) return;
        for (MomentoAvaliacao m : momentos) {
            conexao.execute(
                    "INSERT INTO MomentoAvaliacao (nomeUC, nome, peso, anoLetivo) VALUES (?, ?, ?, ?)",
                    nomeUC, m.getNome(), m.getPeso(), m.getAnoLetivo()
            );
        }
    }

    @Override
    public List<MomentoAvaliacao> listarPorUC(String nomeUC) {
        return conexao.select(
                "SELECT nome, peso, anoLetivo FROM MomentoAvaliacao WHERE nomeUC = ? ORDER BY id",
                MAPPER, nomeUC
        );
    }

    @Override
    public void removerPorUC(String nomeUC) {
        conexao.execute("DELETE FROM MomentoAvaliacao WHERE nomeUC = ?", nomeUC);
    }

    @Override
    public java.util.Map<String, List<MomentoAvaliacao>> listarTodosPorUC() {
        // Retorna pares [nomeUC, MomentoAvaliacao] e agrupa em Java — uma única query.
        ArrayList<String[]> pares = conexao.select(
                "SELECT nomeUC, nome, peso, anoLetivo FROM MomentoAvaliacao ORDER BY nomeUC, id",
                rs -> new String[]{
                        rs.getString("nomeUC"),
                        rs.getString("nome"),
                        String.valueOf(rs.getDouble("peso")),
                        String.valueOf(rs.getInt("anoLetivo"))
                }
        );
        java.util.Map<String, List<MomentoAvaliacao>> resultado = new java.util.LinkedHashMap<>();
        for (String[] par : pares) {
            MomentoAvaliacao m = new MomentoAvaliacao(par[1], Double.parseDouble(par[2]), Integer.parseInt(par[3]));
            resultado.computeIfAbsent(par[0], k -> new ArrayList<>()).add(m);
        }
        return resultado;
    }
}
