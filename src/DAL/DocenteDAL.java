package DAL;

import BLL.DocenteBLL;
import Model.Docente;

import java.util.ArrayList;

/**
 * Camada (DAL) para a entidade Docente
 * Responsável por armazenar e recuperar objeto Docente em memória
 */
public class DocenteDAL {
    private ArrayList<Docente> docentes;

    /**
     *
     */
    public DocenteDAL() {
        docentes = new ArrayList<>();
    }

    /**
     * Adicionar Docente
     * @param docente
     */
    public void adicionarDocente(Docente docente){
        docentes.add(docente);
    }

    /**
     * Atualizar Docente
     * @param docenteatualizado
     * @return
     */
    public boolean atualizarDocente(Docente docenteatualizado){
        for (int i = 0; i < docentes.size(); i++) {
            if (docentes.get(i).getSigla().equals(docenteatualizado.getSigla())) {
                docentes.set(i, docenteatualizado);
                return true;
            }
        }
        return false;
    }

    /**
     * Listar Docentes
     * @return
     */
    public ArrayList<Docente> listarDocentes() {
        return new ArrayList<>(docentes);
    }

    /**
     * Remover Docente
     * @param docente
     */
    public void removerDocente(Docente docente){
        docentes.remove(docente);
    }

    /**
     * Procurar Docente por sigla
     * @param sigla
     * @return
     */
    public Docente procurarPorSigla(String sigla){
        for (Docente docente : docentes) {
            if (docente.getSigla().equals(sigla)) {
                return docente;
            }
        }
        return null;
    }

    /**
     * Procurar Docente por NIF
     * @param nif
     * @return
     */
    public Docente procurarPorNif(String nif){
        for (Docente docente : docentes) {
            if (docente.getNif().equals(nif)) {
                return docente;
            }
        }
        return null;
    }

    /**
     * Procurar Docente por email
     * @param email
     * @return
     */
    public Docente procurarPorEmail(String email){
        for (Docente docente : docentes) {
            if (docente.getEmail().equals(email)) {
                return docente;
            }
        }
        return null;
    }


}
