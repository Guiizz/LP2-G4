package Controller;

import BLL.CursoBLL;
import Model.*;

import java.util.ArrayList;
import java.util.List;

public class CursoController {

    private final CursoBLL cursoBLL;

    public CursoController(CursoBLL cursoBLL) {
        this.cursoBLL = cursoBLL;
    }

    public Curso registarCurso(String nomeCurso, Departamento departamento) {
        return cursoBLL.registarCurso(nomeCurso, departamento);
    }

    public Curso registarCurso(String nomeCurso, Departamento departamento, double valorPropina) {
        return cursoBLL.registarCurso(nomeCurso, departamento, valorPropina);
    }

    public ArrayList<Curso> listarCursos() {
        return cursoBLL.listarCursos();
    }

    public void atualizarNomeCurso(Curso curso, String novoNome) {
        cursoBLL.atualizarNomeCurso(curso, novoNome);
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

    public void adicionarUnidadeCurricular(Curso curso, UnidadeCurricular uc, int anoCurricular) {
        cursoBLL.adicionarUnidadeCurricular(curso, uc, anoCurricular);
    }

    public void removerUnidadeCurricular(Curso curso, UnidadeCurricular uc) {
        cursoBLL.removerUnidadeCurricular(curso, uc);
    }

    public List<UnidadeCurricular> listarUCsPorAno(Curso curso, int anoCurricular) {
        return cursoBLL.listarUCsPorAno(curso, anoCurricular);
    }

    public int vagasUCsDisponiveis(Curso curso, int anoCurricular) {
        return cursoBLL.vagasUCsDisponiveis(curso, anoCurricular);
    }

    public void iniciarCurso(Curso curso, List<Estudante> estudantes) {
        cursoBLL.iniciarCurso(curso, estudantes);
    }

    public List<Estudante> listarEstudantesInscritos(Curso curso, List<Estudante> estudantes) {
        return cursoBLL.listarEstudantesInscritos(curso, estudantes);
    }

    public int contarEstudantesInscritosNoCurso(Curso curso, List<Estudante> estudantes) {
        return cursoBLL.contarEstudantesInscritosNoCurso(curso, estudantes);
    }

    public void atualizarValorPropina(Curso curso, double novoValor) {
        cursoBLL.atualizarValorPropina(curso, novoValor);
    }
}