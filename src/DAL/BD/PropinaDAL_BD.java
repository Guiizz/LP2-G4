package DAL.BD;

import DAL.IPagamentoDAL;
import DAL.IPropinaDAL;
import Model.Pagamento;
import Model.Propina;

import java.util.List;

/**
 * Implementação da persistência de Propina em base de dados (SQL Server).
 * Usa PagamentoDAL_BD para carregar/guardar o histórico de pagamentos.
 *
 * Esquema esperado:
 *   CREATE TABLE Propina (
 *       numMecanografico VARCHAR(20) NOT NULL,
 *       anoLetivo        INT         NOT NULL,
 *       valorTotal       FLOAT       NOT NULL DEFAULT 0,
 *       valorPago        FLOAT       NOT NULL DEFAULT 0,
 *       CONSTRAINT PK_Propina PRIMARY KEY (numMecanografico, anoLetivo),
 *       CONSTRAINT FK_Propina_Inscricao FOREIGN KEY (numMecanografico, anoLetivo)
 *           REFERENCES Inscricao(numMecanografico, anoLetivo)
 *   );
 */
public class PropinaDAL_BD implements IPropinaDAL {

    private final ConexaoBD conexao;
    private final IPagamentoDAL pagamentoDAL;

    public PropinaDAL_BD(IPagamentoDAL pagamentoDAL) {
        this.conexao      = new ConexaoBD();
        this.pagamentoDAL = pagamentoDAL;
    }

    @Override
    public void guardarPropina(String numMecanografico, int anoLetivo, Propina propina) {
        conexao.execute(
                "MERGE Propina AS alvo " +
                "USING (SELECT ? AS numMecanografico, ? AS anoLetivo) AS origem " +
                "   ON alvo.numMecanografico = origem.numMecanografico AND alvo.anoLetivo = origem.anoLetivo " +
                "WHEN MATCHED THEN " +
                "   UPDATE SET valorTotal = ?, valorPago = ? " +
                "WHEN NOT MATCHED THEN " +
                "   INSERT (numMecanografico, anoLetivo, valorTotal, valorPago) VALUES (?, ?, ?, ?);",
                numMecanografico, anoLetivo,
                propina.getValorTotal(), propina.getValorPago(),
                numMecanografico, anoLetivo, propina.getValorTotal(), propina.getValorPago()
        );
        // Sincronizar histórico: limpa e re-insere todos os pagamentos
        pagamentoDAL.removerPorInscricao(numMecanografico, anoLetivo);
        if (propina.getHistoricoPagamentos() != null) {
            for (Pagamento p : propina.getHistoricoPagamentos()) {
                pagamentoDAL.adicionarPagamento(numMecanografico, anoLetivo, p);
            }
        }
    }

    @Override
    public Propina carregarPropina(String numMecanografico, int anoLetivo) {
        var resultados = conexao.select(
                "SELECT valorTotal, valorPago FROM Propina " +
                "WHERE numMecanografico = ? AND anoLetivo = ?",
                rs -> {
                    double total = rs.getDouble("valorTotal");
                    double pago  = rs.getDouble("valorPago");
                    return new double[]{total, pago};
                },
                numMecanografico, anoLetivo
        );
        if (resultados.isEmpty()) return null;

        double valorTotal = resultados.get(0)[0];
        double valorPago  = resultados.get(0)[1];

        Propina propina = new Propina(valorTotal);

        // Reconstituir histórico de pagamentos
        List<Pagamento> historico = pagamentoDAL.listarPorInscricao(numMecanografico, anoLetivo);
        for (Pagamento p : historico) {
            try {
                propina.pagar(p.getValor());
            } catch (IllegalArgumentException ignored) {}
        }

        // Garantir que o valorPago coincide mesmo que os pagamentos individuais
        // não cubram exactamente (ex.: dados migrados do CSV sem histórico)
        if (Double.compare(propina.getValorPago(), valorPago) != 0 && valorPago > 0 && historico.isEmpty()) {
            try {
                propina.pagar(Math.min(valorPago, propina.getSaldoEmDebito()));
            } catch (IllegalArgumentException ignored) {}
        }

        return propina;
    }

    @Override
    public void removerPropina(String numMecanografico, int anoLetivo) {
        pagamentoDAL.removerPorInscricao(numMecanografico, anoLetivo);
        conexao.execute(
                "DELETE FROM Propina WHERE numMecanografico = ? AND anoLetivo = ?",
                numMecanografico, anoLetivo
        );
    }
}
