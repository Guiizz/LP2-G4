package Model;

import java.time.LocalDate;

public class Pagamento {

    private double valor;
    private LocalDate data;

    public Pagamento(double valor) {
        this.valor = valor;
        this.data  = LocalDate.now();
    }

    public double getValor()    { return valor; }
    public LocalDate getData()  { return data; }

    @Override
    public String toString() {
        return String.format("  Pagamento de %.2f € em %s", valor, data);
    }
}
