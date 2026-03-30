package DAL;

import Model.Curso;
import java.util.ArrayList;

public class CursoDAL {
    private ArrayList<Curso> cursos;

    public CursoDAL() {
        cursos = new ArrayList<>();
    }

    public void adicionarCurso(Curso curso) {
        cursos.add(curso);
    }

    public boolean atualizarCurso(Curso cursoatualizado) {
        for (int i = 0; i < cursos.size(); i++) {
            if (cursos.get(i).equals(cursoatualizado)){
                cursos.set(i, cursoatualizado);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Curso> listarCursos() {
        return new ArrayList<>(cursos);
    }

    public void removerCurso(Curso curso) {
        cursos.remove(curso);
    }
}
