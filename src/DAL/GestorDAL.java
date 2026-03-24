package DAL;

import Model.Gestor;
import java.util.ArrayList;

/**
 * Camada (DAL) para a entidade Gestor
 * Responsável por armazenar e recuperar objetos Gestor em memória
 */
public class GestorDAL {
    private ArrayList<Gestor> gestores;

    /**
     * Construtor
     */
    public GestorDAL() {
        gestores = new ArrayList<>();
    }

    /**
     * Adicionar Gestor
     * @param gestor
     */
    public void adicionarGestor(Gestor gestor) {
        gestores.add(gestor);
    }

    /**
     * Atualizar Gestor
     * @param gestorAtualizado
     * @return
     */
    public boolean atualizarGestor(Gestor gestorAtualizado) {
        for (int i = 0; i < gestores.size(); i++) {
            if (gestores.get(i).getNif().equals(gestorAtualizado.getNif())) {
                gestores.set(i, gestorAtualizado);
                return true;
            }
        }
        return false;
    }

    /**
     * Listar Gestores
     * @return
     */
    public ArrayList<Gestor> listarGestores() {
        return new ArrayList<>(gestores);
    }

    /**
     * Remover Gestor
     * @param gestor
     */
    public void removerGestor(Gestor gestor) {
        gestores.remove(gestor);
    }

    /**
     * Procurar Gestor por NIF
     * @param nif
     * @return
     */
    public Gestor procurarPorNif(String nif) {
        for (Gestor gestor : gestores) {
            if (gestor.getNif().equals(nif)) {
                return gestor;
            }
        }
        return null;
    }

    /**
     * Procurar Gestor por email
     * @param email
     * @return
     */
    public Gestor procurarPorEmail(String email) {
        for (Gestor gestor : gestores) {
            if (gestor.getEmail().equals(email)) {
                return gestor;
            }
        }
        return null;
    }
}