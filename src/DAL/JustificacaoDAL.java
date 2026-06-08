package DAL;

import Model.JustificacaoFalta;
import Utils.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JustificacaoDAL {

    private static final String FICHEIRO_CSV = "csv/justificacoes.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO =
            "numMecanografico;nomeUC;nomeCurso;anoLetivo;dataAula;horaInicio;nomeTipo;estado;dataPedido";

    private List<JustificacaoFalta> justificacoes;

    public JustificacaoDAL() {
        this.justificacoes = new ArrayList<>();
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);
        carregarDoCSV();
    }

    public void adicionar(JustificacaoFalta justificacao) {
        justificacoes.add(justificacao);
        guardarNoCSV();
    }

    public void atualizar(JustificacaoFalta justificacao) {
        for (int i = 0; i < justificacoes.size(); i++) {
            JustificacaoFalta j = justificacoes.get(i);
            if (j.getNumMecanografico().equals(justificacao.getNumMecanografico())
                    && j.getNomeUC().equalsIgnoreCase(justificacao.getNomeUC())
                    && j.getNomeCurso().equalsIgnoreCase(justificacao.getNomeCurso())
                    && j.getAnoLetivo() == justificacao.getAnoLetivo()
                    && j.getDataAula().equals(justificacao.getDataAula())
                    && j.getHoraInicio().equals(justificacao.getHoraInicio())) {
                justificacoes.set(i, justificacao);
                guardarNoCSV();
                return;
            }
        }
    }

    public List<JustificacaoFalta> listarPorEstudante(String numMecanografico) {
        List<JustificacaoFalta> resultado = new ArrayList<>();
        for (JustificacaoFalta j : justificacoes) {
            if (j.getNumMecanografico().equals(numMecanografico)) resultado.add(j);
        }
        return resultado;
    }

    public List<JustificacaoFalta> listarPendentes() {
        List<JustificacaoFalta> resultado = new ArrayList<>();
        for (JustificacaoFalta j : justificacoes) {
            if (JustificacaoFalta.PENDENTE.equals(j.getEstado())) resultado.add(j);
        }
        return resultado;
    }

    public List<JustificacaoFalta> listarTodas() {
        return new ArrayList<>(justificacoes);
    }

    private void carregarDoCSV() {
        justificacoes.clear();
        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR)) {
            if (campos.length < 9) continue;
            try {
                justificacoes.add(new JustificacaoFalta(
                        campos[0],
                        campos[1],
                        campos[2],
                        Integer.parseInt(campos[3]),
                        LocalDate.parse(campos[4]),
                        campos[5],
                        campos[6],
                        campos[7],
                        LocalDate.parse(campos[8])
                ));
            } catch (Exception e) {
                System.err.println("Erro ao carregar justificações do CSV: " + e.getMessage());
            }
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO)) {
            for (JustificacaoFalta j : justificacoes) {
                pw.println(j.getNumMecanografico() + SEPARADOR
                        + j.getNomeUC() + SEPARADOR
                        + j.getNomeCurso() + SEPARADOR
                        + j.getAnoLetivo() + SEPARADOR
                        + j.getDataAula() + SEPARADOR
                        + j.getHoraInicio() + SEPARADOR
                        + j.getNomeTipoJustificacao() + SEPARADOR
                        + j.getEstado() + SEPARADOR
                        + j.getDataPedido());
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar justificações no CSV: " + e.getMessage());
        }
    }
}
