package BLL;

import DAL.IAnoLetivoDAL;
import Model.*;
import DAL.HistoricoAnoLetivoDAL;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnoLetivoBLL {
    private final IAnoLetivoDAL anoLetivoDAL;
    private final HistoricoAnoLetivoDAL historicoAnoLetivoDAL;

    public AnoLetivoBLL(IAnoLetivoDAL anoLetivoDAL) {
        this.anoLetivoDAL = anoLetivoDAL;
        this.historicoAnoLetivoDAL = new HistoricoAnoLetivoDAL();
    }

    public AnoLetivo consultarAnoAtual()    { return anoLetivoDAL.procurarAnoAberto(); }
    public AnoLetivo consultarMaisRecente() { return anoLetivoDAL.procurarMaisRecente(); }
    public java.util.ArrayList<AnoLetivo> listarTodos() { return anoLetivoDAL.listarAnosLetivos(); }

    public void removerAnoLetivo(int ano) {
        AnoLetivo alvo = anoLetivoDAL.procurarPorAno(ano);
        if (alvo == null)
            throw new IllegalArgumentException("Ano letivo " + ano + "/" + (ano+1) + " nao encontrado.");
        if (alvo.isAberto())
            throw new IllegalArgumentException("Nao e possivel remover o ano letivo " + alvo.getDesignacao() + " porque esta aberto. Feche-o primeiro.");
        AnoLetivo mr = anoLetivoDAL.procurarMaisRecente();
        if (mr != null && mr.getAno() != ano)
            throw new IllegalArgumentException("So e possivel remover o ano letivo mais recente (" + mr.getDesignacao() + ") para preservar o historico.");
        anoLetivoDAL.removerAnoLetivo(ano);
    }

    /** Devolve o histórico de fechos de anos letivos (cada registo é uma linha do CSV). */
    public java.util.List<String[]> listarHistorico() {
        return historicoAnoLetivoDAL.listarHistorico();
    }

    public AnoLetivo abrirAnoLetivo(int ano) {
        if (ano < 2000) throw new IllegalArgumentException("Ano letivo invalido.");
        if (anoLetivoDAL.procurarAnoAberto() != null) throw new IllegalArgumentException("Ja existe um ano letivo aberto.");
        if (anoLetivoDAL.procurarPorAno(ano) != null) throw new IllegalArgumentException("Ja existe um registo para o ano letivo " + ano + "/" + (ano+1) + ".");
        AnoLetivo al = new AnoLetivo(ano, LocalDate.now());
        anoLetivoDAL.adicionarAnoLetivo(al);
        return al;
    }

    // =========================================================================
    // Fecho de Ano Letivo - path BD (leitura SQL estruturada)
    // =========================================================================

    public RelatorioFechoAnoLetivo fecharAnoAtual() {
        AnoLetivo anoAberto = anoLetivoDAL.procurarAnoAberto();
        if (anoAberto == null)
            throw new IllegalArgumentException("Nao existe ano letivo aberto para fechar.");

        ArrayList<DadosEstudanteFecho> dados = anoLetivoDAL.carregarDadosParaFecho(anoAberto.getAno());

        RelatorioFechoAnoLetivo relatorio = new RelatorioFechoAnoLetivo();
        List<String> numMecsAvancar  = new ArrayList<>();
        List<String> numMecsConcluir = new ArrayList<>();

        for (DadosEstudanteFecho d : dados) {
            TipoResultadoFecho r = processarDadosEstudante(d, relatorio);
            if      (r == TipoResultadoFecho.AVANCAR)  numMecsAvancar.add(d.getNumMecanografico());
            else if (r == TipoResultadoFecho.CONCLUIR) numMecsConcluir.add(d.getNumMecanografico());
        }

        anoLetivoDAL.persistirResultadoFecho(anoAberto.getAno(), numMecsAvancar, numMecsConcluir);

        anoAberto.fechar(LocalDate.now());
        anoLetivoDAL.atualizarAnoLetivo(anoAberto);

        String caminho = historicoAnoLetivoDAL.exportarFecho(anoAberto, relatorio);
        relatorio.setCaminhoFicheiroHistorico(caminho);
        return relatorio;
    }

    // =========================================================================
    // Fecho de Ano Letivo - path CSV (objetos em memoria)
    // =========================================================================

    public RelatorioFechoAnoLetivo fecharAnoAtual(List<Estudante> estudantes) {
        AnoLetivo anoAtual = anoLetivoDAL.procurarAnoAberto();
        if (anoAtual == null)   throw new IllegalArgumentException("Nao existe ano letivo aberto para fechar.");
        if (estudantes == null) throw new IllegalArgumentException("Lista de estudantes invalida.");

        RelatorioFechoAnoLetivo relatorio = new RelatorioFechoAnoLetivo();
        for (Estudante e : estudantes) processarEstudanteNoFecho(e, anoAtual, relatorio);

        anoAtual.fechar(LocalDate.now());
        anoLetivoDAL.atualizarAnoLetivo(anoAtual);

        String caminho = historicoAnoLetivoDAL.exportarFecho(anoAtual, relatorio);
        relatorio.setCaminhoFicheiroHistorico(caminho);
        return relatorio;
    }

    // =========================================================================
    // Auxiliares - detalhe por estudante (para o histórico)
    // =========================================================================

    /** Resumo da propina: pago/total e estado. */
    private String resumoPropina(Inscricao insc) {
        Propina p = insc.getPropina();
        if (p == null) return "propina n/d";
        return String.format("propina %.0f/%.0f%s", p.getValorPago(), p.getValorTotal(),
                p.isTotalmentePaga() ? " (paga)" : " (em divida)");
    }

    /** Resumo das notas por UC: "Estatistica=12.0, Matematica=15.0" (P = pendente). */
    private String resumoNotas(Inscricao insc) {
        if (insc.getAvaliacoes() == null || insc.getAvaliacoes().isEmpty()) return "sem notas";
        StringBuilder sb = new StringBuilder();
        for (Avaliacao av : insc.getAvaliacoes()) {
            String nomeUC = (av.getUc() != null && !av.getUc().isEmpty()) ? av.getUc().get(0).getNome() : "?";
            if (sb.length() > 0) sb.append(", ");
            sb.append(nomeUC).append("=").append(av.isLancada() ? String.format("%.1f", av.getNota()) : "P");
        }
        return sb.toString();
    }

    /** Linha detalhada de um estudante no fecho. */
    private String detalhar(Estudante estudante, Inscricao insc, String resultado) {
        return estudante.getNome()
                + " — " + resumoPropina(insc)
                + " — Notas: " + resumoNotas(insc)
                + " — " + resultado;
    }

    // =========================================================================
    // Auxiliares - path BD
    // =========================================================================

    private enum TipoResultadoFecho { MANTER, AVANCAR, CONCLUIR }

    private TipoResultadoFecho processarDadosEstudante(DadosEstudanteFecho d, RelatorioFechoAnoLetivo relatorio) {
        if (!d.isPropinaPaga()) {
            relatorio.incrementarMantidos();
            relatorio.adicionarMensagem(d.getNome() + " mantido: propina do ano atual nao esta paga.");
            return TipoResultadoFecho.MANTER;
        }
        if (d.temNotasPorLancar()) {
            relatorio.incrementarMantidos();
            relatorio.adicionarMensagem(d.getNome() + " mantido: existem notas por lancar ou sem avaliacoes.");
            return TipoResultadoFecho.MANTER;
        }
        if (d.calcularAproveitamento() < 0.60) {
            relatorio.incrementarMantidos();
            relatorio.adicionarMensagem(d.getNome() + " mantido: aproveitamento inferior a 60%.");
            return TipoResultadoFecho.MANTER;
        }
        if (d.getAnoAtual() >= 3) {
            relatorio.incrementarConcluidos();
            relatorio.adicionarMensagem(d.getNome() + " concluiu o curso.");
            return TipoResultadoFecho.CONCLUIR;
        }
        relatorio.incrementarAvancados();
        relatorio.adicionarMensagem(d.getNome() + " avancou para o " + (d.getAnoAtual()+1) + ".o ano.");
        return TipoResultadoFecho.AVANCAR;
    }

    // =========================================================================
    // Auxiliares - path CSV
    // =========================================================================

    private void processarEstudanteNoFecho(Estudante estudante, AnoLetivo anoAtual, RelatorioFechoAnoLetivo relatorio) {
        if (estudante == null || estudante.isConcluido()) return;

        Inscricao inscricaoAtual = obterInscricaoAtual(estudante);
        if (inscricaoAtual == null) {
            relatorio.incrementarMantidos();
            relatorio.adicionarMensagem(estudante.getNome() + " mantido: sem inscricao ativa.");
            return;
        }
        if (!inscricaoAtual.isPropinaPaga()) {
            relatorio.incrementarMantidos();
            relatorio.adicionarMensagem(detalhar(estudante, inscricaoAtual, "MANTIDO: propina do ano atual nao esta paga"));
            return;
        }
        if (inscricaoAtual.temNotasPorLancar()) {
            relatorio.incrementarMantidos();
            relatorio.adicionarMensagem(detalhar(estudante, inscricaoAtual, "MANTIDO: existem notas por lancar"));
            return;
        }
        double aproveitamento = estudante.calcularAproveitamentoGlobal();
        if (aproveitamento < 0.60) {
            relatorio.incrementarMantidos();
            relatorio.adicionarMensagem(detalhar(estudante, inscricaoAtual,
                    String.format("MANTIDO: aproveitamento %.0f%% (< 60%%)", aproveitamento * 100)));
            return;
        }
        if (estudante.getAnoAtual() >= 3) {
            estudante.setEstado("CONCLUIDO");
            relatorio.incrementarConcluidos();
            relatorio.adicionarMensagem(detalhar(estudante, inscricaoAtual, "CONCLUIU o curso"));
            return;
        }
        int proximoAnoCurso = estudante.getAnoAtual() + 1;
        List<String> nomesUCsEmAtraso = new ArrayList<>();
        for (Avaliacao av : estudante.getUCsEmAtraso()) {
            if (av.getUc() != null) {
                for (UnidadeCurricular uc : av.getUc()) {
                    if (!nomesUCsEmAtraso.contains(uc.getNome())) nomesUCsEmAtraso.add(uc.getNome());
                }
            }
        }
        relatorio.registarUcsEmAtraso(estudante.getNome(), nomesUCsEmAtraso);
        String resultadoAvanco = "AVANCOU para o " + proximoAnoCurso + ".o ano"
                + (nomesUCsEmAtraso.isEmpty() ? "" : " (UCs em atraso: " + String.join(", ", nomesUCsEmAtraso) + ")");
        String detalhe = detalhar(estudante, inscricaoAtual, resultadoAvanco);
        estudante.setAnoAtual(proximoAnoCurso);
        estudante.adicionarInscricao(new Inscricao(anoAtual.getAno()+1, proximoAnoCurso, inscricaoAtual.getCurso()));
        relatorio.incrementarAvancados();
        relatorio.adicionarMensagem(detalhe);
    }

    private Inscricao obterInscricaoAtual(Estudante estudante) {
        if (estudante.getInscricoes() == null || estudante.getInscricoes().isEmpty()) return null;
        return estudante.getInscricoes().get(estudante.getInscricoes().size()-1);
    }
}
