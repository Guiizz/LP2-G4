package Controller;

import BLL.AvaliacaoBLL;
import Model.Avaliacao;
import Model.Docente;
import Model.Estudante;
import Model.UnidadeCurricular;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controlador responsável por gerir as operações relacionadas com a entidade Avaliacao.
 * Serve de intermediário entre a camada de apresentação e a camada de negócio.
 */
public class AvaliacaoController {

    private AvaliacaoBLL avaliacaoBLL;

    /**
     * Construtor do AvaliacaoController.
     * Inicializa a camada de negócio da Avaliação.
     *
     * @param avaliacaoBLL Instância da camada BLL.
     */
    public AvaliacaoController(AvaliacaoBLL avaliacaoBLL) {
        this.avaliacaoBLL = avaliacaoBLL;
    }

    /**
     * Regista uma nova avaliação no sistema.
     *
     * @param ucs Lista de unidades curriculares associadas.
     * @param peso Peso da avaliação.
     * @param data Data da avaliação.
     * @param nota Nota atribuída.
     * @return A avaliação criada.
     * @throws IllegalArgumentException Se os dados forem inválidos.
     */
    public Avaliacao registarAvaliacao(List<UnidadeCurricular> ucs,
                                       double peso,
                                       Date data,
                                       double nota) {
        return avaliacaoBLL.registarAvaliacao(ucs, peso, data, nota);
    }

    /**
     * Atualiza uma avaliação existente.
     *
     * @param avaliacaoAntiga Avaliação original.
     * @param novasUCs Novas unidades curriculares.
     * @param novoPeso Novo peso.
     * @param novaData Nova data.
     * @param novaNota Nova nota.
     * @throws IllegalArgumentException Se os dados forem inválidos ou a avaliação não existir.
     */
    public void atualizarAvaliacao(Avaliacao avaliacaoAntiga,
                                   List<UnidadeCurricular> novasUCs,
                                   double novoPeso,
                                   Date novaData,
                                   double novaNota) {
        avaliacaoBLL.atualizarAvaliacao(avaliacaoAntiga, novasUCs, novoPeso, novaData, novaNota);
    }

    /**
     * Remove uma avaliação do sistema.
     *
     * @param avaliacao A avaliação a remover.
     * @throws IllegalArgumentException Se a avaliação for inválida.
     */
    public void removerAvaliacao(Avaliacao avaliacao) {
        avaliacaoBLL.removerAvaliacao(avaliacao);
    }

    /**
     * Lista todas as avaliações registadas no sistema.
     *
     * @return Lista de avaliações.
     */
    public ArrayList<Avaliacao> listarAvaliacoes() {
        return avaliacaoBLL.listarAvaliacoes();
    }

    /**
     * Procura avaliações associadas a uma unidade curricular.
     *
     * @param uc Unidade curricular.
     * @return Lista de avaliações associadas.
     * @throws IllegalArgumentException Se a UC for inválida.
     */
    public ArrayList<Avaliacao> procurarPorUC(UnidadeCurricular uc) {
        return avaliacaoBLL.procurarPorUC(uc);
    }

    /**
     * Procura avaliações por data.
     *
     * @param data Data da avaliação.
     * @return Lista de avaliações encontradas.
     * @throws IllegalArgumentException Se a data for inválida.
     */
    public ArrayList<Avaliacao> procurarPorData(Date data) {
        return avaliacaoBLL.procurarPorData(data);
    }
    // ACRESCENTA no fim da classe, antes do último }
// Também adiciona os imports: import Model.Docente; import Model.Estudante;

    public Avaliacao lancarNotaAluno(Docente docenteLogado, UnidadeCurricular uc,
                                     Estudante estudante, String nomeMomento,
                                     double peso, Date data, double nota) {
        return avaliacaoBLL.lancarNotaAluno(docenteLogado, uc, estudante, nomeMomento, peso, data, nota);
    }

    public double calcularNotaFinal(Estudante estudante, UnidadeCurricular uc) {
        return avaliacaoBLL.calcularNotaFinal(estudante, uc);
    }
}