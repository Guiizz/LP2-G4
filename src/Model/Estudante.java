package Model;

import java.time.LocalDate;
import java.util.ArrayList;
import Utils.PasswordUtils;

/**
 * Representa um Estudante no sistema do Instituto Superior de Santa Maria da Feira (ISSMF).
 */
public class Estudante extends Utilizador {

    private static int contadorSequencial = 260001;
    private String numMecanografico;
    private int anoAtual;
    private ArrayList<Inscricao> inscricoes;
    /**
     * Construtor da classe Estudante.
     * @param nome Nome do estudante.
     * @param dataNascimento Data de nascimento do estudante.
     * @param nif Número de Identificação fiscal do estudante.
     * @param morada Morada do estudante.
     */
    public Estudante(String nome, LocalDate dataNascimento, String nif, String morada) {
        super(nome, dataNascimento, nif, morada, "","");
        this.numMecanografico = String.valueOf(contadorSequencial++);
        String emailAutomatico = this.numMecanografico + "@issmf.pt";
        String passAutomatica = PasswordUtils.hashPassword("Issmf" + this.numMecanografico);
        this.setEmail(emailAutomatico);
        this.setPassword(passAutomatica);
        this.anoAtual = 1;
        this.inscricoes = new ArrayList<>();
    }

    /**
     * Obtém o valor atual do contador para o transformar no número mecanográfico.
     * @return O proximo número mecanográfico a ser atribuido.
     */
    public static int getContadorSequencial() {
        return contadorSequencial;
    }

    /**
     *Define o o valor do contador sequencial..
     * @param contadorSequencial O novo valor base para o contador.
     */
    public static void setContadorSequencial(int contadorSequencial) {
        Estudante.contadorSequencial = contadorSequencial;
    }

    /**
     * Obtém O número mecanográfico único do estudante.
     * @return O número mecanográfico (ex: "260001").
     */
    public String getNumMecanografico() {
        return numMecanografico;
    }

    /**
     * Obtém o ano curricular atual que o estudante está a frequentar.
     * @return O ano atual (ex: 1, 2 ou 3).
     */
    public int getAnoAtual() {
        return anoAtual;
    }

    /**
     * Atualiza o ano curricular atual do estudante.
     * @param anoAtual O novo ano curricular (ex: 2 ou 3).
     */
    public void setAnoAtual(int anoAtual) {
        this.anoAtual = anoAtual;
    }

    /**
     * Obtém o histórico de inscrições do estudante.
     * @return Uma lista com todas as inscrições associadas ao estudante.
     */
    public ArrayList<Inscricao> getInscricoes() {
        return inscricoes;
    }

    /**
     * Adiciona uma nova inscrição ao histórico do estudante.
     * @param inscricao A inscrição a ser adicionada.
     */
    public void adicionarInscricao(Inscricao inscricao) {
        this.inscricoes.add(inscricao);
    }

    public void setNumMecanografico(String numMecanografico) {
        this.numMecanografico = numMecanografico;
    }

    /**
     * Formato de texto da ficha estudante.
     * @return Uma String formatada com os detalhes do estudante.
     */
    @Override
    public String toString() {
        return "=== Ficha de Estudante ===\n" +
                "NºMecanográfico: " + getNumMecanografico() + "\n" +
                "Nome: " + getNome() + "\n" +
                "E-mail: " + getEmail() + "\n" +
                "Ano: " + getAnoAtual() + "\n" +
                "Data de Nascimento: " + getDataNascimento() + "\n" +
                "NIF " + getNif() + "\n" +
                "Morada: " + getMorada() + "\n" +
                "=========================";
    }
}
