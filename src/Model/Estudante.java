package Model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Estudante extends Utilizador {
    private static int contadorSequencial = 260001;
    private String numMecanografico;
    private int anoCurricular;
    private Curso curso;

    public Estudante(String nome, LocalDate dataNascimento, String nif, String morada, String email, String password, String numMecanografico, int anoCurricular, Curso curso) {
        super(nome, dataNascimento, nif, morada, email, password);
        this.numMecanografico = numMecanografico;
        this.anoCurricular = anoCurricular;
        this.curso = curso;
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

    public void setCurso(Curso curso) {
        this.curso = curso;
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
