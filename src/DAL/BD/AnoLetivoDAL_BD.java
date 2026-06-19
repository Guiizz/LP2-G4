package DAL.BD;

import DAL.IAnoLetivoDAL;
import Model.AnoLetivo;
import Model.DadosEstudanteFecho;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da persistência de AnoLetivo em base de dados (SQL Server),
 * alternativa à versão em ficheiro/memória (DAL.AnoLetivoDAL).
 */
public class AnoLetivoDAL_BD implements IAnoLetivoDAL {

    private static final RowMapper<AnoLetivo> MAPPER = rs -> new AnoLetivo(
            rs.getInt("ano"),
            rs.getString("estado"),
            rs.getDate("dataAbertura").toLocalDate(),
            rs.getDate("dataFecho") != null ? rs.getDate("dataFecho").toLocalDate() : null
    );

    private final ConexaoBD conexao;

    public AnoLetivoDAL_BD() {
        this.conexao = new ConexaoBD();
    }

    @Override
    public void adicionarAnoLetivo(AnoLetivo anoLetivo) {
        conexao.execute(
                "INSERT INTO AnoLetivo (ano, estado, dataAbertura, dataFecho) VALUES (?, ?, ?, ?)",
                anoLetivo.getAno(),
                anoLetivo.getEstado(),
                Date.valueOf(anoLetivo.getDataAbertura()),
                anoLetivo.getDataFecho() != null ? Date.valueOf(anoLetivo.getDataFecho()) : null
        );
    }

    @Override
    public boolean atualizarAnoLetivo(AnoLetivo anoLetivoAtualizado) {
        int linhas = conexao.execute(
                "UPDATE AnoLetivo SET estado = ?, dataAbertura = ?, dataFecho = ? WHERE ano = ?",
                anoLetivoAtualizado.getEstado(),
                Date.valueOf(anoLetivoAtualizado.getDataAbertura()),
                anoLetivoAtualizado.getDataFecho() != null ? Date.valueOf(anoLetivoAtualizado.getDataFecho()) : null,
                anoLetivoAtualizado.getAno()
        );
        return linhas > 0;
    }

    @Override
    public ArrayList<AnoLetivo> listarAnosLetivos() {
        return conexao.select("SELECT ano, estado, dataAbertura, dataFecho FROM AnoLetivo", MAPPER);
    }

    @Override
    public void removerAnoLetivo(int ano) {
        conexao.execute("DELETE FROM AnoLetivo WHERE ano = ?", ano);
    }

