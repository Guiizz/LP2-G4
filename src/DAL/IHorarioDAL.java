package DAL;

import Model.Horario;

import java.util.List;

public interface IHorarioDAL {

    Horario procurarOuCriar(String nomeCurso, int anoCurricular, int anoLetivo);

    List<Horario> listarTodos();

    List<Horario> listarPorCurso(String nomeCurso);

    void guardar(Horario horario);
}
