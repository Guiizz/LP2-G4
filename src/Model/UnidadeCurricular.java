package Model;

import java.util.ArrayList;
import java.util.List;

public class UnidadeCurricular {

    /**
     * Atributos
     */
    private String nome;
    private int anoCurricular;
    private int ects;
    private List<Avaliacao> avaliacoes;
    private Docente docenteResponsavel;
    /**
     * Construtor
     * @param nome
     * @param ano
     * @param avaliacoes
     */
    public UnidadeCurricular(String nome, int ano, int ects, List<Avaliacao> avaliacoes) {
        this.nome = nome;
        this.anoCurricular = ano;
        this.ects = ects;
        this.avaliacoes = avaliacoes;
    }

    /**
     * Gets e Sets
     * @return
     */
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

    public int getEts() {return ects;}

    public void setEts(int ects) {this.ects = ects;}

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(List<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

//GETTER e SETTER do DocenteResponsavel
    public Docente getDocenteResponsavel() {
        return docenteResponsavel;
    }

    public void setDocenteResponsavel(Docente d) {
        this.docenteResponsavel = d;
    }

    public boolean isDocenteResponsavel(Docente docente) {
        if (this.docenteResponsavel == null || docente == null) {
            return false;
        }
        return this.docenteResponsavel.equals(docente);
    }

    @Override
    public String toString() {
        return  "=== Unidade Curricular ===\n" +
                "Nome: " + this.nome + "\n" +
                "Ano: " + this.anoCurricular + "\n" +
                "Avaliações: " + this.avaliacoes + "\n" +
                "===========================";
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UnidadeCurricular uc = (UnidadeCurricular) obj;
        return this.nome.equalsIgnoreCase(uc.nome);
    }
}