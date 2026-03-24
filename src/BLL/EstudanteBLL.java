package BLL;

import DAL.EstudanteDAL;
import Model.Curso;
import Model.Estudante;
import java.time.LocalDate;
import static Utils.Utils.*;

public class EstudanteBLL {
    private EstudanteDAL estudanteDAL;

    public EstudanteBLL(){
        this.estudanteDAL = new EstudanteDAL();
    }

    public Estudante registarEstudante(String nome, LocalDate dataNascimento, String nif, String morada, Curso curso){
        if (validarNome(nome) && validarNif(nif) && curso != null){
            Estudante novoEstudante = new Estudante(nome, dataNascimento, nif, morada);
            return estudanteDAL.adicionarEstudante(novoEstudante);
        }
        return null;

    }
}
