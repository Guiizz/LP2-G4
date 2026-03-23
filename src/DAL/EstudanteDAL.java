package DAL;

import Model.Estudante;
import java.util.ArrayList;

public class EstudanteDAL {
    private ArrayList<Estudante> listaEstudantes;

    public EstudanteDAL(){
        listaEstudantes = new ArrayList<>();
    }

    public Estudante adicionarEstudante(Estudante estudante){
        try {
            listaEstudantes.add(estudante);
            return estudante;
        } catch (Exception e) {
            System.out.println("Apenas para Gestores!");
        }
        return null;
    }

    public Estudante procurarPorNumMecanografico(String numMecanografico){
        boolean encontrado = false;
        Estudante estudanteEncontrado = null;

        for (int i = 0; i <listaEstudantes.size() && !encontrado;i++){
            if (listaEstudantes.get(i).getNumMecanografico().equalsIgnoreCase(numMecanografico)){
                encontrado = true;
                estudanteEncontrado = listaEstudantes.get(i);
            }

        }
        return estudanteEncontrado;
    }

}
