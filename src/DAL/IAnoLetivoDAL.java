package DAL;

import Model.AnoLetivo;

import java.util.ArrayList;

/**
 * Contrato de acesso a dados de AnoLetivo, comum às implementações
 * em ficheiro/memória (AnoLetivoDAL) e em base de dados (DAL.BD.AnoLetivoDAL_BD).
 */
public interface IAnoLetivoDAL {

    void adicionarAnoLetivo(AnoLetivo anoLetivo);

    boolean atualizarAnoLetivo(AnoLetivo anoLetivoAtualizado);

    ArrayList<AnoLetivo> listarAnosLetivos();

    void removerAnoLetivo(int ano);

    AnoLetivo procurarPorAno(int ano);

    AnoLetivo procurarAnoAberto();

    AnoLetivo procurarMaisRecente();
}
