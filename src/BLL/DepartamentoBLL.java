package BLL;

import DAL.DepartamentoDAL;
import Model.Departamento;

import java.util.ArrayList;

import static Utils.Utils.*;

public class DepartamentoBLL {
    private DepartamentoDAL departamentoDAL;

    public DepartamentoBLL(DepartamentoDAL departamentoDAL) {
        this.departamentoDAL = departamentoDAL;
    }

    public Departamento registarDepartamento(String nome, String sigla){

        // validações
        validarNome(nome);
        validarSigla(sigla);

        // verificar duplicados
        if (departamentoDAL.procurarPorSigla(sigla) != null){
            throw new IllegalArgumentException("Já existe um departamento com essa sigla.");
        }

        Departamento novoDepartamento = new Departamento(nome, sigla);
        return departamentoDAL.adicionarDepartamento(novoDepartamento);
    }

    public ArrayList<Departamento> listarDepartamentos() {
        return departamentoDAL.listarDepartamentos();
    }

    public Departamento procurarDepartamento(String sigla){
        return departamentoDAL.procurarPorSigla(sigla);
    }

    public void atualizarDepartamento(Departamento departamento, String novoNome) {
        if (novoNome == null || novoNome.isBlank()) {
            throw new IllegalArgumentException("O nome do departamento nâo deve ser vazio.");
        }
        departamento.setNome(novoNome);
        departamentoDAL.atualizarDepartamento(departamento);
    }
    public void removerDepartamento(Departamento departamento) {
        if (departamento.getCursos() != null && !departamento.getCursos().isEmpty()) {
            throw new IllegalArgumentException(
                    "Não é possível remover o departamento '" + departamento.getNome() + " porque tem cursos associados.");
        }
        departamentoDAL.removerDepartamento(departamento);
    }
}