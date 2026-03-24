package BLL;

import DAL.GestorDAL;
import Model.Gestor;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Camada (BLL) para a entidade Gestor
 * Responsável pela lógica de negócio associada ao Gestor
 */
public class GestorBLL {
    private GestorDAL gestorDAL;

    /**
     * Construtor
     */
    public GestorBLL() {
        gestorDAL = new GestorDAL();
    }

    /**
     * Registar Gestor
     * @param nome
     * @param dataNascimento
     * @param nif
     * @param morada
     * @param email
     * @param password
     * @return
     */
    public boolean registarGestor(String nome, LocalDate dataNascimento, String nif, String morada, String email, String password) {
        if (gestorDAL.procurarPorNif(nif) != null) {
            return false;
        }
        if (gestorDAL.procurarPorEmail(email) != null) {
            return false;
        }
        Gestor gestor = new Gestor(nome, dataNascimento, nif, morada, email, password);
        gestorDAL.adicionarGestor(gestor);
        return true;
    }

    /**
     * Atualizar Gestor
     * @param gestorAtualizado
     * @return
     */
    public boolean atualizarGestor(Gestor gestorAtualizado) {
        return gestorDAL.atualizarGestor(gestorAtualizado);
    }

    /**
     * Remover Gestor
     * @param nif
     * @return
     */
    public boolean removerGestor(String nif) {
        Gestor gestor = gestorDAL.procurarPorNif(nif);
        if (gestor == null) {
            return false;
        }
        gestorDAL.removerGestor(gestor);
        return true;
    }

    /**
     * Listar Gestores
     * @return
     */
    public ArrayList<Gestor> listarGestores() {
        return gestorDAL.listarGestores();
    }

    /**
     * Procurar Gestor por NIF
     * @param nif
     * @return
     */
    public Gestor procurarPorNif(String nif) {
        return gestorDAL.procurarPorNif(nif);
    }

    /**
     * Procurar Gestor por email
     * @param email
     * @return
     */
    public Gestor procurarPorEmail(String email) {
        return gestorDAL.procurarPorEmail(email);
    }

    /**
     * Autenticar Gestor por email e password
     * @param email
     * @param password
     * @return
     */
    public Gestor autenticar(String email, String password) {
        Gestor gestor = gestorDAL.procurarPorEmail(email);
        if (gestor == null) {
            return null;
        }
        if (!gestor.getPassword().equals(password)) {
            return null;
        }
        return gestor;
    }
}