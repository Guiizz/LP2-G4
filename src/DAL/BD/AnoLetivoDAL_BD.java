package DAL.BD;

import DAL.IAnoLetivoDAL;
import Model.AnoLetivo;

import java.sql.Date;
import java.util.ArrayList;

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
}
