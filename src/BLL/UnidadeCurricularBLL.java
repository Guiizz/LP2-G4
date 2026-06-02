package BLL;

import DAL.UnidadeCurricularDAL;
import Model.MomentoAvaliacao;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;

public class UnidadeCurricularBLL {
    private UnidadeCurricularDAL unidadeCurricularDAL;

    public UnidadeCurricularBLL(UnidadeCurricularDAL unidadeCurricularDAL){
        this.unidadeCurricularDAL = unidadeCurricularDAL;
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
     * Adiciona um momento de avaliação a uma UC.
     * Regras:
     *  - A UC não pode já ter 3 momentos definidos.
     *  - O nome do momento não pode ser vazio.
     *  - O peso deve ser > 0 e <= 100.
     *  - A soma dos pesos após a adição não pode ultrapassar 100%.
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
        if (uc.getMomentosAvaliacao().size() >= 3) {
            throw new IllegalArgumentException("Não é possível adicionar outro momento. A UC já atingiu o limite máximo de 3 avaliações.");
        }

        double somaAtual = uc.somaPesos();
        if (somaAtual + peso > 100.0 + 0.01) {
            throw new IllegalArgumentException(
                    "Peso inválido. A soma atual é " + String.format("%.1f", somaAtual)
                            + "% e ao adicionar " + String.format("%.1f", peso)
                            + "% ultrapassaria 100%."
            );
        }

        uc.adicionarMomento(new MomentoAvaliacao(nome, peso));
        unidadeCurricularDAL.atualizarUnidade(uc);
    }

    /**
     * Inicia uma UC, tornando-a ativa.
     * Regras:
     *  - A UC tem de ter exatamente 3 momentos de avaliação.
     *  - A soma dos pesos tem de ser exatamente 100%.
     */
    public void iniciarUC(UnidadeCurricular uc) {
        if (uc == null) {
            throw new IllegalArgumentException("A UC não pode ser nula.");
        }

        if (uc.isAtiva()) {
            throw new IllegalArgumentException("A UC '" + uc.getNome() + "' já está ativa.");
        }

        int numMomentos = uc.getMomentosAvaliacao().size();
        double soma = uc.somaPesos();

        if (numMomentos < 1) {
            throw new IllegalArgumentException(
                    "Não é possível iniciar a UC '" + uc.getNome() + "'.\n"
                            + "  Motivo: deve existir pelo menos 1 momento de avaliação."
            );
        }

        if (numMomentos > 3) {
            throw new IllegalArgumentException(
                    "Não é possível iniciar a UC '" + uc.getNome() + "'.\n"
                            + "  Motivo: não podem existir mais de 3 momentos de avaliação."
            );
        }

        if (Math.abs(soma - 100.0) > 0.01) {
            throw new IllegalArgumentException(
                    "Não é possível iniciar a UC '" + uc.getNome() + "'.\n"
                            + "  Motivo: a soma dos pesos é " + String.format("%.1f", soma)
                            + "% — tem de ser 100%."
            );
        }

        uc.setAtiva(true);
        unidadeCurricularDAL.atualizarUnidade(uc);
    }
}
