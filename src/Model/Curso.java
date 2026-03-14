package Model;

import java.util.ArrayList;

public class Curso {

    private String nomeCurso;

    private Departamento departamento;

    private int duracao;

    private List <UnidadeCurricular> unidades;


    public Curso(String nomeCurso, Departamento departamento, int duracao, List<UnidadeCurricular> unidades) {
        this.nomeCurso = nomeCurso;
        this.departamento = departamento;
        this.duracao = 3;
        this.unidades = new ArrayList<>();
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public List<UnidadeCurricular> getUnidades() {
        return unidades;
    }
}
