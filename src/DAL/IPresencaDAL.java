package DAL;

import Model.Presenca;

import java.time.LocalDate;
import java.util.List;

public interface IPresencaDAL {

    void adicionar(Presenca presenca);

    boolean existe(String numMecanografico, String nomeUC, String nomeCurso,
                   int anoLetivo, LocalDate data, String horaInicio);

    List<Presenca> listarPorEstudante(String numMecanografico);

    List<Presenca> listarPorUCeCurso(String nomeUC, String nomeCurso, int anoLetivo);

    List<Presenca> listarTodas();
}
