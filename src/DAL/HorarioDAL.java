package DAL;

import Model.BlocoHorario;
import Model.Horario;
import Utils.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class HorarioDAL implements IHorarioDAL {

    private static final String FICHEIRO_CSV = "csv/horarios.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "nomeCurso;anoCurricular;anoLetivo;diaSemana;horaInicio;duracao;nomeUC";

    private List<Horario> horarios;

    public HorarioDAL() {
        this.horarios = new ArrayList<>();
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);
        carregarDoCSV();
    }

    public Horario procurarOuCriar(String nomeCurso, int anoCurricular, int anoLetivo) {
        for (Horario h : horarios) {
            if (h.getNomeCurso().equalsIgnoreCase(nomeCurso)
                    && h.getAnoCurricular() == anoCurricular
                    && h.getAnoLetivo() == anoLetivo) {
                return h;
            }
        }
        Horario novo = new Horario(nomeCurso, anoCurricular, anoLetivo);
        horarios.add(novo);
        return novo;
    }

    public List<Horario> listarTodos() {
        return new ArrayList<>(horarios);
    }

    public List<Horario> listarPorCurso(String nomeCurso) {
        List<Horario> resultado = new ArrayList<>();
        for (Horario h : horarios) {
            if (h.getNomeCurso().equalsIgnoreCase(nomeCurso)) resultado.add(h);
        }
        return resultado;
    }

    public void guardar(Horario horario) {
        for (int i = 0; i < horarios.size(); i++) {
            Horario h = horarios.get(i);
            if (h.getNomeCurso().equalsIgnoreCase(horario.getNomeCurso())
                    && h.getAnoCurricular() == horario.getAnoCurricular()
                    && h.getAnoLetivo() == horario.getAnoLetivo()) {
                horarios.set(i, horario);
                guardarNoCSV();
                return;
            }
        }
        horarios.add(horario);
        guardarNoCSV();
    }

    private void carregarDoCSV() {
        horarios.clear();
        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR)) {
            if (campos.length < 7) continue;
            try {
                String nomeCurso = campos[0];
                int anoCurricular = Integer.parseInt(campos[1]);
                int anoLetivo = Integer.parseInt(campos[2]);
                String diaSemana = campos[3];
                String horaInicio = campos[4];
                int duracao = Integer.parseInt(campos[5]);
                String nomeUC = campos[6];

                Horario horario = procurarOuCriar(nomeCurso, anoCurricular, anoLetivo);
                horario.adicionarBloco(new BlocoHorario(diaSemana, horaInicio, duracao, nomeUC));
            } catch (Exception e) {
                System.err.println("Erro ao carregar horário do CSV: " + e.getMessage());
            }
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO)) {
            for (Horario h : horarios) {
                for (BlocoHorario b : h.getBlocos()) {
                    pw.println(h.getNomeCurso() + SEPARADOR
                            + h.getAnoCurricular() + SEPARADOR
                            + h.getAnoLetivo() + SEPARADOR
                            + b.getDiaSemana() + SEPARADOR
                            + b.getHoraInicio() + SEPARADOR
                            + b.getDuracao() + SEPARADOR
                            + b.getNomeUC());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar horários no CSV: " + e.getMessage());
        }
    }
}
