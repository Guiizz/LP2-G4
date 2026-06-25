package DAL;

import Model.RegistoAula;

import java.time.LocalDate;
import java.util.List;

public interface IRegistoAulaDAL {

    void adicionar(RegistoAula registo);

    boolean existe(String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String horaInicio);

    RegistoAula procurar(String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String horaInicio);

    /** Persiste o estado atual de toda a lista (CSV). */
    void guardar();

    /** Persiste o estado atual de um registo específico (BD). */
    void guardarRegisto(RegistoAula registo);

    List<RegistoAula> listarPorDocente(String siglaDocente);

    List<RegistoAula> listarPorUCeCurso(String nomeUC, String nomeCurso, int anoLetivo);

    List<RegistoAula> listarTodas();
}
