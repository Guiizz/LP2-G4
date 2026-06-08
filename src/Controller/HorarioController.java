package Controller;

import BLL.HorarioBLL;
import Model.BlocoHorario;
import Model.Horario;

import java.util.List;

public class HorarioController {

    private final HorarioBLL horarioBLL;

    public HorarioController(HorarioBLL horarioBLL) {
        this.horarioBLL = horarioBLL;
    }

    public BlocoHorario adicionarBloco(String nomeCurso, int anoCurricular, int anoLetivo, String diaSemana, String horaInicio, int duracao, String nomeUC) {
        return horarioBLL.adicionarBloco(nomeCurso, anoCurricular, anoLetivo, diaSemana, horaInicio, duracao, nomeUC);
    }

    public void removerBloco(String nomeCurso, int anoCurricular, int anoLetivo, int indice) {
        horarioBLL.removerBloco(nomeCurso, anoCurricular, anoLetivo, indice);
    }

    public Horario obterHorario(String nomeCurso, int anoCurricular, int anoLetivo) {
        return horarioBLL.obterHorario(nomeCurso, anoCurricular, anoLetivo);
    }

    public List<Horario> listarTodos() {
        return horarioBLL.listarTodos();
    }

    public String[] getDiasSemana() {
        return HorarioBLL.DIAS_SEMANA;
    }

    public String[] getHorasValidas(int duracao) {
        return HorarioBLL.getHorasValidasParaDuracao(duracao);
    }
}
