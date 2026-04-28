package Controller;

import BLL.UnidadeCurricularBLL;
import Model.UnidadeCurricular;

import java.util.ArrayList;

/**
 * Controlador responsável por gerir as operações relacionadas com a entidade UnidadeCurricular.
 * Serve de intermediário entre a camada de apresentação e a camada de negócio.
 */
public class UnidadeCurricularController {

    private UnidadeCurricularBLL unidadeCurricularBLL;

    /**
     * Construtor do UnidadeCurricularController.
     * Inicializa a camada de negócio das Unidades Curriculares.
     *
     * @param unidadeCurricularBLL Instância da camada BLL.
     */
    public UnidadeCurricularController(UnidadeCurricularBLL unidadeCurricularBLL) {
        this.unidadeCurricularBLL = unidadeCurricularBLL;
    }

    /**
     * Adiciona uma nova Unidade Curricular ao sistema.
     *
     * @param unidade A unidade curricular a adicionar.
     * @throws IllegalArgumentException Se os dados forem inválidos.
     */
    public void adicionarUnidade(UnidadeCurricular unidade) {
        unidadeCurricularBLL.adicionarUnidade(unidade);
    }

    /**
     * Lista todas as Unidades Curriculares registadas no sistema.
     *
     * @return Lista de unidades curriculares.
     */
    public ArrayList<UnidadeCurricular> listarUnidades() {
        return unidadeCurricularBLL.listarUnidades();
    }

    /**
     * Atualiza os dados de uma Unidade Curricular existente.
     *
     * @param unidade A unidade curricular com os dados atualizados.
     * @throws IllegalArgumentException Se a unidade não existir ou os dados forem inválidos.
     */
    public void atualizarUnidade(UnidadeCurricular unidade) {
        unidadeCurricularBLL.atualizarUnidade(unidade);
    }

    /**
     * Remove uma Unidade Curricular do sistema.
     *
     * @param unidade A unidade curricular a remover.
     * @throws IllegalArgumentException Se a unidade for inválida ou tiver avaliações associadas.
     */
    public void removerUnidade(UnidadeCurricular unidade) {
        unidadeCurricularBLL.removerUnidade(unidade);
    }

    /**
     * Atribui um Docente Responsável a uma Unidade Curricular.
     *
     * @param nomeUC Nome da Unidade Curricular.
     * @param siglaDocente Sigla do docente a atribuir como responsável.
     * @throws IllegalArgumentException Se a UC não existir ou a sigla for inválida.
     */
    public void atribuirDocenteResponsavel(String nomeUC, String siglaDocente) {
        unidadeCurricularBLL.atribuirDocenteResponsavel(nomeUC, siglaDocente);
    }
}