package Model;

import java.time.LocalDate;
import java.util.ArrayList;
import Utils.PasswordUtils;

public class Estudante extends Utilizador {

    private static int contadorSequencial = 260001;
    private String numMecanografico;
    private int anoAtual;
    private String estado;
    private ArrayList<Inscricao> inscricoes;

    public Estudante(String nome, LocalDate dataNascimento, String nif, String morada) {
        super(nome, dataNascimento, nif, morada, "","");
        this.numMecanografico = String.valueOf(contadorSequencial++);
        String emailAutomatico = this.numMecanografico + "@issmf.pt";
        String passAutomatica = PasswordUtils.hashPassword("Issmf" + this.numMecanografico);
        this.setEmail(emailAutomatico);
        this.setPassword(passAutomatica);
        this.anoAtual = 1;
        this.estado = "ATIVO";
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

    public int getAnoAtual() {
        return anoAtual;
    }

    public void setAnoAtual(int anoAtual) {
        this.anoAtual = anoAtual;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado == null || estado.isBlank() ? "ATIVO" : estado;
    }

    public boolean isConcluido() {
        return "CONCLUIDO".equalsIgnoreCase(estado);
    }

    public ArrayList<Inscricao> getInscricoes() {
        return inscricoes;
    }

    public void setInscricoes(ArrayList<Inscricao> inscricoes) {
        this.inscricoes = inscricoes != null ? inscricoes : new ArrayList<>();
    }

    public void adicionarInscricao(Inscricao inscricao) {
        this.inscricoes.add(inscricao);
    }

    public void setNumMecanografico(String numMecanografico) {
        this.numMecanografico = numMecanografico;
    }

    @Override
    public String toString() {
        return "=== Ficha de Estudante ===\n" +
                "NºMecanográfico: " + getNumMecanografico() + "\n" +
                "Nome: " + getNome() + "\n" +
                "E-mail: " + getEmail() + "\n" +
                "Ano: " + getAnoAtual() + "\n" +
                "Estado: " + getEstado() + "\n" +
                "Data de Nascimento: " + getDataNascimento() + "\n" +
                "NIF " + getNif() + "\n" +
                "Morada: " + getMorada() + "\n" +
                "=========================";
    }
}