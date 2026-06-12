package BLL;

import DAL.AvaliacaoDAL;
import DAL.IAvaliacaoDAL;
import Model.Avaliacao;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Camada de Lógica de Negócio (BLL) para a entidade Avaliacao.
 * Responsável por validar e aplicar regras de negócio às avaliações.
 */
public class AvaliacaoBLL {

    private IAvaliacaoDAL avaliacaoDAL;

    /**
     * Construtor da classe AvaliacaoBLL.
     */
    public AvaliacaoBLL() {
        this.avaliacaoDAL = new AvaliacaoDAL();  // modo ficheiro
    }

    /**
     * Construtor com injeção da DAL.
     *
     * @param avaliacaoDAL A camada DAL a utilizar.
     */
    public AvaliacaoBLL(IAvaliacaoDAL avaliacaoDAL) {
        this.avaliacaoDAL = avaliacaoDAL;
    }

    /**
     * Regista uma nova avaliação no sistema.
     *
     * Regras:
     * - a lista de UCs não pode ser nula nem vazia
     * - o peso deve ser superior a 0 e inferior ou igual a 100
     * - a data não pode ser nula nem futura
     * - a nota tem de estar entre 0 e 20
     *
     * @param ucs Lista de UCs associadas à avaliação.
     * @param peso Peso da avaliação.
     * @param data Data da avaliação.
     * @param nota Nota da avaliação.
     * @return A avaliação criada.
     */
    public Avaliacao registarAvaliacao(List<UnidadeCurricular> ucs, double peso, Date data, double nota) {
        validarUCs(ucs);
        validarPeso(peso);
        validarData(data);
        Utils.validarNota(nota);

        for (Avaliacao existente : avaliacaoDAL.listarAvaliacoes()) {
            if (existente.getData() != null && existente.getData().equals(data)
                    && existente.getUc() != null && existente.getUc().containsAll(ucs) && ucs.containsAll(existente.getUc())) {
                throw new IllegalArgumentException("Já existe uma avaliação para as mesmas UCs nesta data.");
            }
        }

        Avaliacao novaAvaliacao = new Avaliacao(ucs, peso, data, nota, nota >= 10.0);
        avaliacaoDAL.adicionarAvaliacao(novaAvaliacao);

        for (UnidadeCurricular uc : ucs) {
            if (uc.getAvaliacoes() != null && !uc.getAvaliacoes().contains(novaAvaliacao)) {
                uc.getAvaliacoes().add(novaAvaliacao);
            }
        }

        return novaAvaliacao;
    }

    /**
     * Lista todas as avaliações registadas.
     *
     * @return Lista de avaliações.
     */
    public ArrayList<Avaliacao> listarAvaliacoes() {
        return avaliacaoDAL.listarAvaliacoes();
    }

    /**
     * Atualiza uma avaliação existente.
     *
     * @param avaliacaoAntiga A avaliação original.
     * @param novasUCs As novas UCs.
     * @param novoPeso O novo peso.
     * @param novaData A nova data.
     * @param novaNota A nova nota.
     */
    public void atualizarAvaliacao(Avaliacao avaliacaoAntiga,
                                   List<UnidadeCurricular> novasUCs,
                                   double novoPeso,
                                   Date novaData,
                                   double novaNota) {

        if (avaliacaoAntiga == null) {
            throw new IllegalArgumentException("A avaliação antiga não pode ser nula.");
        }

        validarUCs(novasUCs);
        validarPeso(novoPeso);
        validarData(novaData);
        Utils.validarNota(novaNota);

        Avaliacao avaliacaoNova = new Avaliacao(novasUCs, novoPeso, novaData, novaNota, novaNota >= 10.0);

        boolean atualizado = avaliacaoDAL.atualizarAvaliacao(avaliacaoAntiga, avaliacaoNova);

        if (!atualizado) {
            throw new IllegalArgumentException("Não foi possível encontrar a avaliação para atualizar.");
        }

        // remover a avaliação antiga das UCs antigas
        if (avaliacaoAntiga.getUc() != null) {
            for (UnidadeCurricular uc : avaliacaoAntiga.getUc()) {
                if (uc.getAvaliacoes() != null) {
                    uc.getAvaliacoes().remove(avaliacaoAntiga);
                }
            }
        }

        // adicionar a nova avaliação às novas UCs
        for (UnidadeCurricular uc : novasUCs) {
            if (uc.getAvaliacoes() != null && !uc.getAvaliacoes().contains(avaliacaoNova)) {
                uc.getAvaliacoes().add(avaliacaoNova);
            }
        }
    }

    /**
     * Remove uma avaliação do sistema.
     *
     * @param avaliacao A avaliação a remover.
     */
    public void removerAvaliacao(Avaliacao avaliacao) {
        if (avaliacao == null) {
            throw new IllegalArgumentException("A avaliação não pode ser nula.");
        }

        if (avaliacao.getUc() != null) {
            for (UnidadeCurricular uc : avaliacao.getUc()) {
                if (uc.getAvaliacoes() != null) {
                    uc.getAvaliacoes().remove(avaliacao);
                }
            }
        }

        avaliacaoDAL.removerAvaliacao(avaliacao);
    }

    /**
     * Procura avaliações associadas a uma determinada UC.
     *
     * @param uc A unidade curricular.
     * @return Lista de avaliações da UC.
     */
    public ArrayList<Avaliacao> procurarPorUC(UnidadeCurricular uc) {
        if (uc == null) {
            throw new IllegalArgumentException("A unidade curricular não pode ser nula.");
        }

        return avaliacaoDAL.procurarPorUC(uc);
    }

    /**
     * Procura avaliações por data.
     *
     * @param data A data da avaliação.
     * @return Lista de avaliações encontradas.
     */
    public ArrayList<Avaliacao> procurarPorData(Date data) {
        validarData(data);
        return avaliacaoDAL.procurarPorData(data);
    }

    /**
     * Valida se a lista de UCs é válida.
     *
     * @param ucs Lista de UCs.
     */
    private void validarUCs(List<UnidadeCurricular> ucs) {
        if (ucs == null || ucs.isEmpty()) {
            throw new IllegalArgumentException("A avaliação tem de estar associada a pelo menos uma UC.");
        }

        for (UnidadeCurricular uc : ucs) {
            if (uc == null) {
                throw new IllegalArgumentException("A lista de UCs não pode conter valores nulos.");
            }
        }
    }

    /**
     * Valida o peso da avaliação.
     *
     * @param peso O peso.
     */
    private void validarPeso(double peso) {
        if (peso <= 0 || peso > 100) {
            throw new IllegalArgumentException("O peso da avaliação deve estar entre 0 e 100.");
        }
    }

    /**
     * Valida a data da avaliação.
     *
     * @param data A data.
     */
    private void validarData(Date data) {
        if (data == null) {
            throw new IllegalArgumentException("A data da avaliação não pode ser nula.");
        }

        Date hoje = new Date();
        if (data.after(hoje)) {
            throw new IllegalArgumentException("A data da avaliação não pode ser no futuro.");
        }
    }
}