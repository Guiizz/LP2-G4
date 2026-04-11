package DAL;

import Model.Avaliacao;
import Model.UnidadeCurricular;

import java.util.ArrayList;
import java.util.Date;

/**
 * Camada DAL para a entidade Avaliacao.
 * Responsável por armazenar e recuperar avaliações em memória.
 */
public class AvaliacaoDAL {

    private ArrayList<Avaliacao> avaliacoes;

    /**
     * Construtor da classe AvaliacaoDAL.
     */
    public AvaliacaoDAL() {
        avaliacoes = new ArrayList<>();
    }

    /**
     * Adiciona uma nova avaliação à lista.
     *
     * @param avaliacao A avaliação a adicionar.
     */
    public void adicionarAvaliacao(Avaliacao avaliacao) {
        avaliacoes.add(avaliacao);
    }

    /**
     * Lista todas as avaliações registadas no sistema.
     *
     * @return Lista de avaliações.
     */
    public ArrayList<Avaliacao> listarAvaliacoes() {
        return new ArrayList<>(avaliacoes);
    }

    /**
     * Atualiza uma avaliação existente.
     * A identificação é feita por referência ao objeto antigo.
     *
     * @param avaliacaoAntiga A avaliação antiga.
     * @param avaliacaoNova A nova avaliação com os dados atualizados.
     * @return True se foi atualizada com sucesso, False caso contrário.
     */
    public boolean atualizarAvaliacao(Avaliacao avaliacaoAntiga, Avaliacao avaliacaoNova) {
        for (int i = 0; i < avaliacoes.size(); i++) {
            if (avaliacoes.get(i) == avaliacaoAntiga) {
                avaliacoes.set(i, avaliacaoNova);
                return true;
            }
        }
        return false;
    }

    /**
     * Remove uma avaliação da lista.
     *
     * @param avaliacao A avaliação a remover.
     */
    public void removerAvaliacao(Avaliacao avaliacao) {
        avaliacoes.remove(avaliacao);
    }

    /**
     * Procura avaliações associadas a uma determinada UC.
     *
     * @param uc A unidade curricular.
     * @return Lista de avaliações dessa UC.
     */
    public ArrayList<Avaliacao> procurarPorUC(UnidadeCurricular uc) {
        ArrayList<Avaliacao> resultado = new ArrayList<>();

        for (Avaliacao avaliacao : avaliacoes) {
            if (avaliacao.getUc() != null && avaliacao.getUc().contains(uc)) {
                resultado.add(avaliacao);
            }
        }

        return resultado;
    }

    /**
     * Procura avaliações realizadas numa determinada data.
     *
     * @param data A data da avaliação.
     * @return Lista de avaliações encontradas nessa data.
     */
    public ArrayList<Avaliacao> procurarPorData(Date data) {
        ArrayList<Avaliacao> resultado = new ArrayList<>();

        if (data == null) {
            return resultado;
        }

        for (Avaliacao avaliacao : avaliacoes) {
            if (avaliacao.getData() != null && avaliacao.getData().equals(data)) {
                resultado.add(avaliacao);
            }
        }

        return resultado;
    }
}