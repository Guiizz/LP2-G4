package DAL;

import Model.RegistoAula;
import Utils.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RegistoAulaDAL implements IRegistoAulaDAL {

    private static final String FICHEIRO_CSV = "csv/aulas_marcadas.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "siglaDocente;nomeUC;nomeCurso;anoLetivo;data;horaInicio;terminada";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private List<RegistoAula> registos;

    public RegistoAulaDAL() {
        this.registos = new ArrayList<>();
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);
        carregarDoCSV();
    }

    public void adicionar(RegistoAula registo) {
        registos.add(registo);
        guardarNoCSV();
    }

    public boolean existe(String nomeUC, String nomeCurso, int anoLetivo,
                          LocalDate data, String horaInicio) {
        return procurar(nomeUC, nomeCurso, anoLetivo, data, horaInicio) != null;
    }

    public RegistoAula procurar(String nomeUC, String nomeCurso, int anoLetivo,
                                LocalDate data, String horaInicio) {
        for (RegistoAula r : registos) {
            if (r.corresponde(nomeUC, nomeCurso, anoLetivo, data, horaInicio)) return r;
        }
        return null;
    }

    @Override
    public void guardar() {
        guardarNoCSV();
    }

    @Override
    public void guardarRegisto(RegistoAula registo) {
        guardarNoCSV();
    }

    public List<RegistoAula> listarPorDocente(String siglaDocente) {
        List<RegistoAula> resultado = new ArrayList<>();
        for (RegistoAula r : registos) {
            if (r.getSiglaDocente().equalsIgnoreCase(siglaDocente)) resultado.add(r);
        }
        return resultado;
    }

    public List<RegistoAula> listarPorUCeCurso(String nomeUC, String nomeCurso, int anoLetivo) {
        List<RegistoAula> resultado = new ArrayList<>();
        for (RegistoAula r : registos) {
            if (r.getNomeUC().equalsIgnoreCase(nomeUC)
                    && r.getNomeCurso().equalsIgnoreCase(nomeCurso)
                    && r.getAnoLetivo() == anoLetivo) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    public List<RegistoAula> listarTodas() {
        return new ArrayList<>(registos);
    }

    private void carregarDoCSV() {
        registos.clear();
        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR)) {
            if (campos.length < 6) continue;
            try {
                boolean terminada = campos.length >= 7 && Boolean.parseBoolean(campos[6]);
                registos.add(new RegistoAula(
                        campos[0],
                        campos[1],
                        campos[2],
                        Integer.parseInt(campos[3]),
                        LocalDate.parse(campos[4], FMT),
                        campos[5],
                        terminada
                ));
            } catch (Exception e) {
                System.err.println("Erro ao carregar registos de aulas do CSV: " + e.getMessage());
            }
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO)) {
            for (RegistoAula r : registos) {
                pw.println(r.getSiglaDocente() + SEPARADOR
                        + r.getNomeUC() + SEPARADOR
                        + r.getNomeCurso() + SEPARADOR
                        + r.getAnoLetivo() + SEPARADOR
                        + r.getData().format(FMT) + SEPARADOR
                        + r.getHoraInicio() + SEPARADOR
                        + r.isTerminada());
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar registos de aulas no CSV: " + e.getMessage());
        }
    }
}
