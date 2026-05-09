package Controller;

import BLL.AvaliacaoBLL;
import DAL.AvaliacaoDAL;
import DAL.UnidadeCurricularDAL;
import Model.Avaliacao;
import Model.UnidadeCurricular;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AvaliacaoController {

    private final AvaliacaoBLL avaliacaoBLL;

    public AvaliacaoController() {
        this.avaliacaoBLL = new AvaliacaoBLL(new AvaliacaoDAL(new UnidadeCurricularDAL()));
    }

    public Avaliacao registarAvaliacao(List<UnidadeCurricular> ucs, double peso, Date data, double nota) {
        return avaliacaoBLL.registarAvaliacao(ucs, peso, data, nota);
    }

    public void atualizarAvaliacao(Avaliacao avaliacaoAntiga, List<UnidadeCurricular> novasUCs,
                                   double novoPeso, Date novaData, double novaNota) {
        avaliacaoBLL.atualizarAvaliacao(avaliacaoAntiga, novasUCs, novoPeso, novaData, novaNota);
    }

    public void removerAvaliacao(Avaliacao avaliacao) {
        avaliacaoBLL.removerAvaliacao(avaliacao);
    }

    public ArrayList<Avaliacao> listarAvaliacoes() {
        return avaliacaoBLL.listarAvaliacoes();
    }

    public ArrayList<Avaliacao> procurarPorUC(UnidadeCurricular uc) {
        return avaliacaoBLL.procurarPorUC(uc);
    }

    public ArrayList<Avaliacao> procurarPorData(Date data) {
        return avaliacaoBLL.procurarPorData(data);
    }
}