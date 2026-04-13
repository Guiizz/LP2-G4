package Controller;

import BLL.DepartamentoBLL;
import Model.Departamento;

import java.util.ArrayList;

/**
 * Controlador responsável pelas operações relacionadas com Departamento.
 */
public class DepartamentoController {

    private DepartamentoBLL departamentoBLL;

    public DepartamentoController(DepartamentoBLL departamentoBLL) {
        this.departamentoBLL = departamentoBLL;
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
}