package BLL;

import DAL.UnidadeCurricularDAL;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;

public class UnidadeCurricularBLL {
    private UnidadeCurricularDAL unidadeCurricularDAL;

    public UnidadeCurricularBLL(UnidadeCurricularDAL unidadeCurricularDAL){
        this.unidadeCurricularDAL = unidadeCurricularDAL;
    }

    public void adicionarUnidade(UnidadeCurricular unidade){
        if (unidade == null) {
            throw new IllegalArgumentException("A Unidade Curricular não pode ser nula.");
        }
        Utils.validarNome(unidade.getNome());

        if (unidade.getEts() <= 0) {
            throw new IllegalArgumentException("Os ETCS devem ser um valor positivo.");
        }
        if (unidade.getAnoCurricular() < 1 || unidade.getAnoCurricular() > 3){
            throw new IllegalArgumentException("Ano curricular invalido, deve ser entre 1 e 3.");
        }
        unidadeCurricularDAL.adicionarUnidade(unidade);
    }

    public ArrayList<UnidadeCurricular> listarUnidades(){
        return unidadeCurricularDAL.listarUnidades();
    }

    public void atualizarUnidade(UnidadeCurricular unidade){
        if (unidade == null) {
            throw new IllegalArgumentException("A Unidade Curricular não pode ser nula.");
        }
        Utils.validarNome(unidade.getNome());

        boolean atualizado = unidadeCurricularDAL.atualizarUnidade(unidade);
        if(!atualizado) {
            throw new IllegalArgumentException("Não foi possivel encontrar a UC para atualizar.");
        }
    }

    public void removerUnidade(UnidadeCurricular unidade){
        if (unidade == null) {
            throw new IllegalArgumentException("A Unidade Curricular não pode ser nula.");
        }

        if (!unidade.getAvaliacoes().isEmpty()){
            throw new IllegalArgumentException("Não é possivel remover uma UC que já possui avaliações registadas.");
        }
        unidadeCurricularDAL.removerUnidade(unidade);
    }

    /**
     * Atribui um Docente Responsável a uma UC.
     * @param nomeUC Nome da Unidade Curricular.
     * @param siglaDocente Sigla do docente a atribuir.
     * @throws IllegalArgumentException Se a UC não existir ou a sigla for inválida.
     */
    public void atribuirDocenteResponsavel(String nomeUC, String siglaDocente) {
        if (nomeUC == null || nomeUC.isBlank()) {
            throw new IllegalArgumentException("O nome da UC não pode ser vazio.");
        }
        if (siglaDocente == null || siglaDocente.isBlank()) {
            throw new IllegalArgumentException("A sigla do docente não pode ser vazia.");
        }
        boolean sucesso = unidadeCurricularDAL.atribuirDocenteResponsavel(nomeUC, siglaDocente);
        if (!sucesso) {
            throw new IllegalArgumentException("Unidade Curricular '" + nomeUC + "' não encontrada.");
        }
    }

}
