package DAL;

import Model.Docente;

import java.util.ArrayList;

/**
 * Contrato de acesso a dados de Docente.
 */
public interface IDocenteDAL {

    void adicionarDocente(Docente docente);

    boolean atualizarDocente(Docente docenteAtualizado);

    ArrayList<Docente> listarDocentes();

    void removerDocente(Docente docente);

    Docente procurarPorSigla(String sigla);

    Docente procurarPorNif(String nif);

    Docente procurarPorEmail(String email);
}
