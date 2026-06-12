package Controller;

import BLL.AvaliacaoBLL;
import Model.Avaliacao;
import Model.UnidadeCurricular;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AvaliacaoController {

    private final AvaliacaoBLL avaliacaoBLL;

    public AvaliacaoController(AvaliacaoBLL avaliacaoBLL) {
        this.avaliacaoBLL = avaliacaoBLL;
    }

    public Avaliacao registarAvaliacao(List<UnidadeCurricular> ucs, String nomeCurso, Date data, double nota) {
        return avaliacaoBLL.registarAvaliacao(ucs, nomeCurso, data, nota);
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
}