package DAL.BD;

import DAL.IDepartamentoDAL;
import Model.Departamento;

import java.util.ArrayList;

/**
 * Implementação da persistência de Departamento em base de dados (SQL Server),
 * alternativa à versão em ficheiro/memória (DAL.DepartamentoDAL).
 */
public class DepartamentoDAL_BD implements IDepartamentoDAL {

    private static final RowMapper<Departamento> MAPPER = rs ->
            new Departamento(rs.getString("nome"), rs.getString("sigla"));

    private final ConexaoBD conexao;

    public DepartamentoDAL_BD() {
        this.conexao = new ConexaoBD();
    }

    @Override
    public Departamento adicionarDepartamento(Departamento departamento) {
        conexao.execute(
                "INSERT INTO Departamento (nome, sigla) VALUES (?, ?)",
                departamento.getNome(), departamento.getSigla()
        );
        return departamento;
    }

    @Override
    public boolean atualizarDepartamento(Departamento departamentoAtualizado) {
        int linhas = conexao.execute(
                "UPDATE Departamento SET nome = ? WHERE sigla = ?",
                departamentoAtualizado.getNome(), departamentoAtualizado.getSigla()
        );
        return linhas > 0;
    }

    @Override
    public void removerDepartamento(Departamento departamento) {
        conexao.execute("DELETE FROM Departamento WHERE sigla = ?", departamento.getSigla());
    }

    @Override
    public Departamento procurarPorSigla(String sigla) {
        ArrayList<Departamento> resultados = conexao.select(
                "SELECT nome, sigla FROM Departamento WHERE sigla = ?", MAPPER, sigla
        );
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public ArrayList<Departamento> listarDepartamentos() {
        return conexao.select("SELECT nome, sigla FROM Departamento", MAPPER);
    }
}
