package Controller;

import BLL.DocenteBLL;
import DAL.DocenteDAL;
import Model.Docente;

import java.util.ArrayList;

/**
 * Controlador responsável por gerir as operações relacionadas com a entidade Docente.
 * Serve de intermediário entre a camada de apresentação e a camada de negócio.
 */
public class DocenteController {
    private DocenteBLL docenteBLL;

    /**
     * Construtor do DocenteController.
     * Inicializa a camada de negócio com a respetiva camada de acesso a dados.
     */
    public DocenteController(DocenteBLL docenteBLL) {
        this.docenteBLL = docenteBLL;
    }

    /**
     * Regista um novo Docente no sistema.
     *
     * @param docente O docente a registar.
     * @throws IllegalArgumentException Se os dados forem inválidos ou já existir duplicado.
     */
    public void registarDocente(Docente docente) {
        docenteBLL.registarDocente(docente);
    }

    /**
     * Atualiza os dados de um Docente existente.
     *
     * @param docente O docente com os dados atualizados.
     * @throws IllegalArgumentException Se o docente não existir ou os dados forem inválidos.
     */
    public void atualizarDocente(Docente docente) {
        docenteBLL.atualizarDocente(docente);
    }

    /**
     * Remove um Docente do sistema pela sua sigla.
     *
     * @param sigla A sigla do docente a remover.
     * @throws IllegalArgumentException Se a sigla for inválida ou o docente não existir.
     */
    public void removerDocente(String sigla) {
        docenteBLL.removerDocente(sigla);
    }

    /**
     * Devolve a lista de todos os Docentes registados no sistema.
     *
     * @return Lista de docentes.
     */
    public ArrayList<Docente> listarDocentes() {
        return docenteBLL.listarDocentes();
    }

    /**
     * Procura um Docente pela sua sigla.
     *
     * @param sigla A sigla do docente a procurar.
     * @return O docente encontrado, ou null caso não exista.
     * @throws IllegalArgumentException Se a sigla for inválida.
     */
    public Docente procurarPorSigla(String sigla) {
        return docenteBLL.procurarPorSigla(sigla);
    }

    /**
     * Procura um Docente pelo seu NIF.
     *
     * @param nif O NIF do docente a procurar.
     * @return O docente encontrado, ou null caso não exista.
     * @throws IllegalArgumentException Se o NIF for inválido.
     */
    public Docente procurarPorNif(String nif) {
        return docenteBLL.procurarPorNif(nif);
    }

    /**
     * Procura um Docente pelo seu email.
     *
     * @param email O email do docente a procurar.
     * @return O docente encontrado, ou null caso não exista.
     * @throws IllegalArgumentException Se o email for inválido.
     */
    public Docente procurarPorEmail(String email) {
        return docenteBLL.procurarPorEmail(email);
    }

    /**
     * Autentica um Docente através do email e password.
     *
     * @param email    O email do docente.
     * @param password A password do docente.
     * @return O docente autenticado.
     * @throws IllegalArgumentException Se as credenciais forem inválidas ou incorretas.
     */
    public Docente autenticar(String email, String password) {
        return docenteBLL.autenticar(email, password);
    }
    /**
     * Altera a password do docente e marca o primeiro login como concluído.
     * @param docente O docente a alterar.
     * @param novaPassword A nova password.
     */
    public void alterarPassword(Docente docente, String novaPassword) {
        docenteBLL.alterarPassword(docente, novaPassword);
    }
}
