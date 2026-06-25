package DAL.BD;

import DAL.ITipoJustificacaoDAL;
import Model.TipoJustificacao;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da persistência de TipoJustificacao em base de dados (SQL Server).
 *
 * Esquema esperado:
 *   CREATE TABLE TipoJustificacao (
 *       nome        VARCHAR(100) NOT NULL,
 *       categoria   VARCHAR(20)  NOT NULL,
 *       CONSTRAINT PK_TipoJustificacao PRIMARY KEY (nome)
 *   );
 */
public class TipoJustificacaoDAL_BD implements ITipoJustificacaoDAL {

    private static final RowMapper<TipoJustificacao> MAPPER =
            rs -> new TipoJustificacao(rs.getString("nome"), rs.getString("categoria"));

    private final ConexaoBD conexao;

    public TipoJustificacaoDAL_BD() {
        this.conexao = new ConexaoBD();
    }

    @Override
    public void adicionar(TipoJustificacao tipo) {
        conexao.execute(
                "INSERT INTO TipoJustificacao (nome, categoria) VALUES (?, ?)",
                tipo.getNome(), tipo.getCategoria()
        );
    }

    @Override
    public void remover(String nome) {
        conexao.execute("DELETE FROM TipoJustificacao WHERE nome = ?", nome);
    }

    @Override
    public TipoJustificacao procurarPorNome(String nome) {
        ArrayList<TipoJustificacao> resultado = conexao.select(
                "SELECT nome, categoria FROM TipoJustificacao WHERE nome = ?", MAPPER, nome
        );
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    @Override
    public List<TipoJustificacao> listarTodos() {
        return conexao.select("SELECT nome, categoria FROM TipoJustificacao ORDER BY nome", MAPPER);
    }
}
