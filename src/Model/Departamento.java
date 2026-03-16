package Model;

import java.util.ArrayList;
import java.util.List;

public class Departamento {
    private String nome;
    private String sigla;
    private List<Curso> cursos;

    public Departamento(String nome, String sigla) {
        this.nome = nome;
        this.sigla = sigla;
        this.cursos = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public String getSigla() {
        return sigla;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public void adicionarCurso(Curso curso){
        if(curso != null && !cursos.contains(curso)){
            cursos.add(curso);
        }
    }

    @Override
    public String toString() {
        return "==== Departamento ====\n" +
                "Nome: " + nome + "\n" +
                "Sigla: " + sigla + "\n" +
                "Cursos: " + cursos.size() + "\n" +
                "=====================\n";
    }
}
