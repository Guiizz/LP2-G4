package Model;

public class Nota {
    private double valor;
    private boolean aprovado;

    public Nota(double valor) {
        this.valor = valor;
        this.aprovado = valor >= 10.0; // Considera aprovado se a nota for 10 ou superior
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "=== Nota ===\n" +
                "Valor: " + valor + "\n" +
                "Aprovado: " + (aprovado ? "Sim" : "Não") + "\n";
    }
}
