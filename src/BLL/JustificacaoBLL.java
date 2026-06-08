package BLL;

import DAL.JustificacaoDAL;
import DAL.TipoJustificacaoDAL;
import Model.JustificacaoFalta;
import Model.TipoJustificacao;

import java.time.LocalDate;
import java.util.List;

public class JustificacaoBLL {

    private final JustificacaoDAL justificacaoDAL;
    private final TipoJustificacaoDAL tipoDAL;

    public JustificacaoBLL(JustificacaoDAL justificacaoDAL, TipoJustificacaoDAL tipoDAL) {
        this.justificacaoDAL = justificacaoDAL;
        this.tipoDAL = tipoDAL;
    }

    // ── Tipos de Justificação ─────────────────────────────────────────────────

    public TipoJustificacao criarTipo(String nome, String categoria) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do tipo não pode ser vazio.");
        }
        if (!TipoJustificacao.CATEGORIA_SAUDE.equals(categoria)
                && !TipoJustificacao.CATEGORIA_ESTATUTO.equals(categoria)) {
            throw new IllegalArgumentException(
                    "Categoria inválida. Use '" + TipoJustificacao.CATEGORIA_SAUDE
                            + "' ou '" + TipoJustificacao.CATEGORIA_ESTATUTO + "'.");
        }
        if (tipoDAL.procurarPorNome(nome) != null) {
            throw new IllegalArgumentException("Já existe um tipo com esse nome.");
        }
        TipoJustificacao tipo = new TipoJustificacao(nome, categoria);
        tipoDAL.adicionar(tipo);
        return tipo;
    }

    public void removerTipo(String nome) {
        if (tipoDAL.procurarPorNome(nome) == null) {
            throw new IllegalArgumentException("Tipo de justificação não encontrado.");
        }
        tipoDAL.remover(nome);
    }

    public List<TipoJustificacao> listarTipos() {
        return tipoDAL.listarTodos();
    }

    // ── Justificações de Falta ────────────────────────────────────────────────

    public JustificacaoFalta pedirJustificacao(String numMecanografico, String nomeUC,
                                               String nomeCurso, int anoLetivo,
                                               LocalDate dataAula, String horaInicio,
                                               String nomeTipo) {
        if (tipoDAL.procurarPorNome(nomeTipo) == null) {
            throw new IllegalArgumentException("Tipo de justificação inválido: " + nomeTipo);
        }
        for (JustificacaoFalta j : justificacaoDAL.listarPorEstudante(numMecanografico)) {
            if (j.getNomeUC().equalsIgnoreCase(nomeUC)
                    && j.getNomeCurso().equalsIgnoreCase(nomeCurso)
                    && j.getAnoLetivo() == anoLetivo
                    && j.getDataAula().equals(dataAula)
                    && j.getHoraInicio().equals(horaInicio)) {
                throw new IllegalArgumentException(
                        "Já existe um pedido de justificação para esta falta.");
            }
        }
        JustificacaoFalta justificacao = new JustificacaoFalta(
                numMecanografico, nomeUC, nomeCurso, anoLetivo, dataAula, horaInicio, nomeTipo);
        justificacaoDAL.adicionar(justificacao);
        return justificacao;
    }

    public void aprovarJustificacao(JustificacaoFalta justificacao) {
        if (!JustificacaoFalta.PENDENTE.equals(justificacao.getEstado())) {
            throw new IllegalArgumentException("Só é possível aprovar justificações pendentes.");
        }
        justificacao.setEstado(JustificacaoFalta.APROVADA);
        justificacaoDAL.atualizar(justificacao);
    }

    public void rejeitarJustificacao(JustificacaoFalta justificacao) {
        if (!JustificacaoFalta.PENDENTE.equals(justificacao.getEstado())) {
            throw new IllegalArgumentException("Só é possível rejeitar justificações pendentes.");
        }
        justificacao.setEstado(JustificacaoFalta.REJEITADA);
        justificacaoDAL.atualizar(justificacao);
    }

    public List<JustificacaoFalta> listarPendentes() {
        return justificacaoDAL.listarPendentes();
    }

    public List<JustificacaoFalta> listarPorEstudante(String numMecanografico) {
        return justificacaoDAL.listarPorEstudante(numMecanografico);
    }

    public List<JustificacaoFalta> listarTodas() {
        return justificacaoDAL.listarTodas();
    }
}
