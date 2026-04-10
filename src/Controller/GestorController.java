package Controller;

import BLL.GestorBLL;
import Model.Gestor;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Controlador responsável por gerir as operações relacionadas com a entidade Gestor.
 * Serve de intermediário entre a camada de apresentação e a camada de negócio.
 */
public class GestorController {

    private GestorBLL gestorBLL;

    /**
     * Construtor do GestorController.
     * Inicializa a camada de negócio do Gestor.
     */
    public GestorController() {
        this.gestorBLL = new GestorBLL();
    }

    /**
     * Regista um novo Gestor no sistema.
     *
     * @param nome Nome do gestor.
     * @param dataNascimento Data de nascimento do gestor.
     * @param nif NIF do gestor.
     * @param morada Morada do gestor.
     * @param email Email do gestor.
     * @param password Password do gestor.
     * @throws IllegalArgumentException Se os dados forem inválidos ou já existir duplicado.
     */
    public void registarGestor(String nome,
                               LocalDate dataNascimento,
                               String nif,
                               String morada,
                               String email,
                               String password) {
        gestorBLL.registarGestor(nome, dataNascimento, nif, morada, email, password);
    }

    /**
     * Atualiza os dados de um Gestor existente.
     *
     * @param gestor O gestor com os dados atualizados.
     * @throws IllegalArgumentException Se o gestor não existir ou os dados forem inválidos.
     */
    public void atualizarGestor(Gestor gestor) {
        gestorBLL.atualizarGestor(gestor);
    }

    /**
     * Remove um Gestor do sistema pelo NIF.
     *
     * @param nif O NIF do gestor a remover.
     * @throws IllegalArgumentException Se o NIF for inválido ou o gestor não existir.
     */
    public void removerGestor(String nif) {
        gestorBLL.removerGestor(nif);
    }

    /**
     * Devolve a lista de todos os Gestores registados no sistema.
     *
     * @return Lista de gestores.
     */
    public ArrayList<Gestor> listarGestores() {
        return gestorBLL.listarGestores();
    }

    /**
     * Procura um Gestor pelo seu NIF.
     *
     * @param nif O NIF do gestor a procurar.
     * @return O gestor encontrado, ou null caso não exista.
     * @throws IllegalArgumentException Se o NIF for inválido.
     */
    public Gestor procurarPorNif(String nif) {
        return gestorBLL.procurarPorNif(nif);
    }

    /**
     * Procura um Gestor pelo seu email.
     *
     * @param email O email do gestor a procurar.
     * @return O gestor encontrado, ou null caso não exista.
     * @throws IllegalArgumentException Se o email for inválido.
     */
    public Gestor procurarPorEmail(String email) {
        return gestorBLL.procurarPorEmail(email);
    }

    /**
     * Autentica um Gestor através do email e password.
     *
     * @param email O email do gestor.
     * @param password A password do gestor.
     * @return O gestor autenticado.
     * @throws IllegalArgumentException Se as credenciais forem inválidas ou incorretas.
     */
    public Gestor autenticar(String email, String password) {
        return gestorBLL.autenticar(email, password);
    }
}