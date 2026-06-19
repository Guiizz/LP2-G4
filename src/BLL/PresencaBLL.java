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
        RegistoAula aula = registoAulaDAL.procurar(nomeUC, nomeCurso, anoLetivo, data, horaInicio);
        if (aula == null) {
            throw new IllegalArgumentException(
                    "O docente ainda não marcou presença nesta aula.");
        }
        if (aula.isTerminada()) {
            throw new IllegalArgumentException(
                    "A aula já foi terminada pelo docente. Já não é possível marcar presença.");
        }
        if (presencaDAL.existe(numMecanografico, nomeUC, nomeCurso, anoLetivo, data, horaInicio)) {
            throw new IllegalArgumentException("Já marcou a sua presença nesta aula.");
        }
        Presenca presenca = new Presenca(numMecanografico, nomeUC, nomeCurso, anoLetivo, data, horaInicio, true);
        presencaDAL.adicionar(presenca);
        return presenca;
    }

    /**
     * Termina uma aula marcada pelo docente. Todos os estudantes inscritos
     * que ainda não marcaram presença ficam com falta (presença = false).
     * Após terminada, não é possível marcar presença na aula.
     *
     * @param numMecanograficosInscritos números mecanográficos dos alunos inscritos na UC/curso/ano
     * @return número de faltas registadas
     */
    public int terminarAula(String nomeUC, String nomeCurso, int anoLetivo,
                            LocalDate data, String horaInicio,
                            List<String> numMecanograficosInscritos) {
        RegistoAula aula = registoAulaDAL.procurar(nomeUC, nomeCurso, anoLetivo, data, horaInicio);
        if (aula == null) {
            throw new IllegalArgumentException("Aula não encontrada.");
        }
        if (aula.isTerminada()) {
            throw new IllegalArgumentException("Esta aula já foi terminada.");
        }

        int faltas = 0;
        for (String num : numMecanograficosInscritos) {
            if (!presencaDAL.existe(num, nomeUC, nomeCurso, anoLetivo, data, horaInicio)) {
                presencaDAL.adicionar(new Presenca(num, nomeUC, nomeCurso, anoLetivo, data, horaInicio, false));
                faltas++;
            }
        }

        aula.setTerminada(true);
        registoAulaDAL.guardar();
        return faltas;
    }

    /** Faltas (presença = false) de um estudante — base para o pedido de justificação. */
    public List<Presenca> listarFaltasEstudante(String numMecanografico) {
        List<Presenca> faltas = new ArrayList<>();
        for (Presenca p : presencaDAL.listarPorEstudante(numMecanografico)) {
            if (!p.isPresente()) faltas.add(p);
        }
        return faltas;
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
            if (!r.isTerminada()) continue; // só aulas terminadas têm faltas definitivas
            if (!presencaDAL.existe(numMecanografico, nomeUC, nomeCurso,
                    anoLetivo, r.getData(), r.getHoraInicio())) {
                semPresenca.add(r);
            }
        }
        return semPresenca;
    }
}
