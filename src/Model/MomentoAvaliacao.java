package Model;

public class MomentoAvaliacao {
    private String nome;
    private double peso;

    public MomentoAvaliacao(String nome, double peso) {
        this.nome = nome;
        this.peso = peso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    @Override
    public String toString() {
        return  "--- Momento de Avaliação ---" +
                "\nNome: " + this.nome +
                "\nPeso: " + this.peso;
    }
}
