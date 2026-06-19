package DAL;

import Model.Pagamento;

import java.util.List;

/**
 * Contrato de acesso a dados de Pagamento, comum às implementações
 * em ficheiro/memória (PagamentoDAL — no-op) e em base de dados (DAL.BD.PagamentoDAL_BD).
 */
public interface IPagamentoDAL {

    /** Regista um pagamento para a inscrição indicada. */
    void adicionarPagamento(String numMecanografico, int anoLetivo, Pagamento pagamento);

    /** Devolve o histórico de pagamentos de uma inscrição. */
    List<Pagamento> listarPorInscricao(String numMecanografico, int anoLetivo);

    /** Remove todos os pagamentos de uma inscrição. */
    void removerPorInscricao(String numMecanografico, int anoLetivo);

    /**
     * Devolve todos os pagamentos agrupados por inscrição: chave = "numMecanografico|anoLetivo".
     * Usado para carregar pagamentos de todas as inscrições numa única query.
     */
    java.util.Map<String, List<Pagamento>> listarTodosAgrupados();
}
