package DAL;

import Model.Departamento;
import java.util.ArrayList;


public class DepartamentoDAL {
    private ArrayList<Departamento> listaDepartamentos;

    public DepartamentoDAL(){
        listaDepartamentos = new ArrayList<>();
    }

    public Departamento adicionarDepartamento(Departamento departamento){
        listaDepartamentos.add(departamento);
        return departamento;
    }

    public Departamento procurarPorSigla(String sigla){
        for (Departamento d : listaDepartamentos){
            if (d.getSigla().equalsIgnoreCase(sigla)){
                return d;
            }
        }
        return null;
    }

    public ArrayList<Departamento> listarDepartamentos(){
        return listaDepartamentos;
    }
}
