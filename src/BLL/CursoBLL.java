package BLL;

import DAL.CursoDAL;
import Model.Curso;
import Model.Departamento;
import Model.Estudante;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Camada de Lógica de Negócio (BLL) para a entidade Curso.
 * Responsável por validar e aplicar todas as regras de negócio associadas aos cursos.
 */
public class CursoBLL {

    private CursoDAL cursoDAL;

    /** Número máximo de unidades curriculares por ano de curso. */
    private static final int MAX_UCS_POR_ANO = 5;

    /** Duração fixa de todos os cursos em anos. */
    private static final int DURACAO_CURSO = 3;

    /**
     * Construtor da classe CursoBLL.
     */
    public CursoBLL() {
        this.cursoDAL = new CursoDAL();
    }

    // -------------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------------

    /**
     * Regista um novo curso no sistema.
     * Valida o nome e verifica se já existe um curso com o mesmo nome no departamento.
     *
     * @param nomeCurso    O nome do curso.
     * @param departamento O departamento ao qual o curso pertence.
     * @return O curso criado e guardado.
     * @throws IllegalArgumentException Se o nome for inválido, o departamento for nulo
     *                                  ou já existir um curso com o mesmo nome.
     */
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

    /**
     * Lista todos os cursos registados no sistema.
     *
     * @return Uma lista com todos os cursos.
     */
    public ArrayList<Curso> listarCursos() {
        return cursoDAL.listarCursos();
    }

    /**
     * Atualiza o nome de um curso existente.
     * Não é permitido alterar o curso se já tiver estudantes ou docentes alocados.
     *
     * @param curso        O curso a atualizar.
     * @param novoNome     O novo nome do curso.
     * @param estudantes   Lista de todos os estudantes do sistema (para verificar alocações).
     * @throws IllegalArgumentException Se o curso for nulo, o nome inválido, o curso tiver
     *                                  alocados ou já existir outro curso com o mesmo nome.
     */
    public void atualizarNomeCurso(Curso curso, String novoNome, List<Estudante> estudantes) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }

        Utils.validarNome(novoNome);

        // Regra: curso com estudantes ou professores alocados não pode ser alterado
        if (temEstudantesAlocados(curso, estudantes)) {
            throw new IllegalArgumentException(
                    "Não é possível alterar o curso '" + curso.getNomeCurso() +
                            "' porque tem estudantes alocados.");
        }

        // Verificar duplicados (excluindo o próprio curso)
        Curso existente = procurarPorNome(novoNome);
        if (existente != null && existente != curso) {
            throw new IllegalArgumentException("Já existe um curso com o nome: " + novoNome);
        }

        curso.setNomeCurso(novoNome);
        cursoDAL.atualizarCurso(curso);
    }

    /**
     * Remove um curso do sistema.
     * Não é permitido remover se o curso tiver estudantes ou docentes alocados.
     *
     * @param curso      O curso a remover.
     * @param estudantes Lista de todos os estudantes do sistema.
     * @throws IllegalArgumentException Se o curso for nulo ou tiver alocados.
     */
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

    // -------------------------------------------------------------------------
    // Pesquisa
    // -------------------------------------------------------------------------

    /**
     * Procura um curso pelo nome (case-insensitive).
     *
     * @param nome O nome do curso a procurar.
     * @return O curso encontrado, ou null se não existir.
     * @throws IllegalArgumentException Se o nome for nulo ou vazio.
     */
    public Curso procurarPorNome(String nome) {
        Utils.validarNome(nome);
        for (Curso c : cursoDAL.listarCursos()) {
            if (c.getNomeCurso().equalsIgnoreCase(nome.trim())) {
                return c;
            }
        }
        return null;
    }

    /**
     * Lista todos os cursos pertencentes a um determinado departamento.
     *
     * @param departamento O departamento a filtrar.
     * @return Lista de cursos do departamento.
     * @throws IllegalArgumentException Se o departamento for nulo.
     */
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

    /**
     * Adiciona uma unidade curricular a um curso.
     * Valida a regra de máximo de 5 UCs por ano curricular.
     *
     * @param curso O curso ao qual adicionar a UC.
     * @param uc    A unidade curricular a adicionar.
     * @throws IllegalArgumentException Se o curso ou a UC forem nulos, a UC já estiver
     *                                  no curso, o ano curricular for inválido ou o limite
     *                                  de 5 UCs por ano for atingido.
     */
    public void adicionarUnidadeCurricular(Curso curso, UnidadeCurricular uc) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }
        if (uc == null) {
            throw new IllegalArgumentException("A unidade curricular não pode ser nula.");
        }

        // Validar ano curricular (1, 2 ou 3)
        if (uc.getAnoCurricular() < 1 || uc.getAnoCurricular() > DURACAO_CURSO) {
            throw new IllegalArgumentException(
                    "O ano curricular da UC deve estar entre 1 e " + DURACAO_CURSO + ".");
        }

        // Verificar se a UC já está neste curso
        if (curso.getUnidades().contains(uc)) {
            throw new IllegalArgumentException(
                    "A unidade curricular '" + uc.getNome() + "' já está registada neste curso.");
        }

        // Regra: máximo 5 UCs por ano
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

    /**
     * Lista as unidades curriculares de um curso filtradas por ano curricular.
     *
     * @param curso          O curso a consultar.
     * @param anoCurricular  O ano a filtrar (1, 2 ou 3).
     * @return Lista de UCs do ano indicado.
     * @throws IllegalArgumentException Se o curso for nulo ou o ano for inválido.
     */
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

    /**
     * Verifica se um curso tem estudantes alocados (com inscrições no curso).
     * Utilizado para impedir alterações ou remoções indevidas.
     *
     * @param curso      O curso a verificar.
     * @param estudantes Lista de todos os estudantes do sistema.
     * @return true se existir pelo menos um estudante inscrito no curso, false caso contrário.
     */
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

    /**
     * Verifica quantas vagas de UCs ainda existem num determinado ano do curso.
     *
     * @param curso         O curso a consultar.
     * @param anoCurricular O ano a verificar.
     * @return O número de vagas restantes (entre 0 e 5).
     * @throws IllegalArgumentException Se o curso for nulo ou o ano for inválido.
     */
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
}