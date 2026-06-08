package Controller;

import BLL.PresencaBLL;
import Model.RegistoAula;
import Model.Presenca;

import java.time.LocalDate;
import java.util.List;

public class PresencaController {

    private final PresencaBLL presencaBLL;

    public PresencaController(PresencaBLL presencaBLL) {
        this.presencaBLL = presencaBLL;
    }

    public RegistoAula marcarAulaDocente(String siglaDocente, String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String horaInicio) {
        return presencaBLL.marcarAulaDocente(siglaDocente, nomeUC, nomeCurso, anoLetivo, data, horaInicio);
    }

    public Presenca marcarPresencaEstudante(String numMecanografico, String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String horaInicio) {
        return presencaBLL.marcarPresencaEstudante(numMecanografico, nomeUC, nomeCurso, anoLetivo, data, horaInicio);
    }

    public List<RegistoAula> listarAulasDocente(String siglaDocente) {
        return presencaBLL.listarAulasDocente(siglaDocente);
    }

    public List<RegistoAula> listarAulasPorUC(String nomeUC, String nomeCurso, int anoLetivo) {
        return presencaBLL.listarAulasPorUC(nomeUC, nomeCurso, anoLetivo);
    }

    public List<Presenca> listarPresencasEstudante(String numMecanografico) {
        return presencaBLL.listarPresencasEstudante(numMecanografico);
    }

    public List<Presenca> listarPresencasPorUC(String nomeUC, String nomeCurso, int anoLetivo) {
        return presencaBLL.listarPresencasPorUC(nomeUC, nomeCurso, anoLetivo);
    }

    public List<RegistoAula> listarAulasSemPresencaEstudante(String numMecanografico, String nomeUC, String nomeCurso, int anoLetivo) {
        return presencaBLL.listarAulasSemPresencaEstudante(numMecanografico, nomeUC, nomeCurso, anoLetivo);
    }
}
