package DAL.BD;

import DAL.IPagamentoDAL;
import Model.Pagamento;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da persistência de Pagamento em base de dados (SQL Server).
 *
 * Esquema esperado:
 *   CREATE TABLE Pagamento (
 *       id               INT IDENTITY  NOT NULL,
 *       numMecanografico VARCHAR(20)   NOT NULL,
 *       anoLetivo        INT           NOT NULL,
 *       valor            FLOAT         NOT NULL,
 *       data             DATE          NOT NULL,
 *       CONSTRAINT PK_Pagamento PRIMARY KEY (id),
 *       CONSTRAINT FK_Pagamento_Propina FOREIGN KEY (numMecanografico, anoLetivo)
 *           REFERENCES Propina(numMecanografico, anoLetivo)
 *   );
 */
public class PagamentoDAL_BD implements IPagamentoDAL {

    private static final RowMapper<Pagamento> MAPPER = rs -> {
        double valor     = rs.getDouble("valor");
        LocalDate data   = rs.getDate("data").toLocalDate();
        return new Pagamento(valor, data);
    };

    private final ConexaoBD conexao;

    public PagamentoDAL_BD() {
        this.conexao = new ConexaoBD();
    }

    @Override
    public void adicionarPagamento(String numMecanografico, int anoLetivo, Pagamento pagamento) {
        conexao.execute(
                "INSERT INTO Pagamento (numMecanografico, anoLetivo, valor, data) VALUES (?, ?, ?, ?)",
                numMecanografico,
                anoLetivo,
                pagamento.getValor(),
                Date.valueOf(pagamento.getData())
        );
    }

    @Override
    public List<Pagamento> listarPorInscricao(String numMecanografico, int anoLetivo) {
        return conexao.select(
                "SELECT valor, data FROM Pagamento WHERE numMecanografico = ? AND anoLetivo = ? ORDER BY id",
                MAPPER, numMecanografico, anoLetivo
        );
    }

    @Override
    public void removerPorInscricao(String numMecanografico, int anoLetivo) {
        conexao.execute(
                "DELETE FROM Pagamento WHERE numMecanografico = ? AND anoLetivo = ?",
                numMecanografico, anoLetivo
        );
    }

    @Override
    public java.util.Map<String, List<Pagamento>> listarTodosAgrupados() {
        ArrayList<Object[]> linhas = conexao.select(
                "SELECT numMecanografico, anoLetivo, valor, data FROM Pagamento " +
                "ORDER BY numMecanografico, anoLetivo, id",
                rs -> new Object[]{
                        rs.getString("numMecanografico"),
                        rs.getInt("anoLetivo"),
                        rs.getDouble("valor"),
                        rs.getDate("data").toLocalDate()
                }
        );
        java.util.Map<String, List<Pagamento>> resultado = new java.util.HashMap<>();
        for (Object[] linha : linhas) {
            String chave = linha[0] + "|" + linha[1];
            Pagamento p = new Pagamento((double) linha[2], (LocalDate) linha[3]);
            resultado.computeIfAbsent(chave, k -> new ArrayList<>()).add(p);
        }
        return resultado;
    }
}
