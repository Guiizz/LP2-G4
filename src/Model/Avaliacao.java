package Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Avaliacao {

    /**
     * Atributos
     */
    private List<UnidadeCurricular> uc;
    private double peso;
    private Date data;
    private double nota;
    private boolean aprovado;

    /**
     * Construtor
     * @param uc
     * @param peso
     * @param data
     */
    public Avaliacao(List<UnidadeCurricular> uc, double peso, Date data, double nota) {
        this.uc = uc;
        this.peso = peso;
        this.data = data;
        this.nota = nota;
        this.aprovado = nota >= 10.0;
    }

    public List<UnidadeCurricular> getUc() {return uc;}

    public double getPeso() {return peso;}

    public double setPeso(double peso) {return this.peso = peso;}

    public Date getData() {return data;}

    public void setData(Date data) {this.data = data;}

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    /**
     * Devolve a data formatada para dd/MM/yyyy
     * @return data
     */

    public String getDataFormatada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(this.data);
    }


    @Override
    public String toString() {
        return  "===== Momento de Avaliação =====\n"+
                "Cadeira: "+ uc + "\n" +
                "Peso: " + peso + "\n" +
                "Data: " + getDataFormatada() + "\n" +
                "Nota Final: " + nota + "\n" +
                "Aprovado: " + (aprovado ? "Sim" : "Não") + "\n" +
                "================================";
    }
}

