package Controller;

import BLL.UnidadeCurricularBLL;
import DAL.UnidadeCurricularDAL;
import Model.UnidadeCurricular;

import java.util.ArrayList;

public class UnidadeCurricularController {

    private final UnidadeCurricularBLL unidadeCurricularBLL;

    public UnidadeCurricularController() {
        this.unidadeCurricularBLL = new UnidadeCurricularBLL(new UnidadeCurricularDAL());
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

    public UnidadeCurricular registarUC(String nome, int ano, int ects) {
        UnidadeCurricular uc = new UnidadeCurricular(nome, ano, ects);
        unidadeCurricularBLL.adicionarUnidade(uc);
        return uc;
    }
}