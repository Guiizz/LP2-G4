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
    private boolean lancada;
    private Date dataModificacao;

    /**
     * Construtor para avaliação com nota já lançada (comportamento anterior mantido).
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
        this.lancada = true;
    }
    /**
     * Construtor para avaliação ainda não lançada (Pendente).
     * Usar quando o docente ainda não registou a nota.
     */
    public Avaliacao(List<UnidadeCurricular> uc, double peso, Date data) {
        this.uc = uc;
        this.peso = peso;
        this.data = data;
        this.nota = 0;
        this.aprovado = false;
        this.lancada = false;
    }
    /**
     * Gets e Sets
     * @return
     */
    public List<UnidadeCurricular> getUc() {return uc;}

    public double getPeso() {return peso;}

    public void setPeso(double peso) { this.peso = peso; }

    public Date getData() {return data;}

    public void setData(Date data) {this.data = data;}

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public boolean isAprovado(){
        return aprovado;
    }

    public boolean isLancada() { return lancada; }

    public void lancarNota(double nota, boolean aprovado){
        this.nota = nota;
        this.aprovado = aprovado;
        this.lancada = true;
        this.dataModificacao = new Date();
    }

    public Date getDataModificacao() { return dataModificacao; }

    public String getDataModificacaoFormatada() {
        if (dataModificacao == null) return "-";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(dataModificacao);
    }

    /**
     * Devolve a nota formatada:
     *  - Se lançada: o valor numérico (ex: "15,5")
     *  - Se não lançada: "Pendente"
     */
    public String getNotaFormatada() {
        return lancada ? String.format("%.1f", nota) : "Pendente";
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
                "Cadeira: " + (uc != null && !uc.isEmpty() ? uc.get(0).getNome() : "(sem UC)") + "\n" +
                "Peso: " + peso + "\n" +
                "Nota: " + getNotaFormatada()+ "\n" +
                "Data: " + getDataFormatada() + "\n" +
                (dataModificacao != null ? "Última modificação: " + getDataModificacaoFormatada() + "\n" : "") +
                "================================";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Avaliacao)) {
            return false;
        }
        Avaliacao that = (Avaliacao) o;
        if (this.data == null || that.data == null) {
            return false;
        }
        return this.data.equals(that.data) && (this.uc != null ? this.uc.equals(that.uc) : that.uc == null);
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (uc != null ? uc.hashCode() : 0);
        return result;
    }
}

