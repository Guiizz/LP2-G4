package BLL;

import DAL.AnoLetivoDAL;
import DAL.UnidadeCurricularDAL;
import Model.AnoLetivo;
import Model.MomentoAvaliacao;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class UnidadeCurricularBLL {
    private UnidadeCurricularDAL unidadeCurricularDAL;
    private AnoLetivoDAL         anoLetivoDAL;

    public UnidadeCurricularBLL(UnidadeCurricularDAL unidadeCurricularDAL, AnoLetivoDAL anoLetivoDAL) {
        this.unidadeCurricularDAL = unidadeCurricularDAL;
        this.anoLetivoDAL         = anoLetivoDAL;
    }

    /** Construtor de compatibilidade. */
    public UnidadeCurricularBLL(UnidadeCurricularDAL unidadeCurricularDAL) {
        this(unidadeCurricularDAL, new AnoLetivoDAL());
    }

    public void adicionarUnidade(UnidadeCurricular unidade){
            if (unidade == null) {
                throw new IllegalArgumentException("A Unidade Curricular não pode ser nula.");
            }
            Utils.validarNome(unidade.getNome());
            if (unidade.getAnoCurricular() < 1 || unidade.getAnoCurricular() > 3) {
                throw new IllegalArgumentException("Ano curricular inválido, deve ser entre 1 e 3.");
            }

            // Todos os ECTS são iguais no sistema — forçar valor fixo
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
        boolean sucesso = unidadeCurricularDAL.atribuirDocenteResponsavel(nomeUC, siglaDocente);
        if (!sucesso) {
            throw new IllegalArgumentException("Unidade Curricular '" + nomeUC + "' não encontrada.");
        }
    }

    /**
     * Adiciona um momento de avaliação a uma UC, associado ao ano letivo actual.
     * Se não existir ano letivo aberto, lança excepção.
     * Regras:
     *  - A UC não pode já ter 3 momentos para esse ano letivo.
     *  - O nome não pode ser vazio.
     *  - O peso deve ser > 0 e <= 100.
     *  - A soma dos pesos desse ano não pode ultrapassar 100%.
     */
    public void adicionarMomento(UnidadeCurricular uc, String nome, double peso) {
        if (uc == null) {
            throw new IllegalArgumentException("A UC não pode ser nula.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do momento não pode ser vazio.");
        }
        if (peso <= 0 || peso > 100) {
            throw new IllegalArgumentException("O peso deve ser um valor entre 0 e 100.");
        }

        AnoLetivo anoAberto = anoLetivoDAL.procurarAnoAberto();
        if (anoAberto == null) {
            throw new IllegalArgumentException(
                    "Não é possível adicionar momentos: não existe ano letivo aberto.");
        }
        int anoLetivo = anoAberto.getAno();

        List<MomentoAvaliacao> momentosDoAno = uc.getMomentosParaAno(anoLetivo);
        if (momentosDoAno.size() >= 3) {
            throw new IllegalArgumentException(
                    "A UC já tem 3 momentos para o ano letivo "
                    + anoLetivo + "/" + (anoLetivo + 1) + ".");
        }

        double somaAtual = uc.somaPesosParaAno(anoLetivo);
        if (somaAtual + peso > 100.0 + 0.01) {
            throw new IllegalArgumentException(
                    "Peso inválido. A soma actual para " + anoLetivo + "/" + (anoLetivo + 1)
                    + " é " + String.format("%.1f", somaAtual)
                    + "% e ao adicionar " + String.format("%.1f", peso)
                    + "% ultrapassaria 100%.");
        }

        uc.adicionarMomento(new MomentoAvaliacao(nome, peso, anoLetivo));
        unidadeCurricularDAL.atualizarUnidade(uc);
    }

    /**
     * Remove um momento de avaliação de uma UC pelo seu índice (0-based).
     * Não é permitido remover momentos de uma UC já activa.
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
        uc.getMomentosAvaliacao().remove(indice);
        unidadeCurricularDAL.atualizarUnidade(uc);
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
