package DAL;

import Model.Estudante;

import java.util.ArrayList;

/**
 * Contrato de acesso a dados de Estudante.
 */
public interface IEstudanteDAL {

    void adicionarEstudante(Estudante estudante);

    boolean atualizarEstudante(Estudante estudanteAtualizado);

    ArrayList<Estudante> listarEstudantes();

    void removerEstudante(String numMecanografico);

    Estudante procurarPorNumMecanografico(String numMecanografico);

    Estudante procurarPorNif(String nif);

    Estudante procurarPorEmail(String email);
}
