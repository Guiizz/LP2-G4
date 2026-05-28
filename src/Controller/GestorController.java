package Controller;

import BLL.GestorBLL;
import Model.Gestor;

import java.time.LocalDate;
import java.util.ArrayList;

public class GestorController {

    private final GestorBLL gestorBLL;

    public GestorController(GestorBLL gestorBLL) {
        this.gestorBLL = gestorBLL;
    }

    public void registarGestor(String nome, LocalDate dataNascimento, String nif, String morada, String email, String password) {
        gestorBLL.registarGestor(nome, dataNascimento, nif, morada, email, password);
    }

    public void atualizarGestor(Gestor gestor) {
        gestorBLL.atualizarGestor(gestor);
    }

    public void removerGestor(String nif) {
        gestorBLL.removerGestor(nif);
    }

    public ArrayList<Gestor> listarGestores() {
        return gestorBLL.listarGestores();
    }

    public Gestor procurarPorNif(String nif) {
        return gestorBLL.procurarPorNif(nif);
    }

    public Gestor procurarPorEmail(String email) {
        return gestorBLL.procurarPorEmail(email);
    }

    public Gestor autenticar(String email, String password) {
        return gestorBLL.autenticar(email, password);
    }

    public void alterarPassword(Gestor gestor, String novaPassword) {
        gestorBLL.alterarPassword(gestor, novaPassword);
    }

    public void recuperarPassword(String email) {
        gestorBLL.recuperarPassword(email);
    }
}