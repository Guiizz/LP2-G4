package DAL;

import Model.TipoJustificacao;

import java.util.List;

public interface ITipoJustificacaoDAL {

    void adicionar(TipoJustificacao tipo);

    void remover(String nome);

    TipoJustificacao procurarPorNome(String nome);

    List<TipoJustificacao> listarTodos();
}
