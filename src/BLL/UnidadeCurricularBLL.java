package BLL;

import DAL.UnidadeCurricularDAL;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;

public class UnidadeCurricularBLL {
    private UnidadeCurricularDAL unidadeCurricularDAL;

    public UnidadeCurricularBLL() {
        unidadeCurricularDAL = new UnidadeCurricularDAL();
    }

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

}
