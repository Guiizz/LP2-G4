package Model;

import java.time.LocalDate;

public class Gestor extends Utilizador {
    public Gestor(String nome, LocalDate dataNascimento, String nif, String morada, String email, String password) {
        super(nome, dataNascimento, nif, morada, email, password);
    }

    @Override
    public String toString() {
        return "=== Ficha de Gestor ===\n" +
                "Nome: " + getNome() + "\n" +
                "Email: " + getEmail() + "\n" +
                "=======================";
    }

    public String toStringDetalhado() {
        return "=== Ficha de Gestor ===\n" +
                "Nome: " + getNome() + "\n" +
                "Data de Nascimento: " + getDataNascimento() + "\n" +
                "NIF: " + getNif() + "\n" +
                "Morada: " + getMorada() + "\n" +
                "Email: " + getEmail() + "\n" +
                "=======================";
    }
}