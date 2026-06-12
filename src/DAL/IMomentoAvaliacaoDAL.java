package DAL;

import Model.MomentoAvaliacao;

import java.util.List;

/**
 * Contrato de acesso a dados de MomentoAvaliacao, comum às implementações
 * em ficheiro/memória (MomentoAvaliacaoDAL — no-op, embutido na UC)
 * e em base de dados (DAL.BD.MomentoAvaliacaoDAL_BD).
 */
public interface IMomentoAvaliacaoDAL {

    /** Substitui todos os momentos de uma UC pelos fornecidos. */
    void guardarMomentos(String nomeUC, List<MomentoAvaliacao> momentos);

    /** Devolve todos os momentos de uma UC. */
    List<MomentoAvaliacao> listarPorUC(String nomeUC);

    /** Remove todos os momentos de uma UC (usado ao remover a UC). */
    void removerPorUC(String nomeUC);
}
