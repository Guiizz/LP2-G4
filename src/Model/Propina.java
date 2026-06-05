package Model;

import java.util.ArrayList;
import java.util.List;

public class Propina {

    private double valorTotal;
    private double valorPago;
    private List<Pagamento> historicoPagamentos;

    public Propina(double valorTotal) {
        this.valorTotal          = valorTotal;
        this.valorPago           = 0.0;
        this.historicoPagamentos = new ArrayList<>();
    }

    public double getValorTotal()  { return valorTotal; }
    public double getValorPago()   { return valorPago; }
    public List<Pagamento> getHistoricoPagamentos() { return historicoPagamentos; }

    /**
     * Atualiza o valor total da propina.
     * Usado quando o curso é iniciado para garantir que o valor reflecte
     * o configurado no curso no momento da iniciação.
     * Só é permitido se a propina ainda não tiver pagamentos registados.
     */
    public void setValorTotal(double novoValor) {
        if (novoValor < 0) {
            throw new IllegalArgumentException("O valor da propina não pode ser negativo.");
        }
        this.valorTotal = novoValor;
    }

    public double getSaldoEmDebito() { return valorTotal - valorPago; }

    public boolean isTotalmentePaga() { return getSaldoEmDebito() <= 0.0; }

    public void pagar(double valor) {
        if (valor <= 0)
            throw new IllegalArgumentException("O valor do pagamento tem de ser positivo.");
        if (valor > getSaldoEmDebito())
            throw new IllegalArgumentException(
                    String.format("O valor %.2f € excede o saldo em débito de %.2f €.", valor, getSaldoEmDebito()));
        valorPago += valor;
        historicoPagamentos.add(new Pagamento(valor));
    }

    @Override
    public String toString() {
        String estado;
        if (isTotalmentePaga()) {
            estado = "PAGA";
        } else if (valorPago > 0) {
            estado = "PARCIALMENTE PAGA";
        } else {
            estado = "POR PAGAR";
        }
        return String.format("Propina: %.2f € | Pago: %.2f € | Em dívida: %.2f € | Estado: %s",
                valorTotal, valorPago, getSaldoEmDebito(), estado);
    }
}