package Model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Estudante extends Utilizador {

    private static int contadorSequencial = 260001;
    private String numMecanografico;
    private int anoAtual;
    private Curso curso;
    private ArrayList<Inscricao> inscricoes;

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

    public static int getContadorSequencial() {
        return contadorSequencial;
    }

    public static void setContadorSequencial(int contadorSequencial) {
        Estudante.contadorSequencial = contadorSequencial;
    }

    public String getNumMecanografico() {
        return numMecanografico;
    }

    public void setNumMecanografico(String numMecanografico) {
        this.numMecanografico = numMecanografico;
    }

    public int getAnoAtual() {
        return anoAtual;
    }

    public void setAnoAtual(int anoAtual) {
        this.anoAtual = anoAtual;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public ArrayList<Inscricao> getInscricoes() {
        return inscricoes;
    }

    public void adicionarInscricao(Inscricao inscricao) {
        this.inscricoes.add(inscricao);
    }

    @Override
    public String toString() {
        return "=== Ficha de Estudante ===\n" +
                "Nome: " + getNome() + "\n" +
                "NºMecanográfico: " + getNumMecanografico() + "\n" +
                "E-mail: " + getEmail() + "\n" +
                "Morada: " + getMorada() + "\n" +
                "Curso:" + getCurso() + "\n" +
                "Ano: " + getAnoAtual() + "\n" +
                "=========================";
    }
}
