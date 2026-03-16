package Model;

import java.time.LocalDate;

public class Gestor extends Utilizador {
    public Gestor(String nome, LocalDate dataNascimento, String nif, String morada, String email, String password) {
        super(nome, dataNascimento, nif, morada, email, password);
    }

    @Override
    public String toString() {
        return "Gestor{" + super.toString() + "}";
    }
}