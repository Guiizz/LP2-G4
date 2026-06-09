package BLL;

import DAL.HorarioDAL;
import DAL.UnidadeCurricularDAL;
import Model.BlocoHorario;
import Model.Horario;
import Model.UnidadeCurricular;

import java.util.List;

public class HorarioBLL {

    public static final String[] DIAS_SEMANA = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"};

    private static final int HORA_INICIO_MIN = 18 * 60;      // 18:00
    private static final int HORA_FIM_MAX = 23 * 60 + 30; // 23:30
    private static final int PAUSA_INICIO = 20 * 60;      // 20:00
    private static final int PAUSA_FIM = 20 * 60 + 30; // 20:30
    private static final int MAX_MIN_DIA = 5 * 60;       // 300 min = 5h
    private static final int MAX_MIN_UC = 6 * 60;       // 360 min = 6h

    private final HorarioDAL horarioDAL;
    private final UnidadeCurricularDAL ucDAL;

    public HorarioBLL(HorarioDAL horarioDAL, UnidadeCurricularDAL ucDAL) {
        this.horarioDAL = horarioDAL;
        this.ucDAL = ucDAL;
    }

    public BlocoHorario adicionarBloco(String nomeCurso, int anoCurricular, int anoLetivo,
                                       String diaSemana, String horaInicio, int duracao, String nomeUC) {
        validarDiaSemana(diaSemana);
        validarDuracao(duracao);
        int inicioMin = parseMinutos(horaInicio);
        int fimMin = inicioMin + duracao;
        validarJanelaHoraria(inicioMin, fimMin);
        validarPausaJantar(inicioMin, fimMin);

        Horario horario = horarioDAL.procurarOuCriar(nomeCurso, anoCurricular, anoLetivo);

        // Limite diário para este ano curricular
        if (horario.getMinutosTotaisParaDia(diaSemana) + duracao > MAX_MIN_DIA) {
            throw new IllegalArgumentException(
                    "Máximo de 5h diárias atingido para " + diaSemana + " neste ano curricular.");
        }

        // Limite total da UC
        if (horario.getMinutosTotaisParaUC(nomeUC) + duracao > MAX_MIN_UC) {
            throw new IllegalArgumentException(
                    "A UC '" + nomeUC + "' atingiu o máximo de 6h semanais.");
        }

        // Sobreposição no próprio horário (mesmo dia e hora)
        for (BlocoHorario b : horario.getBlocosParaDia(diaSemana)) {
            int bInicio = parseMinutos(b.getHoraInicio());
            int bFim = bInicio + b.getDuracao();
            if (inicioMin < bFim && fimMin > bInicio) {
                throw new IllegalArgumentException(
                        "Já existe um bloco no período " + horaInicio + " de " + diaSemana + ".");
            }
        }

        // Sobreposição do docente noutros cursos
        String siglaDocente = obterSiglaDocente(nomeUC);
        if (siglaDocente != null) {
            verificarSemSobreposicaoDocente(siglaDocente, diaSemana, inicioMin, fimMin,
                    nomeCurso, anoCurricular, anoLetivo);
        }

        BlocoHorario bloco = new BlocoHorario(diaSemana, horaInicio, duracao, nomeUC);
        horario.adicionarBloco(bloco);
        horarioDAL.guardar(horario);
        return bloco;
    }

    public void removerBloco(String nomeCurso, int anoCurricular, int anoLetivo, int indice) {
        Horario horario = horarioDAL.procurarOuCriar(nomeCurso, anoCurricular, anoLetivo);
        if (indice < 0 || indice >= horario.getBlocos().size()) {
            throw new IllegalArgumentException("Índice de bloco inválido.");
        }
        horario.removerBloco(indice);
        horarioDAL.guardar(horario);
    }

    public Horario obterHorario(String nomeCurso, int anoCurricular, int anoLetivo) {
        return horarioDAL.procurarOuCriar(nomeCurso, anoCurricular, anoLetivo);
    }

    public List<Horario> listarTodos() {
        return horarioDAL.listarTodos();
    }

    // ── Horas válidas por duração ─────────────────────────────────────────────

    public static String[] getHorasValidasParaDuracao(int duracao) {
        if (duracao == 60) {
            return new String[]{"18:00", "19:00", "20:30", "21:30", "22:30"};
        } else {
            return new String[]{"18:00", "20:30", "21:30"};
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    public static int parseMinutos(String hora) {
        if (hora == null || !hora.matches("\\d{1,2}:\\d{2}")){
            throw new IllegalArgumentException("Hora inválida: '" + hora + "'. Use um formato HH:mm.");
        }
        String[] p = hora.split(":");
        return Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1]);
    }

    private void validarDiaSemana(String dia) {
        for (String d : DIAS_SEMANA) {
            if (d.equalsIgnoreCase(dia)) return;
        }
        throw new IllegalArgumentException("Dia da semana inválido: " + dia);
    }

    private void validarDuracao(int duracao) {
        if (duracao != 60 && duracao != 120) {
            throw new IllegalArgumentException("A duração deve ser 1h (60 min) ou 2h (120 min).");
        }
    }

    private void validarJanelaHoraria(int inicioMin, int fimMin) {
        if (inicioMin < HORA_INICIO_MIN) {
            throw new IllegalArgumentException("O horário não pode começar antes das 18:00.");
        }
        if (fimMin > HORA_FIM_MAX) {
            throw new IllegalArgumentException("O bloco não pode terminar depois das 23:30.");
        }
    }

    private void validarPausaJantar(int inicioMin, int fimMin) {
        if (inicioMin < PAUSA_FIM && fimMin > PAUSA_INICIO) {
            throw new IllegalArgumentException(
                    "O bloco não pode sobrepor-se à pausa de jantar (20:00–20:30).");
        }
    }

    private void verificarSemSobreposicaoDocente(String siglaDocente, String diaSemana,
                                                 int inicioMin, int fimMin,
                                                 String nomeCursoAtual, int anoCurricularAtual,
                                                 int anoLetivo) {
        for (Horario h : horarioDAL.listarTodos()) {
            if (h.getAnoLetivo() != anoLetivo) continue;
            if (h.getNomeCurso().equalsIgnoreCase(nomeCursoAtual)
                    && h.getAnoCurricular() == anoCurricularAtual) continue;
            for (BlocoHorario b : h.getBlocosParaDia(diaSemana)) {
                String siglaB = obterSiglaDocente(b.getNomeUC());
                if (siglaDocente.equalsIgnoreCase(siglaB)) {
                    int bInicio = parseMinutos(b.getHoraInicio());
                    int bFim = bInicio + b.getDuracao();
                    if (inicioMin < bFim && fimMin > bInicio) {
                        throw new IllegalArgumentException(
                                "O docente já tem uma aula neste horário noutro curso/ano.");
                    }
                }
            }
        }
    }

    private String obterSiglaDocente(String nomeUC) {
        UnidadeCurricular uc = ucDAL.procurarPorNome(nomeUC);
        return (uc != null) ? uc.getDocenteResponsavel() : null;
    }
}
