package BLL;

import DAL.RegistoAulaDAL;
import DAL.PresencaDAL;
import Model.RegistoAula;
import Model.Presenca;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PresencaBLL {

    private final RegistoAulaDAL registoAulaDAL;
    private final PresencaDAL presencaDAL;

    public PresencaBLL(RegistoAulaDAL registoAulaDAL, PresencaDAL presencaDAL) {
        this.registoAulaDAL = registoAulaDAL;
        this.presencaDAL = presencaDAL;
    }

    public RegistoAula marcarAulaDocente(String siglaDocente, String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String horaInicio) {
        if (siglaDocente == null || siglaDocente.isBlank()) {
            throw new IllegalArgumentException("A sigla do docente é obrigatória.");
        }
        if (registoAulaDAL.existe(nomeUC, nomeCurso, anoLetivo, data, horaInicio)) {
            throw new IllegalArgumentException("Esta aula já foi marcada.");
        }
        RegistoAula registo = new RegistoAula(siglaDocente, nomeUC, nomeCurso, anoLetivo, data, horaInicio);
        registoAulaDAL.adicionar(registo);
        return registo;
    }

    public Presenca marcarPresencaEstudante(String numMecanografico, String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String horaInicio) {
        if (!registoAulaDAL.existe(nomeUC, nomeCurso, anoLetivo, data, horaInicio)) {
            throw new IllegalArgumentException(
                    "O docente ainda não marcou presença nesta aula.");
        }
        if (presencaDAL.existe(numMecanografico, nomeUC, nomeCurso, anoLetivo, data, horaInicio)) {
            throw new IllegalArgumentException("Já marcou a sua presença nesta aula.");
        }
        Presenca presenca = new Presenca(numMecanografico, nomeUC, nomeCurso, anoLetivo, data, horaInicio, true);
        presencaDAL.adicionar(presenca);
        return presenca;
    }

    public List<RegistoAula> listarAulasDocente(String siglaDocente) {
        return registoAulaDAL.listarPorDocente(siglaDocente);
    }

    public List<RegistoAula> listarAulasPorUC(String nomeUC, String nomeCurso, int anoLetivo) {
        return registoAulaDAL.listarPorUCeCurso(nomeUC, nomeCurso, anoLetivo);
    }

    public List<Presenca> listarPresencasEstudante(String numMecanografico) {
        return presencaDAL.listarPorEstudante(numMecanografico);
    }

    public List<Presenca> listarPresencasPorUC(String nomeUC, String nomeCurso, int anoLetivo) {
        return presencaDAL.listarPorUCeCurso(nomeUC, nomeCurso, anoLetivo);
    }

    public List<RegistoAula> listarAulasSemPresencaEstudante(String numMecanografico, String nomeUC, String nomeCurso, int anoLetivo) {
        List<RegistoAula> aulas = registoAulaDAL.listarPorUCeCurso(nomeUC, nomeCurso, anoLetivo);
        List<RegistoAula> semPresenca = new ArrayList<>();
        for (RegistoAula r : aulas) {
            if (!presencaDAL.existe(numMecanografico, nomeUC, nomeCurso,
                    anoLetivo, r.getData(), r.getHoraInicio())) {
                semPresenca.add(r);
            }
        }
        return semPresenca;
    }
}
