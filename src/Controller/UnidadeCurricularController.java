package Controller;

import BLL.UnidadeCurricularBLL;
import Model.UnidadeCurricular;

import java.util.ArrayList;

public class UnidadeCurricularController {

    private final UnidadeCurricularBLL unidadeCurricularBLL;

    public UnidadeCurricularController(UnidadeCurricularBLL unidadeCurricularBLL) {
        this.unidadeCurricularBLL = unidadeCurricularBLL;
    }

    public void adicionarUnidade(UnidadeCurricular unidade) {
        unidadeCurricularBLL.adicionarUnidade(unidade);
    }

    public ArrayList<UnidadeCurricular> listarUnidades() {
        return unidadeCurricularBLL.listarUnidades();
    }

    public void atualizarUnidade(UnidadeCurricular unidade) {
        unidadeCurricularBLL.atualizarUnidade(unidade);
    }

    public void removerUnidade(UnidadeCurricular unidade) {
        unidadeCurricularBLL.removerUnidade(unidade);
    }

    public void atribuirDocenteResponsavel(String nomeUC, String siglaDocente) {
        unidadeCurricularBLL.atribuirDocenteResponsavel(nomeUC, siglaDocente);
    }

    public void adicionarMomento(UnidadeCurricular uc, String nome, double peso) {
        unidadeCurricularBLL.adicionarMomento(uc, nome, peso);
    }

    public void removerMomento(UnidadeCurricular uc, int indice) {
        unidadeCurricularBLL.removerMomento(uc, indice);
    }

    public void iniciarUC(UnidadeCurricular uc) {
        unidadeCurricularBLL.iniciarUC(uc);
    }
}