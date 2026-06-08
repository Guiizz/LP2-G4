package Controller;

import BLL.JustificacaoBLL;
import Model.JustificacaoFalta;
import Model.TipoJustificacao;

import java.time.LocalDate;
import java.util.List;

public class JustificacaoController {

    private final JustificacaoBLL justificacaoBLL;

    public JustificacaoController(JustificacaoBLL justificacaoBLL) {
        this.justificacaoBLL = justificacaoBLL;
    }

    public TipoJustificacao criarTipo(String nome, String categoria) {
        return justificacaoBLL.criarTipo(nome, categoria);
    }

    public void removerTipo(String nome) {
        justificacaoBLL.removerTipo(nome);
    }

    public List<TipoJustificacao> listarTipos() {
        return justificacaoBLL.listarTipos();
    }

    public JustificacaoFalta pedirJustificacao(String numMecanografico, String nomeUC, String nomeCurso, int anoLetivo, LocalDate dataAula, String horaInicio, String nomeTipo) {
        return justificacaoBLL.pedirJustificacao(numMecanografico, nomeUC, nomeCurso,
                anoLetivo, dataAula, horaInicio, nomeTipo);
    }

    public void aprovarJustificacao(JustificacaoFalta justificacao) {
        justificacaoBLL.aprovarJustificacao(justificacao);
    }

    public void rejeitarJustificacao(JustificacaoFalta justificacao) {
        justificacaoBLL.rejeitarJustificacao(justificacao);
    }

    public List<JustificacaoFalta> listarPendentes() {
        return justificacaoBLL.listarPendentes();
    }

    public List<JustificacaoFalta> listarPorEstudante(String numMecanografico) {
        return justificacaoBLL.listarPorEstudante(numMecanografico);
    }

    public List<JustificacaoFalta> listarTodas() {
        return justificacaoBLL.listarTodas();
    }
}
