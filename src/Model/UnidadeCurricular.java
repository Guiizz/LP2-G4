package Model;

import java.util.ArrayList;
import java.util.List;

public class UnidadeCurricular {

    private String nome;
    private int anoCurricular;
    private int ects;
    private List<Avaliacao> avaliacoes;
    private String docenteResponsavel;

    public UnidadeCurricular(String nome, int ano, int ects) {
        this.nome = nome;
        this.anoCurricular = ano;
        this.ects = ects;
        this.avaliacoes = new ArrayList<>();
        this.docenteResponsavel = null;
    }

    public UnidadeCurricular(String nome, int ano, int ects, List<Avaliacao> avaliacoes, String docenteResponsavel) {
        this.nome = nome;
        this.anoCurricular = ano;
        this.ects = ects;
        this.avaliacoes = avaliacoes != null ? avaliacoes : new ArrayList<>();
        this.docenteResponsavel = docenteResponsavel;
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

    @Override
    public String toString() {
        String docente = temDocenteResponsavel() ? docenteResponsavel : "(sem docente responsável)";
        return  "=== Unidade Curricular ===\n" +
                "Nome: " + this.nome + "\n" +
                "Ano: " + this.anoCurricular + "\n" +
                "ECTS: " + this.ects + "\n" +
                "Avaliações: " + this.avaliacoes.size() + "\n" +
                "Docente: " + docente + "\n" +
                "===========================";
    }
}