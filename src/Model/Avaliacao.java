package Model;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class Avaliacao {

    /**
     * Atributos
     */
    private List<UnidadeCurricular> uc;
    private double peso;
    private Date data;

    /**
     * Construtor
     * @param uc
     * @param peso
     * @param data
     */
    public Avaliacao(List<UnidadeCurricular> uc, double peso, Date data) {
        this.uc = uc;
        this.peso = peso;
        this.data = data;
    }

    public List<UnidadeCurricular> getUc() {return uc;}

    public double getPeso() {return peso;}

    public double setPeso(double peso) {return this.peso = peso;}

    public Date getData() {return data;}

    public void setData(Date data) {this.data = data;}

    @Override
    public String toString() {
        return  "===== Momento de Avaliação =====\n"+
                "Cadeira: "+ uc + "\n" +
                "Peso: " + peso + "\n" +
                "Data: " + data + "\n" +
                "================================";
    }
}

