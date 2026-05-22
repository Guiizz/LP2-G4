package Controller;

import BLL.DocenteBLL;
import DAL.DocenteDAL;
import DAL.EstudanteDAL;
import DAL.UnidadeCurricularDAL;
import Model.Docente;

import java.util.ArrayList;

public class DocenteController {

    private final DocenteBLL docenteBLL;

    public DocenteController() {
        this.docenteBLL = new DocenteBLL(new DocenteDAL(new UnidadeCurricularDAL()), new EstudanteDAL());
    }

    public Docente registarDocente(Docente docente) {
        docenteBLL.registarDocente(docente);
        return docente;
    }

    public void atualizarDocente(Docente docente) {
        docenteBLL.atualizarDocente(docente);
    }

    public void removerDocente(String sigla) {
        docenteBLL.removerDocente(sigla);
    }

    public ArrayList<Docente> listarDocentes() {
        return docenteBLL.listarDocentes();
    }

    public Docente procurarPorSigla(String sigla) {
        return docenteBLL.procurarPorSigla(sigla);
    }

    public Docente procurarPorNif(String nif) {
        return docenteBLL.procurarPorNif(nif);
    }

    public Docente procurarPorEmail(String email) {
        return docenteBLL.procurarPorEmail(email);
    }

    public Docente autenticar(String email, String password) {
        return docenteBLL.autenticar(email, password);
    }

    public void alterarPassword(Docente docente, String novaPassword) {
        docenteBLL.alterarPassword(docente, novaPassword);
    }

    public void recuperarPassword(String email) {
        docenteBLL.recuperarPassword(email);
    }

    public boolean nifJaExiste(String nif) {
        try {
            return docenteBLL.procurarPorNif(nif) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}