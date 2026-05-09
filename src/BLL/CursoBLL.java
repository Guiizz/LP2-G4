package BLL;

import DAL.CursoDAL;
import DAL.EstudanteDAL;
import Model.*;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Camada de Lógica de Negócio (BLL) para a entidade Curso.
 * Responsável por validar e aplicar todas as regras de negócio associadas aos cursos.
 */
public class CursoBLL {

    private CursoDAL    cursoDAL;
    private EstudanteDAL estudanteDAL;

    private static final int QUORUM_MINIMO   = 5;
    private static final int MAX_UCS_POR_ANO = 5;
    private static final int DURACAO_CURSO   = 3;

    public CursoBLL(CursoDAL cursoDAL, EstudanteDAL estudanteDAL) {
        this.cursoDAL     = cursoDAL;
        this.estudanteDAL = estudanteDAL;
    }

    // -------------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------------

    public Curso registarCurso(String nomeCurso, Departamento departamento) {
        Utils.validarNome(nomeCurso);

        if (departamento == null) {
            throw new IllegalArgumentException("O departamento não pode ser nulo.");
        }

        if (procurarPorNome(nomeCurso) != null) {
            throw new IllegalArgumentException("Já existe um curso com o nome: " + nomeCurso);
        }

        Curso novoCurso = new Curso(nomeCurso, departamento);
        cursoDAL.adicionarCurso(novoCurso);
        return novoCurso;
    }

    public ArrayList<Curso> listarCursos() {
        return cursoDAL.listarCursos();
    }

