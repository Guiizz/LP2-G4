package Model;

import java.util.ArrayList;
import java.util.List;

public class UnidadeCurricular {

    private String nome;
    private int anoCurricular;
    private int ects;
    private List<Avaliacao> avaliacoes;
    private String docenteResponsavel;
    private List<MomentoAvaliacao> momentosAvaliacao;
    private boolean ativa;

    public UnidadeCurricular(String nome, int ano, int ects) {
        this.nome = nome;
        this.anoCurricular = ano;
        this.ects = ects;
        this.avaliacoes = new ArrayList<>();
        this.docenteResponsavel = null;
        this.momentosAvaliacao = new ArrayList<>();
        this.ativa = false;
    }

    public UnidadeCurricular(String nome, int ano, int ects, List<Avaliacao> avaliacoes, String docenteResponsavel) {
        this.nome = nome;
        this.anoCurricular = ano;
        this.ects = ects;
        this.avaliacoes = avaliacoes != null ? avaliacoes : new ArrayList<>();
        this.docenteResponsavel = docenteResponsavel;
        this.momentosAvaliacao = new ArrayList<>();
        this.ativa = false;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getAnoCurricular() {
        return anoCurricular;
    }

    public void setAnoCurricular(int ano) {
        this.anoCurricular = ano;
    }

    public int getEts() {
        return ects;
    }

    public void setEts(int ects) {
        this.ects = ects;
    }

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(List<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes != null ? avaliacoes : new ArrayList<>();
    }

    public String getDocenteResponsavel() {
        return docenteResponsavel;
    }

    public void setDocenteResponsavel(String docenteResponsavel) {
        this.docenteResponsavel = docenteResponsavel;
    }

    public boolean temDocenteResponsavel(){
        return this.docenteResponsavel != null && !this.docenteResponsavel.isEmpty();
    }

    public List<MomentoAvaliacao> getMomentosAvaliacao() { return momentosAvaliacao; }
    public void setMomentosAvaliacao(List<MomentoAvaliacao> momentosAvaliacao) {
        this.momentosAvaliacao = momentosAvaliacao;
    }

    /** Adiciona um momento de avaliação à lista da UC. */
    public void adicionarMomento(MomentoAvaliacao momento) {
        this.momentosAvaliacao.add(momento);
    }

    /**
     * Verifica se a UC tem exatamente 3 momentos com soma de pesos = 100%.
     * Condição obrigatória para poder iniciar a UC.
     */
    public boolean momentosValidos() {
        if (momentosAvaliacao == null || momentosAvaliacao.isEmpty()) return false;
        if (momentosAvaliacao.size() > 3) return false;

        double soma = 0;
        for (MomentoAvaliacao m : momentosAvaliacao) {
            soma += m.getPeso();
        }

        return Math.abs(soma - 100.0) < 0.01;
    }

    /** Soma atual dos pesos dos momentos definidos. */
    public double somaPesos() {
        double soma = 0;
        for (MomentoAvaliacao m : momentosAvaliacao) {
            soma += m.getPeso();
        }
        return soma;
    }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    @Override
    public String toString() {
        String docente = temDocenteResponsavel() ? docenteResponsavel : "(sem docente responsável)";
        return  "=== Unidade Curricular ===\n" +
                "Nome: " + this.nome + "\n" +
                "Ano: " + this.anoCurricular + "\n" +
                "ECTS: " + this.ects + "\n" +
                "Avaliações: " + this.avaliacoes.size() + "\n" +
                "Docente: " + docente + "\n" +
                "Momentos: " + this.momentosAvaliacao.size() + "\n" +
                "Estado: " + (this.ativa ? "Ativa" : "Inativa") + "\n" +
                "===========================";
    }
}