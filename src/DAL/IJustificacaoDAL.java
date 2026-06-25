package DAL;

import Model.JustificacaoFalta;

import java.util.List;

public interface IJustificacaoDAL {

    void adicionar(JustificacaoFalta justificacao);

    void atualizar(JustificacaoFalta justificacao);

    List<JustificacaoFalta> listarPorEstudante(String numMecanografico);

    List<JustificacaoFalta> listarPendentes();

    List<JustificacaoFalta> listarTodas();
}
