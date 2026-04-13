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
}