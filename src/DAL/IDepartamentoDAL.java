package DAL;

import Model.Departamento;

import java.util.ArrayList;

/**
 * Contrato de acesso a dados de Departamento, comum às implementações
 * em ficheiro/memória (DepartamentoDAL) e em base de dados (DAL.BD.DepartamentoDAL_BD).
 */
public interface IDepartamentoDAL {

    Departamento adicionarDepartamento(Departamento departamento);

    boolean atualizarDepartamento(Departamento departamentoAtualizado);

    void removerDepartamento(Departamento departamento);

    Departamento procurarPorSigla(String sigla);

    ArrayList<Departamento> listarDepartamentos();
}
