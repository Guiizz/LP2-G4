package DAL;

import Model.Avaliacao;
import Model.UnidadeCurricular;

import java.util.ArrayList;
import java.util.Date;

/**
 * Contrato de acesso a dados de Avaliacao, comum às implementações
 * em ficheiro/memória (AvaliacaoDAL) e em base de dados (DAL.BD.AvaliacaoDAL_BD).
 */
public interface IAvaliacaoDAL {

    void adicionarAvaliacao(Avaliacao avaliacao);

    ArrayList<Avaliacao> listarAvaliacoes();

    boolean atualizarAvaliacao(Avaliacao avaliacaoAntiga, Avaliacao avaliacaoNova);

    void removerAvaliacao(Avaliacao avaliacao);

    ArrayList<Avaliacao> procurarPorUC(UnidadeCurricular uc);

    ArrayList<Avaliacao> procurarPorData(Date data);
}
