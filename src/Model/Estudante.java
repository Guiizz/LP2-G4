package Model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Estudante extends Utilizador {
    private static int contadorSequencial = 260001;
    private String numMecanografico;
    private int anoCurricular;
    private Curso curso;
    private List <Avaliacao> notas;

    public Estudante(String nome, LocalDate dataNascimento, String nif, String morada, String email, String password, String numMecanografico, int anoLetivo, String curso, double notas) {
        super(nome, dataNascimento, nif, morada, email, password);

        this.numMecanografico = "" + contadorSequencial++;
        this.setEmail(this.numMecanografico + "@issmf.pt");
        this.setPassword("pass123");
        this.anoCurricular = 1;
        this.curso = curso;
        this.notas = new ArrayList<>();
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

    public int getAnoCurricular() {
        return anoCurricular;
    }

    public void setAnoCurricular(int anoCurricular) {
        this.anoCurricular = anoCurricular;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public List<Avaliacao> getNotas() {
        return notas;
    }

    public void setNotas(double notas) {
        this.notas = notas;
    }

    @Override
    public String toString() {
        return "=== Ficha de Estudante ===\n" +
                "Nome: " + getNome() + "\n" +
                "NºMecanográfico: " + this.numMecanografico + "\n" +
                "E-mail: " + getEmail() + "\n" +
                "Morada: " + getMorada() + "\n" +
                "Curso:" + getCurso() + "\n" +
                "Ano Curricular: " + getAnoCurricular() + "\n" +
                "=========================";
    }
}
