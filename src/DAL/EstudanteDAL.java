package DAL;

import Model.Estudante;
import java.util.ArrayList;
/**
 * Camada (DAL) para a entidade Estudante.
 * Responsável por armazenar e recuperar objetos Estudante em memória.
 */
public class EstudanteDAL {
    private ArrayList<Estudante> estudantes;

    /**
     * Construtor da classe EstudanteDAL
     */
    public EstudanteDAL(){
        estudantes = new ArrayList<>();
    }

    /**
     * Adiciona um novo estudante a lista.
     * @param estudante O novo estudante.
     */
   public void adicionarEstudante(Estudante estudante){
        estudantes.add(estudante);
   }

    /**
     * Lista de estudantes registados no sistema.
     * @return Lista de estudantes.
     */
   public ArrayList<Estudante> listarEstudantes(){
        return new ArrayList<>(estudantes);
   }

    /**
     * Atualiza os dados do estudante, identificado pelo número mecanográfico.
     * @param estudanteAtualizado O Estudante com os dados atualizados.
     * @return True se for atualizado com sucesso e False se não for encontrado.
     */
   public boolean atualizarEstudante(Estudante estudanteAtualizado){
       for (int i = 0; i < estudantes.size();i++){
           if(estudantes.get(i).getNumMecanografico().equals(estudanteAtualizado.getNumMecanografico())){
               estudantes.set(i, estudanteAtualizado);
               return true;
           }
       }
       return false;
   }

    /**
     * Remove o estudante da lista pelo número mecanográfico.
     * @param estudante O estudante a remover.
     */
    public void removerEstudante(Estudante estudante){
        estudantes.remove(estudante);
    }

    /**
     * Procura um estudante por número mecanográfico.
     * @param numMecanografico O número mecanográfico a procurar.
     * @return O estudante encontrado ou null se não existir.
     */
    public Estudante procurarPorNumMecanografico(String numMecanografico) {
        for (Estudante e : estudantes) {
            if (e.getNumMecanografico().equals(numMecanografico)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Procura o estudante por NIF
     * @param nif O NIF a procurar.
     * @return O estudante encontrado ou null se não existir.
     */
    public Estudante procurarPorNif(String nif){
        for (Estudante e : estudantes) {
            if (e.getNif().equals(nif)){
                return e;
            }
        }
        return null;
    }

    /**
     * Procura o estudante por E-mail.
     * @param email O e-mail a procurar.
     * @return O estudante encontrado ou null se não existir.
     */
    public Estudante procurarPorEmail(String email){
        for (Estudante e :estudantes){
            if (e.getEmail().equals(email)){
                return e;
            }
        }
        return null;
    }
}
