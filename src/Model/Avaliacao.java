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
     * @param nota
     * @param aprovado
     */
    public Avaliacao(List<UnidadeCurricular> uc, double peso, Date data, double nota,boolean aprovado) {
        this.uc = uc;
        this.peso = peso;
        this.data = data;
        this.nota = nota;
        this.aprovado = nota >= 10.0;
    }

    /**
     * Gets e Sets
     * @return
     */
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
     * Devolve a data formatada em dd/MM/yyyy
     * @return data formatada
     */
    public String getDataFormatada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(this.data);
    }

    /**
     * toSting Avaliação
     * @return
     */
    @Override
    public String toString() {
        return  "===== Momento de Avaliação =====\n"+
                "Cadeira: "+ uc + "\n" +
                "Peso: " + peso + "\n" +
                "Nota: " + nota + "\n" +
                "Data: " + data + "\n" +
                "================================";
    }
}

