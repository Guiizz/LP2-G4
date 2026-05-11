package BLL;

import DAL.AnoLetivoDAL;
import Model.AnoLetivo;
import Model.Estudante;
import Model.Inscricao;
import Model.RelatorioFechoAnoLetivo;

import java.time.LocalDate;
import java.util.List;

public class AnoLetivoBLL {
    private final AnoLetivoDAL anoLetivoDAL;

    public AnoLetivoBLL(AnoLetivoDAL anoLetivoDAL) {
        this.anoLetivoDAL = anoLetivoDAL;
    }

    public AnoLetivo consultarAnoAtual() {
        return anoLetivoDAL.procurarAnoAberto();
    }

    public AnoLetivo consultarMaisRecente() {
        return anoLetivoDAL.procurarMaisRecente();
    }

    public AnoLetivo abrirAnoLetivo(int ano) {
        if (ano < 2000) {
            throw new IllegalArgumentException("Ano letivo inválido.");
        }

        if (anoLetivoDAL.procurarAnoAberto() != null) {
            throw new IllegalArgumentException("Já existe um ano letivo aberto.");
        }

        if (anoLetivoDAL.procurarPorAno(ano) != null) {
            throw new IllegalArgumentException("Já existe um registo para o ano letivo " + ano + "/" + (ano + 1) + ".");
        }

        AnoLetivo anoLetivo = new AnoLetivo(ano, LocalDate.now());
        anoLetivoDAL.adicionarAnoLetivo(anoLetivo);
        return anoLetivo;
    }

    public RelatorioFechoAnoLetivo fecharAnoAtual(List<Estudante> estudantes) {
        AnoLetivo anoAtual = anoLetivoDAL.procurarAnoAberto();

        if (anoAtual == null) {
            throw new IllegalArgumentException("Não existe ano letivo aberto para fechar.");
        }

        if (estudantes == null) {
            throw new IllegalArgumentException("Lista de estudantes inválida.");
        }

        RelatorioFechoAnoLetivo relatorio = new RelatorioFechoAnoLetivo();

        for (Estudante estudante : estudantes) {
            processarEstudanteNoFecho(estudante, anoAtual, relatorio);
        }

        anoAtual.fechar(LocalDate.now());
        anoLetivoDAL.atualizarAnoLetivo(anoAtual);

        return relatorio;
    }

    private void processarEstudanteNoFecho(Estudante estudante, AnoLetivo anoAtual, RelatorioFechoAnoLetivo relatorio) {
        if (estudante == null || estudante.isConcluido()) {
            return;
        }

        Inscricao inscricaoAtual = obterInscricaoAtual(estudante);

        if (inscricaoAtual == null) {
            relatorio.incrementarMantidos();
            relatorio.adicionarMensagem(estudante.getNome() + " mantido: sem inscrição ativa.");
            return;
        }

        if (!inscricaoAtual.isPropinaPaga()) {
            relatorio.incrementarMantidos();
            relatorio.adicionarMensagem(estudante.getNome() + " mantido: propina do ano atual não está paga.");
            return;
        }

        if (inscricaoAtual.temNotasPorLancar()) {
            relatorio.incrementarMantidos();
            relatorio.adicionarMensagem(estudante.getNome() + " mantido: existem notas por lançar ou sem avaliações.");
            return;
        }

        double aproveitamento = inscricaoAtual.calcularAproveitamento();

        if (aproveitamento < 0.60) {
            relatorio.incrementarMantidos();
            relatorio.adicionarMensagem(estudante.getNome() + " mantido: aproveitamento inferior a 60%.");
            return;
        }

        if (estudante.getAnoAtual() >= 3) {
            estudante.setEstado("CONCLUIDO");
            relatorio.incrementarConcluidos();
            relatorio.adicionarMensagem(estudante.getNome() + " concluiu o curso.");
            return;
        }

        int proximoAnoCurso = estudante.getAnoAtual() + 1;
        estudante.setAnoAtual(proximoAnoCurso);
        estudante.adicionarInscricao(new Inscricao(anoAtual.getAno() + 1, proximoAnoCurso, inscricaoAtual.getCurso()));

        relatorio.incrementarAvancados();
        relatorio.adicionarMensagem(estudante.getNome() + " avançou para o " + proximoAnoCurso + ".º ano.");
    }

    private Inscricao obterInscricaoAtual(Estudante estudante) {
        if (estudante.getInscricoes() == null || estudante.getInscricoes().isEmpty()) {
            return null;
        }

        return estudante.getInscricoes().get(estudante.getInscricoes().size() - 1);
    }
}