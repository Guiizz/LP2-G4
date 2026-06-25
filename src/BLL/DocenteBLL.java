package BLL;

import DAL.EstudanteDAL;
import DAL.IEstudanteDAL;
import Utils.Utils;
import Utils.ServicoEmail;
import DAL.DocenteDAL;
import DAL.IDocenteDAL;
import Model.Docente;
import Utils.PasswordUtils;
import java.util.ArrayList;

public class DocenteBLL {
    private IDocenteDAL docenteDAL;
    private IEstudanteDAL estudanteDAL;

    /**
     * Construtor da classe DocenteBLL.
     * Inicializa a camada de acesso a dados.
     */
    public DocenteBLL(IDocenteDAL docenteDAL, IEstudanteDAL estudanteDAL) {
        this.docenteDAL = docenteDAL;
        this.estudanteDAL = estudanteDAL;
    }

    /**
     * Regista um novo Docente no sistema.
     * O email e a password são gerados automaticamente pelo construtor do Docente
     * com base na sigla, pelo que não necessitam de ser fornecidos externamente.
     * Valida os campos obrigatórios e garante unicidade de NIF, email e sigla.
     * @param docente O docente a registar.
     * @throws IllegalArgumentException Se o docente for nulo, os dados forem inválidos ou já existir duplicado.
     */
    public void registarDocente(Docente docente) {
        if (docente == null) {
            throw new IllegalArgumentException("Docente não pode ser nulo.");
        }

        Utils.validarNome(docente.getNome());
        Utils.validarNif(docente.getNif());
        Utils.validarDataNascimento(docente.getDataNascimento());
        Utils.validarMorada(docente.getMorada());
        Utils.validarSigla(docente.getSigla());
        Utils.validarEmail(docente.getEmail());
        Utils.validarPassword(docente.getPassword());

        if (docenteDAL.procurarPorNif(docente.getNif()) != null) {
            throw new IllegalArgumentException("Já existe um docente com o NIF: " + docente.getNif());
        }

        if (estudanteDAL.procurarPorNif(docente.getNif()) != null) {
            throw new IllegalArgumentException("Este NIF já existe no sistema como estudante.");
        }

        if (docenteDAL.procurarPorEmail(docente.getEmail()) != null) {
            throw new IllegalArgumentException("Já existe um docente com o email: " + docente.getEmail());
        }

        if (docenteDAL.procurarPorSigla(docente.getSigla()) != null) {
            throw new IllegalArgumentException("Já existe um docente com a sigla: " + docente.getSigla());
        }

        String passwordPlainText = "Issmf" + docente.getSigla();
        docenteDAL.adicionarDocente(docente);
        ServicoEmail.enviarCredenciais(
                docente.getEmail(),
                passwordPlainText,       // ← texto simples
                "Docente"
        );
    }

    /**
     * Atualiza os dados de um Docente existente.
     * Não é permitido alterar o NIF nem a sigla.
     * Apenas é possível atualizar a morada e as unidades lecionadas.
     * @param docenteAtualizado O docente com os dados atualizados.
     * @throws IllegalArgumentException Se o docente não existir ou os dados forem inválidos.
     * @throws IllegalStateException    Se não for possível efetuar a atualização.
     */
    public void atualizarDocente(Docente docenteAtualizado) {
        if (docenteAtualizado == null) {
            throw new IllegalArgumentException("Docente não pode ser nulo.");
        }

        Docente existente = docenteDAL.procurarPorSigla(docenteAtualizado.getSigla());
        if (existente == null) {
            throw new IllegalArgumentException("Docente com sigla '" + docenteAtualizado.getSigla() + "' não encontrado.");
        }

        Utils.validarNome(docenteAtualizado.getNome());
        Utils.validarMorada(docenteAtualizado.getMorada());

        boolean atualizado = docenteDAL.atualizarDocente(docenteAtualizado);
        if (!atualizado) {
            throw new IllegalStateException("Não foi possível atualizar o docente.");
        }
    }

    /**
     * Remove um Docente do sistema pela sua sigla.
     * @param sigla A sigla do docente a remover.
     * @throws IllegalArgumentException Se a sigla for inválida ou o docente não existir.
     */
    public void removerDocente(String sigla) {
        Utils.validarSigla(sigla);

        Docente docente = docenteDAL.procurarPorSigla(sigla);
        if (docente == null) {
            throw new IllegalArgumentException("Docente com sigla '" + sigla + "' não encontrado.");
        }

        docenteDAL.removerDocente(docente);
    }

    /**
     * Devolve a lista de todos os Docentes registados no sistema.
     * @return Lista de docentes.
     */
    public ArrayList<Docente> listarDocentes() {
        return docenteDAL.listarDocentes();
    }

    /**
     * Procura um Docente pela sua sigla.
     * @param sigla A sigla do docente a procurar.
     * @return O docente encontrado, ou null caso não exista.
     * @throws IllegalArgumentException Se a sigla for inválida.
     */
    public Docente procurarPorSigla(String sigla) {
        Utils.validarSigla(sigla);
        return docenteDAL.procurarPorSigla(sigla);
    }

    /**
     * Procura um Docente pelo seu NIF.
     * @param nif O NIF do docente a procurar.
     * @return O docente encontrado, ou null caso não exista.
     * @throws IllegalArgumentException Se o NIF for inválido.
     */
    public Docente procurarPorNif(String nif) {
        Utils.validarNif(nif);
        return docenteDAL.procurarPorNif(nif);
    }

    /**
     * Procura um Docente pelo seu email.
     * @param email O email do docente a procurar.
     * @return O docente encontrado, ou null caso não exista.
     * @throws IllegalArgumentException Se o email for inválido.
     */
    public Docente procurarPorEmail(String email) {
        Utils.validarEmail(email);
        return docenteDAL.procurarPorEmail(email);
    }

    /**
     * Autentica um Docente através do email e password.
     * @param email    O email do docente.
     * @param password A password do docente.
     * @return O docente autenticado.
     * @throws IllegalArgumentException Se as credenciais forem inválidas ou incorretas.
     */
    public Docente autenticar(String email, String password) {
        Utils.validarEmail(email);
        Utils.validarPassword(password);

        Docente docente = docenteDAL.procurarPorEmail(email);
        if (docente == null || !PasswordUtils.verificarPassword(password, docente.getPassword())) {
            throw new IllegalArgumentException("Email ou password incorretos.");
        }

        return docente;
    }

    public void recuperarPassword(String email) {
        Docente docente = null;
        for (Docente d : docenteDAL.listarDocentes()) {
            if (d.getEmail().equalsIgnoreCase(email)) {
                docente = d;
                break;
            }
        }
        if (docente == null) {
            throw new IllegalArgumentException("Não existe nenhum docente com esse email.");
        }

        String passwordTemporaria = "Issmf" + docente.getSigla() + "Tmp";
        docente.setPassword(PasswordUtils.hashPassword(passwordTemporaria));
        docente.setPrimeiroLogin(true);
        docenteDAL.atualizarDocente(docente);
        ServicoEmail.enviarPasswordTemporaria(email, passwordTemporaria, "Docente");
    }

    /**
     * Altera a password do docente e marca o primeiro login como concluído.
     * @param docente O docente a alterar.
     * @param novaPassword A nova password.
     */
    public void alterarPassword(Docente docente, String novaPassword) {
        Utils.validarPassword(novaPassword);
        docente.setPassword(PasswordUtils.hashPassword(novaPassword));
        docente.setPrimeiroLogin(false);
        docenteDAL.atualizarDocente(docente);
    }
}
