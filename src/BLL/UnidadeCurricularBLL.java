package BLL;

import DAL.AnoLetivoDAL;
import DAL.IAnoLetivoDAL;
import DAL.UnidadeCurricularDAL;
import DAL.IUnidadeCurricularDAL;
import Model.AnoLetivo;
import Model.MomentoAvaliacao;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class UnidadeCurricularBLL {
    private IUnidadeCurricularDAL unidadeCurricularDAL;
    private IAnoLetivoDAL         anoLetivoDAL;

    public UnidadeCurricularBLL(IUnidadeCurricularDAL unidadeCurricularDAL, IAnoLetivoDAL anoLetivoDAL) {
        this.unidadeCurricularDAL = unidadeCurricularDAL;
        this.anoLetivoDAL         = anoLetivoDAL;
    }

    /** Construtor de compatibilidade. */
    public UnidadeCurricularBLL(IUnidadeCurricularDAL unidadeCurricularDAL) {
        this(unidadeCurricularDAL, new AnoLetivoDAL());
    }

    public void adicionarUnidade(UnidadeCurricular unidade){
        if (unidade == null) {
            throw new IllegalArgumentException("A Unidade Curricular não pode ser nula.");
        }
        Utils.validarDesignacao(unidade.getNome());

        String nomeTrimmed = unidade.getNome().trim();
        for (UnidadeCurricular u : unidadeCurricularDAL.listarUnidades()) {
            if (u.getNome().equalsIgnoreCase(nomeTrimmed)) {
                throw new IllegalArgumentException("Já existe uma UC com o nome: " + unidade.getNome());
            }
        }

        unidade.setEts(Utils.ECTS_POR_UC);
        unidadeCurricularDAL.adicionarUnidade(unidade);
    }

    public ArrayList<UnidadeCurricular> listarUnidades(){
        return unidadeCurricularDAL.listarUnidades();
    }

    public void atualizarUnidade(UnidadeCurricular unidade){
        if (unidade == null) {
            throw new IllegalArgumentException("A Unidade Curricular não pode ser nula.");
        }
        Utils.validarDesignacao(unidade.getNome());

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
        Utils.validarSigla(siglaDocente);
        boolean sucesso = unidadeCurricularDAL.atribuirDocenteResponsavel(nomeUC, siglaDocente);
        if (!sucesso) {
            throw new IllegalArgumentException("Unidade Curricular '" + nomeUC + "' não encontrada.");
        }
    }

    /**
     * Adiciona um momento de avaliação a uma UC, associado ao ano letivo actual
     * e a um curso específico (a mesma UC noutro curso tem momentos próprios).
     * O peso é calculado automaticamente: 1→100%, 2→50/50%, 3→33/33/34%.
     * A data tem de ser hoje ou no futuro e cair dentro do ano letivo aberto.
     */
    public void adicionarMomento(UnidadeCurricular uc, String nome, String nomeCurso, java.util.Date data) {
        if (uc == null) {
            throw new IllegalArgumentException("A UC não pode ser nula.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do momento não pode ser vazio.");
        }
        if (nomeCurso == null || nomeCurso.isBlank()) {
            throw new IllegalArgumentException("O momento tem de estar associado a um curso.");
        }
        if (uc.isAtiva()) {
            throw new IllegalArgumentException("Não é possível adicionar momentos à UC '" + uc.getNome() + "' porque já está ativa.");
        }

        AnoLetivo anoAberto = anoLetivoDAL.procurarAnoAberto();
        if (anoAberto == null) {
            throw new IllegalArgumentException(
                    "Não é possível adicionar momentos: não existe ano letivo aberto.");
        }
        int anoLetivo = anoAberto.getAno();

        validarDataMomento(data, anoAberto);

        List<MomentoAvaliacao> momentosDoAno = uc.getMomentosParaAno(anoLetivo, nomeCurso);
        if (momentosDoAno.size() >= 3) {
            throw new IllegalArgumentException(
                    "A UC já tem 3 momentos para o ano letivo "
                    + anoLetivo + "/" + (anoLetivo + 1) + " no curso '" + nomeCurso + "'.");
        }

        uc.adicionarMomento(new MomentoAvaliacao(nome, 0, anoLetivo, nomeCurso, data));
        redistribuirPesos(uc.getMomentosParaAno(anoLetivo, nomeCurso));
        unidadeCurricularDAL.atualizarUnidade(uc);
    }

    /**
     * Valida a data de um momento: não pode ser no passado nem fora do ano letivo aberto.
     * O ano letivo X/X+1 termina a 31 de agosto de X+1.
     */
    private void validarDataMomento(java.util.Date data, AnoLetivo anoAberto) {
        if (data == null) {
            throw new IllegalArgumentException("A data do momento não pode ser nula.");
        }
        java.time.LocalDate dataMomento = data.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

        if (dataMomento.isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("A data do momento não pode ser no passado.");
        }

        java.time.LocalDate fimAnoLetivo = java.time.LocalDate.of(anoAberto.getAno() + 1, 8, 31);
        if (dataMomento.isAfter(fimAnoLetivo)) {
            throw new IllegalArgumentException(
                    "A data do momento tem de estar dentro do ano letivo "
                    + anoAberto.getDesignacao() + " (até " + fimAnoLetivo + ").");
        }
    }

    /**
     * Remove um momento de avaliação de uma UC pelo seu índice (0-based)
     * e redistribui os pesos automaticamente.
     */
    public void removerMomento(UnidadeCurricular uc, int indice) {
        if (uc == null) {
            throw new IllegalArgumentException("A UC não pode ser nula.");
        }
        if (uc.isAtiva()) {
            throw new IllegalArgumentException(
                    "Não é possível remover momentos da UC '" + uc.getNome() + "' porque já está ativa.");
        }
        if (uc.getMomentosAvaliacao() == null || uc.getMomentosAvaliacao().isEmpty()) {
            throw new IllegalArgumentException("A UC '" + uc.getNome() + "' não tem momentos para remover.");
        }
        if (indice < 0 || indice >= uc.getMomentosAvaliacao().size()) {
            throw new IllegalArgumentException("Índice de momento inválido.");
        }
        MomentoAvaliacao removido = uc.getMomentosAvaliacao().get(indice);
        int anoLetivo = removido.getAnoLetivo();
        uc.getMomentosAvaliacao().remove(indice);
        redistribuirPesos(uc.getMomentosParaAno(anoLetivo, removido.getNomeCurso()));
        unidadeCurricularDAL.atualizarUnidade(uc);
    }

    private void redistribuirPesos(List<MomentoAvaliacao> momentos) {
        int n = momentos.size();
        if (n == 0) return;
        if (n == 1) {
            momentos.get(0).setPeso(100.0);
        } else if (n == 2) {
            momentos.get(0).setPeso(50.0);
            momentos.get(1).setPeso(50.0);
        } else {
            momentos.get(0).setPeso(33.0);
            momentos.get(1).setPeso(33.0);
            momentos.get(2).setPeso(34.0);
        }
    }

    /**
     * Inicia uma UC, tornando-a ativa.
     * Valida os momentos do ano letivo actual (ou legados se não houver ano aberto).
     * Regras: pelo menos 1 momento, no máximo 3, soma de pesos = 100%.
     */
    public void iniciarUC(UnidadeCurricular uc) {
        if (uc == null) {
            throw new IllegalArgumentException("A UC não pode ser nula.");
        }
        if (uc.isAtiva()) {
            throw new IllegalArgumentException("A UC '" + uc.getNome() + "' já está ativa.");
        }

        AnoLetivo anoAberto = anoLetivoDAL.procurarAnoAberto();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : 0;

        List<MomentoAvaliacao> momentos = uc.getMomentosParaAno(anoLetivo);
        int numMomentos = momentos.size();
        double soma = 0;
        for (MomentoAvaliacao m : momentos) soma += m.getPeso();

        if (numMomentos < 1) {
            throw new IllegalArgumentException(
                    "Não é possível iniciar a UC '" + uc.getNome() + "'.\n"
                    + "  Motivo: deve existir pelo menos 1 momento de avaliação.");
        }
        if (numMomentos > 3) {
            throw new IllegalArgumentException(
                    "Não é possível iniciar a UC '" + uc.getNome() + "'.\n"
                    + "  Motivo: não podem existir mais de 3 momentos de avaliação.");
        }
        if (Math.abs(soma - 100.0) > 0.01) {
            throw new IllegalArgumentException(
                    "Não é possível iniciar a UC '" + uc.getNome() + "'.\n"
                    + "  Motivo: a soma dos pesos é " + String.format("%.1f", soma)
                    + "% — tem de ser 100%.");
        }

        uc.setAtiva(true);
        unidadeCurricularDAL.atualizarUnidade(uc);
    }
}
