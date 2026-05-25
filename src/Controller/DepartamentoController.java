package Controller;

import BLL.DepartamentoBLL;
import DAL.DepartamentoDAL;
import Model.Departamento;

import java.util.ArrayList;

public class DepartamentoController {

    private final DepartamentoBLL departamentoBLL;

    public DepartamentoController() {
        this.departamentoBLL = new DepartamentoBLL(new DepartamentoDAL());
    }

    public Departamento registarDepartamento(String nome, String sigla) {
        return departamentoBLL.registarDepartamento(nome, sigla);
    }

    public ArrayList<Departamento> listarDepartamentos() {
        return departamentoBLL.listarDepartamentos();
    }

    public Departamento procurarDepartamento(String sigla) {
        return departamentoBLL.procurarDepartamento(sigla);
    }

    public void atualizarDepartamento(Departamento departamento, String novoNome) {
        departamentoBLL.atualizarDepartamento(departamento, novoNome);
    }

    public void removerDepartamento(Departamento departamento) {
        departamentoBLL.removerDepartamento(departamento);
    }

    public Departamento procurarPorSigla(String sigla) {
        return departamentoBLL.procurarDepartamento(sigla);
    }
}