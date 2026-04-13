package Controller;

import BLL.CursoBLL;
import Model.Curso;
import Model.Departamento;
import Model.Estudante;
import Model.UnidadeCurricular;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador responsável por gerir as operações relacionadas com a entidade Curso.
 * Serve de intermediário entre a camada de apresentação e a camada de negócio.
 */
public class CursoController {

    private CursoBLL cursoBLL;

    /**
     * Construtor do CursoController.
     * Inicializa a camada de negócio do Curso.
     *
     * @param cursoBLL Instância da camada BLL.
     */
    public CursoController(CursoBLL cursoBLL) {
        this.cursoBLL = cursoBLL;
    }

    /**
     * Regista um novo curso no sistema.
     *
     * @param nomeCurso    O nome do curso.
     * @param departamento O departamento ao qual o curso pertence.
     * @return O curso criado.
     * @throws IllegalArgumentException Se o nome for inválido, o departamento for nulo
     *                                  ou já existir um curso com o mesmo nome.
     */
    public Curso registarCurso(String nomeCurso, Departamento departamento) {
        return cursoBLL.registarCurso(nomeCurso, departamento);
    }

    /**
     * Lista todos os cursos registados no sistema.
     *
     * @return Lista de cursos.
     */
    public ArrayList<Curso> listarCursos() {
        return cursoBLL.listarCursos();
    }

    /**
     * Atualiza o nome de um curso existente.
     * Não é permitido alterar se o curso já tiver estudantes alocados.
     *
     * @param curso      O curso a atualizar.
     * @param novoNome   O novo nome do curso.
     * @param estudantes Lista de todos os estudantes do sistema.
     * @throws IllegalArgumentException Se o curso for nulo, o nome inválido,
     *                                  tiver estudantes alocados ou o nome já existir.
     */
    public void atualizarNomeCurso(Curso curso, String novoNome, List<Estudante> estudantes) {
        cursoBLL.atualizarNomeCurso(curso, novoNome, estudantes);
    }

    /**
     * Remove um curso do sistema.
     * Não é permitido remover se o curso tiver estudantes alocados.
     *
     * @param curso      O curso a remover.
     * @param estudantes Lista de todos os estudantes do sistema.
     * @throws IllegalArgumentException Se o curso for nulo ou tiver estudantes alocados.
     */
    public void removerCurso(Curso curso, List<Estudante> estudantes) {
        cursoBLL.removerCurso(curso, estudantes);
    }

    /**
     * Procura um curso pelo nome (case-insensitive).
     *
     * @param nome O nome do curso a procurar.
     * @return O curso encontrado, ou null se não existir.
     * @throws IllegalArgumentException Se o nome for nulo ou vazio.
     */
    public Curso procurarPorNome(String nome) {
        return cursoBLL.procurarPorNome(nome);
    }

    /**
     * Lista todos os cursos pertencentes a um determinado departamento.
     *
     * @param departamento O departamento a filtrar.
     * @return Lista de cursos do departamento.
     * @throws IllegalArgumentException Se o departamento for nulo.
     */
    public ArrayList<Curso> listarCursosPorDepartamento(Departamento departamento) {
        return cursoBLL.listarCursosPorDepartamento(departamento);
    }

    /**
     * Adiciona uma unidade curricular a um curso.
     * Valida a regra de máximo de 5 UCs por ano curricular.
     *
     * @param curso O curso ao qual adicionar a UC.
     * @param uc    A unidade curricular a adicionar.
     * @throws IllegalArgumentException Se o curso ou UC forem nulos, a UC já estiver
     *                                  no curso, o ano for inválido ou o limite de 5 UCs
     *                                  por ano for atingido.
     */
    public void adicionarUnidadeCurricular(Curso curso, UnidadeCurricular uc) {
        cursoBLL.adicionarUnidadeCurricular(curso, uc);
    }

    /**
     * Lista as unidades curriculares de um curso filtradas por ano curricular.
     *
     * @param curso         O curso a consultar.
     * @param anoCurricular O ano a filtrar (1, 2 ou 3).
     * @return Lista de UCs do ano indicado.
     * @throws IllegalArgumentException Se o curso for nulo ou o ano for inválido.
     */
    public List<UnidadeCurricular> listarUCsPorAno(Curso curso, int anoCurricular) {
        return cursoBLL.listarUCsPorAno(curso, anoCurricular);
    }

    /**
     * Verifica se um curso tem estudantes alocados.
     *
     * @param curso      O curso a verificar.
     * @param estudantes Lista de todos os estudantes do sistema.
     * @return true se existir pelo menos um estudante inscrito no curso, false caso contrário.
     */
    public boolean temEstudantesAlocados(Curso curso, List<Estudante> estudantes) {
        return cursoBLL.temEstudantesAlocados(curso, estudantes);
    }

    /**
     * Verifica quantas vagas de UCs ainda existem num determinado ano do curso.
     *
     * @param curso         O curso a consultar.
     * @param anoCurricular O ano a verificar (1, 2 ou 3).
     * @return O número de vagas restantes (entre 0 e 5).
     * @throws IllegalArgumentException Se o curso for nulo ou o ano for inválido.
     */
    public int vagasUCsDisponiveis(Curso curso, int anoCurricular) {
        return cursoBLL.vagasUCsDisponiveis(curso, anoCurricular);
    }
}