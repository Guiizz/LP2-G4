package DAL;

import Model.Presenca;
import Utils.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PresencaDAL implements IPresencaDAL {

    private static final String FICHEIRO_CSV = "csv/presencas.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "numMecanografico;nomeUC;nomeCurso;anoLetivo;data;horaInicio;presente";

    private List<Presenca> presencas;

    public PresencaDAL() {
        this.presencas = new ArrayList<>();
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);
        carregarDoCSV();
    }

    public void adicionar(Presenca presenca) {
        presencas.add(presenca);
        guardarNoCSV();
    }

    public boolean existe(String numMecanografico, String nomeUC, String nomeCurso,
                          int anoLetivo, LocalDate data, String horaInicio) {
        for (Presenca p : presencas) {
            if (p.corresponde(numMecanografico, nomeUC, nomeCurso, anoLetivo, data, horaInicio)) return true;
        }
        return false;
    }

    public List<Presenca> listarPorEstudante(String numMecanografico) {
        List<Presenca> resultado = new ArrayList<>();
        for (Presenca p : presencas) {
            if (p.getNumMecanografico().equals(numMecanografico)) resultado.add(p);
        }
        return resultado;
    }

    public List<Presenca> listarPorUCeCurso(String nomeUC, String nomeCurso, int anoLetivo) {
        List<Presenca> resultado = new ArrayList<>();
        for (Presenca p : presencas) {
            if (p.getNomeUC().equalsIgnoreCase(nomeUC)
                    && p.getNomeCurso().equalsIgnoreCase(nomeCurso)
                    && p.getAnoLetivo() == anoLetivo) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Presenca> listarTodas() {
        return new ArrayList<>(presencas);
    }

    private void carregarDoCSV() {
        presencas.clear();
        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR)) {
            if (campos.length < 7) continue;
            try {
                presencas.add(new Presenca(
                        campos[0],
                        campos[1],
                        campos[2],
                        Integer.parseInt(campos[3]),
                        LocalDate.parse(campos[4]),
                        campos[5],
                        Boolean.parseBoolean(campos[6])
                ));
            } catch (Exception e) {
                System.err.println("Erro ao carregar presenças do CSV: " + e.getMessage());
            }
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO)) {
            for (Presenca p : presencas) {
                pw.println(p.getNumMecanografico() + SEPARADOR
                        + p.getNomeUC() + SEPARADOR
                        + p.getNomeCurso() + SEPARADOR
                        + p.getAnoLetivo() + SEPARADOR
                        + p.getData() + SEPARADOR
                        + p.getHoraInicio() + SEPARADOR
                        + p.isPresente());
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar presenças no CSV: " + e.getMessage());
        }
    }
}
