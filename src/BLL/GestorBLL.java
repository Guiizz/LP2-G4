package BLL;

import DAL.GestorDAL;
import Model.Gestor;
import Utils.Utils;

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
     * Regista um novo Gestor no sistema.
     * Valida todos os dados e garante unicidade de NIF e email.
     * @param nome O nome do gestor.
     * @param dataNascimento A data de nascimento do gestor.
     * @param nif O NIF do gestor.
     * @param morada A morada do gestor.
     * @param email O email do gestor.
     * @param password A palavra-chave do gestor.
     * @throws IllegalArgumentException Se alguma validação falhar ou já existir duplicado.
     */
    public void registarGestor(String nome, LocalDate dataNascimento, String nif, String morada, String email, String password) {
        Utils.validarNome(nome);
        Utils.validarDataNascimento(dataNascimento);
        Utils.validarNif(nif);
        Utils.validarMorada(morada);
        Utils.validarEmail(email);
        Utils.validarPassword(password);

        if (gestorDAL.procurarPorNif(nif) != null) {
            throw new IllegalArgumentException("Já existe um gestor com o NIF: " + nif);
        }
        if (gestorDAL.procurarPorEmail(email) != null) {
            throw new IllegalArgumentException("Já existe um gestor com o email: " + email);
        }

        Gestor gestor = new Gestor(nome, dataNascimento, nif, morada, email, password);
        gestorDAL.adicionarGestor(gestor);
    }

    /**
     * Atualiza os dados de um Gestor existente.
     * @param gestorAtualizado O gestor com os dados atualizados.
     * @throws IllegalArgumentException Se o gestor não existir ou os dados forem inválidos.
     */
    public void atualizarGestor(Gestor gestorAtualizado) {
        if (gestorAtualizado == null) {
            throw new IllegalArgumentException("Gestor não pode ser nulo.");
        }

        Gestor existente = gestorDAL.procurarPorNif(gestorAtualizado.getNif());
        if (existente == null) {
            throw new IllegalArgumentException("Gestor com NIF '" + gestorAtualizado.getNif() + "' não encontrado.");
        }

        Utils.validarNome(gestorAtualizado.getNome());
        Utils.validarMorada(gestorAtualizado.getMorada());

        gestorDAL.atualizarGestor(gestorAtualizado);
    }

    /**
     * Remove um Gestor do sistema pelo NIF.
     * @param nif O NIF do gestor a remover.
     * @throws IllegalArgumentException Se o NIF for inválido ou o gestor não existir.
     */
    public void removerGestor(String nif) {
        Utils.validarNif(nif);

        Gestor gestor = gestorDAL.procurarPorNif(nif);
        if (gestor == null) {
            throw new IllegalArgumentException("Gestor com NIF '" + nif + "' não encontrado.");
        }

        gestorDAL.removerGestor(gestor);
    }

    /**
     * Devolve a lista de todos os Gestores registados no sistema.
     * @return Lista de gestores.
     */
    public ArrayList<Gestor> listarGestores() {
        return gestorDAL.listarGestores();
    }

    /**
     * Procura um Gestor pelo seu NIF.
     * @param nif O NIF do gestor a procurar.
     * @return O gestor encontrado, ou null caso não exista.
     * @throws IllegalArgumentException Se o NIF for inválido.
     */
    public Gestor procurarPorNif(String nif) {
        Utils.validarNif(nif);
        return gestorDAL.procurarPorNif(nif);
    }

    /**
     * Procura um Gestor pelo seu email.
     * @param email O email do gestor a procurar.
     * @return O gestor encontrado, ou null caso não exista.
     * @throws IllegalArgumentException Se o email for inválido.
     */
    public Gestor procurarPorEmail(String email) {
        Utils.validarEmail(email);
        return gestorDAL.procurarPorEmail(email);
    }

    /**
     * Autentica um Gestor através do email e password.
     * @param email O email do gestor.
     * @param password A password do gestor.
     * @return O gestor autenticado.
     * @throws IllegalArgumentException Se as credenciais forem inválidas ou incorretas.
     */
    public Gestor autenticar(String email, String password) {
        Utils.validarEmail(email);
        Utils.validarPassword(password);

        Gestor gestor = gestorDAL.procurarPorEmail(email);
        if (gestor == null || !gestor.getPassword().equals(password)) {
            throw new IllegalArgumentException("Email ou password incorretos.");
        }

        return gestor;
    }
}