    @Override
    public AnoLetivo procurarPorAno(int ano) {
        ArrayList<AnoLetivo> resultados = conexao.select(
                "SELECT ano, estado, dataAbertura, dataFecho FROM AnoLetivo WHERE ano = ?", MAPPER, ano
        );
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public AnoLetivo procurarAnoAberto() {
        ArrayList<AnoLetivo> resultados = conexao.select(
                "SELECT ano, estado, dataAbertura, dataFecho FROM AnoLetivo WHERE estado = ?",
                MAPPER, AnoLetivo.ESTADO_ABERTO
        );
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public AnoLetivo procurarMaisRecente() {
        ArrayList<AnoLetivo> resultados = conexao.select(
                "SELECT TOP 1 ano, estado, dataAbertura, dataFecho FROM AnoLetivo ORDER BY ano DESC", MAPPER
        );
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    // ── Fecho de Ano Letivo ───────────────────────────────────────────────────

    /**
     * Lê num único JOIN os dados de todos os estudantes ativos com inscrição
     * no ano letivo indicado, juntando o estado da propina calculado em SQL.
     *
     * Query gerada:
     * <pre>
     *   SELECT e.numMecanografico, e.nome, e.anoAtual, e.estado,
     *          i.anoDeCurso, i.nomeCurso, i.notas,
     *          CASE WHEN ISNULL(p.valorTotal,0) > 0
     *               AND p.valorPago >= p.valorTotal THEN 1 ELSE 0 END AS propinaPaga
     *   FROM Estudante e
     *   INNER JOIN Inscricao i
     *       ON e.numMecanografico = i.numMecanografico AND i.anoLetivo = ?
     *   LEFT  JOIN Propina p
     *       ON p.numMecanografico = i.numMecanografico AND p.anoLetivo = i.anoLetivo
     *   WHERE e.estado <> 'CONCLUIDO'
     *   ORDER BY e.nome
     * </pre>
     */
    @Override
    public ArrayList<DadosEstudanteFecho> carregarDadosParaFecho(int anoLetivo) {
        return conexao.select(
                "SELECT e.numMecanografico, e.nome, e.anoAtual, e.estado, " +
                "       i.anoDeCurso, i.nomeCurso, i.notas, " +
                "       CASE WHEN ISNULL(p.valorTotal, 0) > 0 " +
                "                 AND p.valorPago >= p.valorTotal THEN 1 ELSE 0 " +
                "       END AS propinaPaga, " +
                // Concatenar notas de todos os anos anteriores para calcular aproveitamento global
                "       STUFF(( " +
                "           SELECT ',' + ia.notas " +
                "           FROM   Inscricao ia " +
                "           WHERE  ia.numMecanografico = e.numMecanografico " +
                "             AND  ia.anoLetivo        < ? " +
                "             AND  ia.notas IS NOT NULL " +
                "             AND  ia.notas            <> '' " +
                "           FOR XML PATH(''), TYPE).value('.','NVARCHAR(MAX)'), 1, 1, '') AS notasAnteriores " +
                "FROM   Estudante  e " +
                "INNER  JOIN Inscricao i " +
                "       ON  e.numMecanografico = i.numMecanografico " +
                "       AND i.anoLetivo = ? " +
                "LEFT   JOIN Propina p " +
                "       ON  p.numMecanografico = i.numMecanografico " +
                "       AND p.anoLetivo        = i.anoLetivo " +
                "WHERE  e.estado <> 'CONCLUIDO' " +
                "ORDER  BY e.nome",
                rs -> new DadosEstudanteFecho(
                        rs.getString("numMecanografico"),
                        rs.getString("nome"),
                        rs.getInt("anoAtual"),
                        rs.getString("estado"),
                        rs.getInt("anoDeCurso"),
                        rs.getString("nomeCurso"),
                        rs.getBoolean("propinaPaga"),
                        rs.getString("notas"),
                        rs.getString("notasAnteriores")
                ),
                anoLetivo, anoLetivo   // dois parâmetros: um para subquery, outro para JOIN
        );
    }

    /**
     * Persiste os resultados do fecho directamente em BD:
     * <ul>
     *   <li>Avançar: anoAtual++, nova inscrição para o próximo ano letivo,
     *       nova propina com valorTotal copiado do curso.</li>
     *   <li>Concluir: estado = 'CONCLUIDO'.</li>
     * </ul>
     * Cada UPDATE/INSERT é executado por PK — sem full table scans.
     */
    @Override
    public void persistirResultadoFecho(int anoLetivo,
                                         List<String> numMecsAvancar,
                                         List<String> numMecsConcluir) {

        int proximoAnoLetivo = anoLetivo + 1;

        for (String numMec : numMecsAvancar) {

            // 1. Incrementa o ano de curso do estudante
            conexao.execute(
                    "UPDATE Estudante SET anoAtual = anoAtual + 1 " +
                    "WHERE numMecanografico = ?",
                    numMec
            );

            // 2. Cria a inscrição do próximo ano letivo copiando o curso e
            //    incrementando o anoDeCurso (notas começa vazia).
            conexao.execute(
                    "INSERT INTO Inscricao (numMecanografico, anoLetivo, anoDeCurso, nomeCurso, notas) " +
                    "SELECT i.numMecanografico, ? , i.anoDeCurso + 1, i.nomeCurso, '' " +
                    "FROM   Inscricao i " +
                    "WHERE  i.numMecanografico = ? AND i.anoLetivo = ?",
                    proximoAnoLetivo, numMec, anoLetivo
            );

            // 3. Cria a propina do próximo ano letivo com o valor do curso
            //    (valorPago começa a zero).
            conexao.execute(
                    "INSERT INTO Propina (numMecanografico, anoLetivo, valorTotal, valorPago) " +
                    "SELECT i.numMecanografico, ?, c.valorPropina, 0 " +
                    "FROM   Inscricao i " +
                    "JOIN   Curso     c ON c.nomeCurso = i.nomeCurso " +
                    "WHERE  i.numMecanografico = ? AND i.anoLetivo = ?",
                    proximoAnoLetivo, numMec, anoLetivo
            );
        }

        for (String numMec : numMecsConcluir) {
            conexao.execute(
                    "UPDATE Estudante SET estado = 'CONCLUIDO' " +
                    "WHERE numMecanografico = ?",
                    numMec
            );
        }
    }
}
