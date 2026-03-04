package Model;

import java.time.LocalDate;

public class Pessoa {

    //Atributos da Classe

    private String nome;
    private LocalDate dataNascimento;
    private String nif;
    private String morada;
    private String email;
    private String password;

    //Construtores

    public Pessoa(String nome, LocalDate dataNascimento, String nif, String morada, String email, String password) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.nif = nif;
        this.morada = morada;
        this.email = email;
        this.password = password;


    }

    //Getters e Setters

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //Detalhar o toString

    @Override
    public String toString() {
        return super.toString();
    }
}

