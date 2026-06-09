package DAL;

import Model.UnidadeCurricular;

import java.util.ArrayList;

/**
 * Contrato de acesso a dados de UnidadeCurricular, comum às implementações
 * em ficheiro/memória (UnidadeCurricularDAL) e em base de dados (DAL.BD.UnidadeCurricularDAL_BD).
 */
public interface IUnidadeCurricularDAL {

    void adicionarUnidade(UnidadeCurricular unidade);

    boolean atualizarUnidade(UnidadeCurricular unidadeAtualizada);

    ArrayList<UnidadeCurricular> listarUnidades();

    void removerUnidade(UnidadeCurricular unidade);

    UnidadeCurricular procurarPorNome(String nome);

    boolean atribuirDocenteResponsavel(String nomeUC, String siglaDocente);
}
