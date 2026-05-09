package Controller;

import BLL.CursoBLL;
import DAL.CursoDAL;
import DAL.DepartamentoDAL;
import DAL.EstudanteDAL;
import DAL.UnidadeCurricularDAL;
import Model.Curso;
import Model.Departamento;
import Model.Estudante;
import Model.UnidadeCurricular;

import java.util.ArrayList;
import java.util.List;

public class CursoController {

    private final CursoBLL cursoBLL;

    public CursoController() {
        UnidadeCurricularDAL ucDAL = new UnidadeCurricularDAL();
        DepartamentoDAL      depDAL = new DepartamentoDAL();
        this.cursoBLL = new CursoBLL(
                new CursoDAL(depDAL, ucDAL),
                new EstudanteDAL()
        );
    }

    public Curso registarCurso(String nomeCurso, Departamento departamento) {
        return cursoBLL.registarCurso(nomeCurso, departamento);
    }

    public ArrayList<Curso> listarCursos() {
        return cursoBLL.listarCursos();
    }

    public void atualizarNomeCurso(Curso curso, String novoNome, List<Estudante> estudantes) {
        cursoBLL.atualizarNomeCurso(curso, novoNome, estudantes);
    }

    public void atualizarNomeCurso(Curso curso, String novoNome) {
        cursoBLL.atualizarNomeCurso(curso, novoNome);
    }

    public void removerCurso(Curso curso, List<Estudante> estudantes) {
        cursoBLL.removerCurso(curso, estudantes);
    }

    public void removerCurso(Curso curso) {
        cursoBLL.removerCurso(curso);
    }

    public Curso procurarPorNome(String nome) {
        return cursoBLL.procurarPorNome(nome);
    }

    public ArrayList<Curso> listarCursosPorDepartamento(Departamento departamento) {
        return cursoBLL.listarCursosPorDepartamento(departamento);
    }

    public void adicionarUnidadeCurricular(Curso curso, UnidadeCurricular uc) {
        cursoBLL.adicionarUnidadeCurricular(curso, uc);
    }

    public List<UnidadeCurricular> listarUCsPorAno(Curso curso, int anoCurricular) {
        return cursoBLL.listarUCsPorAno(curso, anoCurricular);
    }

    public boolean temEstudantesAlocados(Curso curso, List<Estudante> estudantes) {
        return cursoBLL.temEstudantesAlocados(curso, estudantes);
    }

    public int vagasUCsDisponiveis(Curso curso, int anoCurricular) {
        return cursoBLL.vagasUCsDisponiveis(curso, anoCurricular);
    }

    public void iniciarCurso(Curso curso, List<Estudante> estudantes) {
        cursoBLL.iniciarCurso(curso, estudantes);
    }

    public int contarEstudantesInscritosNoCurso(Curso curso, List<Estudante> estudantes) {
        return cursoBLL.contarEstudantesInscritosNoCurso(curso, estudantes);
    }
}