package Controller;

import BLL.EstudanteBLL;
import DAL.DocenteDAL;
import DAL.EstudanteDAL;
import DAL.UnidadeCurricularDAL;
import Model.Curso;
import Model.Estudante;
import Model.Inscricao;

import java.util.ArrayList;

public class InscricaoController {

    private final EstudanteBLL estudanteBLL;

    public InscricaoController() {
        this.estudanteBLL = new EstudanteBLL(new EstudanteDAL(), new DocenteDAL(new UnidadeCurricularDAL()));
    }

    public Inscricao inscreverEstudante(Estudante estudante, Curso curso, int anoLetivo) {
        if (estudante == null) throw new IllegalArgumentException("O estudante não pode ser nulo.");
        if (curso == null) throw new IllegalArgumentException("O curso não pode ser nulo.");
        if (estudante.getInscricoes() != null && !estudante.getInscricoes().isEmpty())
            throw new IllegalArgumentException("O estudante já está associado a um curso.");

        Inscricao inscricao = new Inscricao(anoLetivo, 1, curso);
        estudante.adicionarInscricao(inscricao);
        return inscricao;
    }

    public void verificarProgressaoAno(Estudante estudante) {
        estudanteBLL.podeProgredirAno(estudante);
    }

    public void passarDeAno(Estudante estudante, Inscricao novaInscricao) {
        estudanteBLL.passarDeAno(estudante, novaInscricao);
    }

    public ArrayList<Inscricao> listarInscricoes(Estudante estudante) {
        if (estudante == null) throw new IllegalArgumentException("O estudante não pode ser nulo.");
        return estudante.getInscricoes();
    }

    public Inscricao obterInscricaoAtual(Estudante estudante) {
        if (estudante == null) throw new IllegalArgumentException("O estudante não pode ser nulo.");
        ArrayList<Inscricao> inscricoes = estudante.getInscricoes();
        return inscricoes.isEmpty() ? null : inscricoes.get(inscricoes.size() - 1);
    }

    public boolean estaInscritoNoCurso(Estudante estudante, Curso curso) {
        if (estudante == null) throw new IllegalArgumentException("O estudante não pode ser nulo.");
        if (curso == null)     throw new IllegalArgumentException("O curso não pode ser nulo.");
        for (Inscricao inscricao : estudante.getInscricoes()) {
            if (inscricao.getCurso() != null && inscricao.getCurso().equals(curso)) return true;
        }
        return false;
    }
}