    /** Com lista externa — mantido para compatibilidade. */
    public void atualizarNomeCurso(Curso curso, String novoNome, List<Estudante> estudantes) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }
        Utils.validarNome(novoNome);

        if (temEstudantesAlocados(curso, estudantes)) {
            throw new IllegalArgumentException(
                    "Não é possível alterar o curso '" + curso.getNomeCurso() +
                            "' porque tem estudantes alocados.");
        }

        Curso existente = procurarPorNome(novoNome);
        if (existente != null && existente != curso) {
            throw new IllegalArgumentException("Já existe um curso com o nome: " + novoNome);
        }

        curso.setNomeCurso(novoNome);
        cursoDAL.atualizarCurso(curso);
    }

    /** Sem lista externa — obtém estudantes internamente. */
    public void atualizarNomeCurso(Curso curso, String novoNome) {
        atualizarNomeCurso(curso, novoNome, estudanteDAL.listarEstudantes());
    }

    /** Com lista externa — mantido para compatibilidade. */
    public void removerCurso(Curso curso, List<Estudante> estudantes) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }

        if (temEstudantesAlocados(curso, estudantes)) {
            throw new IllegalArgumentException(
                    "Não é possível remover o curso '" + curso.getNomeCurso() +
                            "' porque tem estudantes alocados.");
        }

        cursoDAL.removerCurso(curso);
    }

    /** Sem lista externa — obtém estudantes internamente. */
    public void removerCurso(Curso curso) {
        removerCurso(curso, estudanteDAL.listarEstudantes());
    }

    // -------------------------------------------------------------------------
    // Pesquisa
    // -------------------------------------------------------------------------

    public Curso procurarPorNome(String nome) {
        Utils.validarNome(nome);
        for (Curso c : cursoDAL.listarCursos()) {
            if (c.getNomeCurso().equalsIgnoreCase(nome.trim())) {
                return c;
            }
        }
        return null;
    }

    public ArrayList<Curso> listarCursosPorDepartamento(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("O departamento não pode ser nulo.");
        }
        ArrayList<Curso> resultado = new ArrayList<>();
        for (Curso c : cursoDAL.listarCursos()) {
            if (c.getDepartamento().equals(departamento)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    // -------------------------------------------------------------------------
    // Unidades Curriculares
    // -------------------------------------------------------------------------

    public void adicionarUnidadeCurricular(Curso curso, UnidadeCurricular uc) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }
        if (uc == null) {
            throw new IllegalArgumentException("A unidade curricular não pode ser nula.");
        }

        if (uc.getAnoCurricular() < 1 || uc.getAnoCurricular() > DURACAO_CURSO) {
            throw new IllegalArgumentException(
                    "O ano curricular da UC deve estar entre 1 e " + DURACAO_CURSO + ".");
        }

        if (curso.getUnidades().contains(uc)) {
            throw new IllegalArgumentException(
                    "A unidade curricular '" + uc.getNome() + "' já está registada neste curso.");
        }

        long ucsNesteAno = curso.getUnidades().stream()
                .filter(u -> u.getAnoCurricular() == uc.getAnoCurricular())
                .count();

        if (ucsNesteAno >= MAX_UCS_POR_ANO) {
            throw new IllegalArgumentException(
                    "O curso já atingiu o limite de " + MAX_UCS_POR_ANO +
                            " unidades curriculares para o ano " + uc.getAnoCurricular() + ".");
        }

        curso.adicionarUnidadeCurricular(uc);
        cursoDAL.atualizarCurso(curso);
    }

    public List<UnidadeCurricular> listarUCsPorAno(Curso curso, int anoCurricular) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }
        if (anoCurricular < 1 || anoCurricular > DURACAO_CURSO) {
            throw new IllegalArgumentException(
                    "O ano curricular deve estar entre 1 e " + DURACAO_CURSO + ".");
        }

        List<UnidadeCurricular> resultado = new ArrayList<>();
        for (UnidadeCurricular uc : curso.getUnidades()) {
            if (uc.getAnoCurricular() == anoCurricular) {
                resultado.add(uc);
            }
        }
        return resultado;
    }

    // -------------------------------------------------------------------------
    // Regras de negócio auxiliares
    // -------------------------------------------------------------------------

    public boolean temEstudantesAlocados(Curso curso, List<Estudante> estudantes) {
        if (estudantes == null || estudantes.isEmpty()) {
            return false;
        }
        for (Estudante e : estudantes) {
            for (var inscricao : e.getInscricoes()) {
                if (inscricao.getCurso() != null && inscricao.getCurso().equals(curso)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int vagasUCsDisponiveis(Curso curso, int anoCurricular) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }
        if (anoCurricular < 1 || anoCurricular > DURACAO_CURSO) {
            throw new IllegalArgumentException(
                    "O ano curricular deve estar entre 1 e " + DURACAO_CURSO + ".");
        }

        long ucsNesteAno = curso.getUnidades().stream()
                .filter(u -> u.getAnoCurricular() == anoCurricular)
                .count();

        return (int) (MAX_UCS_POR_ANO - ucsNesteAno);
    }

    public void iniciarCurso(Curso curso, List<Estudante> estudantes) {
        if (curso == null) {
            throw new IllegalArgumentException("Curso não encontrado.");
        }
        if (estudantes == null) {
            throw new IllegalArgumentException("Lista de estudantes inválida.");
        }
        if (curso.getEstado() == null) {
            curso.setEstado("PENDENTE");
        }
        if (!curso.getEstado().equalsIgnoreCase("PENDENTE")) {
            throw new IllegalArgumentException("Só é possível iniciar cursos no estado PENDENTE.");
        }

        int numeroInscritos = contarEstudantesInscritosNoCurso(curso, estudantes);
        if (numeroInscritos < QUORUM_MINIMO) {
            throw new IllegalArgumentException("Número mínimo de 5 estudantes não atingido.");
        }

        curso.setEstado("ATIVO");
        cursoDAL.atualizarCurso(curso);
    }

    public int contarEstudantesInscritosNoCurso(Curso curso, List<Estudante> estudantes) {
        if (curso == null || estudantes == null) {
            return 0;
        }
        int contador = 0;
        for (Estudante estudante : estudantes) {
            if (estudante.getInscricoes() == null) continue;
            for (Inscricao inscricao : estudante.getInscricoes()) {
                if (inscricao.getCurso() != null &&
                        inscricao.getCurso().getNomeCurso().equalsIgnoreCase(curso.getNomeCurso())) {
                    contador++;
                    break;
                }
            }
        }
        return contador;
    }
}