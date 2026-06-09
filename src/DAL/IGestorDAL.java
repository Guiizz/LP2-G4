package DAL;

import Model.Gestor;

import java.util.ArrayList;

/**
 * Contrato de acesso a dados de Gestor.
 */
public interface IGestorDAL {

    void adicionarGestor(Gestor gestor);

    boolean atualizarGestor(Gestor gestorAtualizado);

    ArrayList<Gestor> listarGestores();

    void removerGestor(Gestor gestor);

    Gestor procurarPorNif(String nif);

    Gestor procurarPorEmail(String email);
}
