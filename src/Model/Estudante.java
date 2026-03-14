package Model;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Representa um Estudante no sistema do Instituto Superior de Santa Maria da Feira (ISSMF).
 */
public class Estudante extends Utilizador {

    private static int contadorSequencial = 260001;
    private String numMecanografico;
    private int anoAtual;
    private Curso curso;
    private ArrayList<Inscricao> inscricoes;

    /**
     * Construtor da classe Estudante.
     * @param nome Nome do estudante.
     * @param dataNascimento Data de nascimento do estudante.
     * @param nif Número de Identificação fiscal do estudante.
     * @param morada Morada do estudante.
     * @param curso Curso que o estudante se está a inscrever.
     */
    public Estudante(String nome, LocalDate dataNascimento, String nif, String morada, Curso curso) {
        super(nome, dataNascimento, nif, morada, "","");
        this.numMecanografico = String.valueOf(contadorSequencial++);
        String emailAutomatico = this.numMecanografico + "@issmf.pt";
        String passAutomatica = "Issmf" + this.numMecanografico;
        this.setEmail(emailAutomatico);
        this.setPassword(passAutomatica);
        this.anoAtual = 1;
        this.curso = curso;
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
     * Obtém o curso em que o estudante está matriculado.
     * @return o objeto (Curso).
     */
    public Curso getCurso() {
        return curso;
    }

    /**
     * Altera o curso em que o estudante está matriculado.
     * @param curso O novo objeto Curso a associar ao estudante.
     */
    public void setCurso(Curso curso) {
        this.curso = curso;
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

    /**
     * Formato de texto da ficha estudante.
     * @return Uma String formatada com os detalhes do estudante.
     */
    @Override
    public String toString() {
        return "=== Ficha de Estudante ===\n" +
                "Nome: " + getNome() + "\n" +
                "NºMecanográfico: " + getNumMecanografico() + "\n" +
                "E-mail: " + getEmail() + "\n" +
                "Morada: " + getMorada() + "\n" +
                "Curso:" + getCurso().getNomeCurso() + "\n" +
                "Ano: " + getAnoAtual() + "\n" +
                "=========================";
    }
}
