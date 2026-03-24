package DAL;

import Model.UnidadeCurricular;
import java.util.ArrayList;

public class UnidadeCurricularDAL {
    private ArrayList<UnidadeCurricular> unidades;

    public UnidadeCurricularDAL() {
        unidades = new ArrayList<>();
    }

    public void adicionarUnidade(UnidadeCurricular unidade) {
        unidades.add(unidade);
    }

    public boolean atualizarUnidade(UnidadeCurricular unidadeatualizado) {
        for (int i = 0; i < unidades.size(); i++) {
            if (unidades.get(i).equals(unidadeatualizado)){
                unidades.set(i, unidadeatualizado);
                return true;
            }
        }
        return false;
    }

    public ArrayList<UnidadeCurricular> listarUnidades() {
        return new ArrayList<>(unidades);
    }

    public void removerUnidade(UnidadeCurricular unidade) {
        unidades.remove(unidade);
    }
}
