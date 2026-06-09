package DAL;

import Model.Pagamento;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação CSV de IPagamentoDAL.
 * Em modo ficheiro o histórico de pagamentos não é persistido individualmente
 * (só o total é guardado na InscricaoDAL) — esta classe é no-op.
 */
public class PagamentoDAL implements IPagamentoDAL {

    @Override
    public void adicionarPagamento(String numMecanografico, int anoLetivo, Pagamento pagamento) {
        // No-op.
    }

    @Override
    public List<Pagamento> listarPorInscricao(String numMecanografico, int anoLetivo) {
        return new ArrayList<>();
    }

    @Override
    public void removerPorInscricao(String numMecanografico, int anoLetivo) {
        // No-op.
    }
}
