package BLL;

import DAL.IDocenteDAL;
import DAL.IEstudanteDAL;
import DAL.GestorDAL;
import DAL.IGestorDAL;
import Model.Gestor;
import Utils.Utils;
import Utils.ServicoEmail;
import Utils.PasswordUtils;
import java.time.LocalDate;
import java.util.ArrayList;


public class GestorBLL {
    private IGestorDAL gestorDAL;
    private IDocenteDAL docenteDAL;
    private IEstudanteDAL estudanteDAL;


    public GestorBLL(IGestorDAL gestorDAL, IDocenteDAL docenteDAL, IEstudanteDAL estudanteDAL) {
        this.gestorDAL = gestorDAL;
        this.docenteDAL = docenteDAL;
        this.estudanteDAL = estudanteDAL;
    }

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

        if (docenteDAL.procurarPorNif(nif) != null) {
            throw new IllegalArgumentException("Este NIF já existe no sistema como docente.");
        }

        if (estudanteDAL.procurarPorNif(nif) != null ) {
            throw new IllegalArgumentException("Este NIF já existe no sistema como estudante.");
        }
        if (gestorDAL.procurarPorEmail(email) != null) {
            throw new IllegalArgumentException("Já existe um gestor com o email: " + email);
        }

        Gestor gestor = new Gestor(nome, dataNascimento, nif, morada, email, PasswordUtils.hashPassword(password));
        gestorDAL.adicionarGestor(gestor);

        ServicoEmail.enviarCredenciais(
                email,
                password,
                "Gestor"
        );
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
        if (gestor == null || !PasswordUtils.verificarPassword(password, gestor.getPassword())) {
            throw new IllegalArgumentException("Email ou password incorretos.");
        }

        return gestor;
    }
    /**
     * Altera a password do gestor e marca o primeiro login como concluído.
     * @param gestor O gestor a alterar.
     * @param novaPassword A nova password.
     */
    public void alterarPassword(Gestor gestor, String novaPassword) {
        Utils.validarPassword(novaPassword);
        gestor.setPassword(PasswordUtils.hashPassword(novaPassword));
        gestor.setPrimeiroLogin(false);
        gestorDAL.atualizarGestor(gestor);
    }

    public void recuperarPassword(String email) {
        Gestor gestor = null;
        for (Gestor g : gestorDAL.listarGestores()) {
            if (g.getEmail().equalsIgnoreCase(email)) {
                gestor = g;
                break;
            }
        }
        if (gestor == null) {
            throw new IllegalArgumentException("Não existe nenhum gestor com esse email.");
        }

        String nifParcial = gestor.getNif().length() >= 4 ? gestor.getNif().substring(0, 4) : gestor.getNif();
        String passwordTemporaria = "IssmfGestor" + nifParcial + "Tmp";
        gestor.setPassword(PasswordUtils.hashPassword(passwordTemporaria));
        gestor.setPrimeiroLogin(true);
        gestorDAL.atualizarGestor(gestor);
        ServicoEmail.enviarPasswordTemporaria(email, passwordTemporaria, "Gestor");
    }
}