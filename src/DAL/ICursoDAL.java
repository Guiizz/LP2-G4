package DAL;

import Model.Curso;

import java.util.ArrayList;

/**
 * Contrato de acesso a dados de Curso, comum às implementações
 * em ficheiro/memória (CursoDAL) e em base de dados (DAL.BD.CursoDAL_BD).
 */
public interface ICursoDAL {

    void adicionarCurso(Curso curso);

    boolean atualizarCurso(Curso cursoAtualizado);

    ArrayList<Curso> listarCursos();

    void removerCurso(Curso curso);

    Curso procurarPorNome(String nomeCurso);
}
