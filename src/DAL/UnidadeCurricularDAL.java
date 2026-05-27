package DAL;

import Model.UnidadeCurricular;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import Utils.Utils;

/**
 * Camada DAL para a entidade UnidadeCurricular.
 * Responsável por armazenar e recuperar UCs com persistência em ficheiro CSV.
 */
public class UnidadeCurricularDAL {

    private static final String FICHEIRO_CSV = "csv/unidades_curriculares.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "nome;anoCurricular;ects;docenteResponsavel;ativa;momentos";

    private ArrayList<UnidadeCurricular> unidades;

    public UnidadeCurricularDAL() {
        this.unidades = new ArrayList<>();
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);
        carregarDoCSV();
    }

    public void adicionarUnidade(UnidadeCurricular unidade) {
        unidades.add(unidade);
        guardarNoCSV();
    }

    public boolean atualizarUnidade(UnidadeCurricular unidadeAtualizada) {
        for (int i = 0; i < unidades.size(); i++) {
            UnidadeCurricular atual = unidades.get(i);
            if (atual.getNome().equalsIgnoreCase(unidadeAtualizada.getNome())
                    && atual.getAnoCurricular() == unidadeAtualizada.getAnoCurricular()) {
                unidades.set(i, unidadeAtualizada);
                guardarNoCSV();
                return true;
            }
        }
        return false;
    }

    public ArrayList<UnidadeCurricular> listarUnidades() {
        return new ArrayList<>(unidades);
    }

    public void removerUnidade(UnidadeCurricular unidade) {
        unidades.remove(unidade);
        guardarNoCSV();
    }

    public UnidadeCurricular procurarPorNome(String nome) {
        for (UnidadeCurricular uc : unidades) {
            if (uc.getNome().equalsIgnoreCase(nome)) {
                return uc;
            }
        }
        return null;
    }

    public boolean atribuirDocenteResponsavel(String nomeUC, String siglaDocente) {
        for (UnidadeCurricular uc : unidades) {
            if (uc.getNome().equalsIgnoreCase(nomeUC)) {
                uc.setDocenteResponsavel(siglaDocente);
                guardarNoCSV();
                return true;
            }
        }
        return false;
    }

    private void carregarDoCSV() {
        unidades.clear();
        List<String[]> linhas = Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR);

        for (String[] campos : linhas) {
            if (campos.length < 3) continue;

            String nome         = campos[0];
            int anoCurricular   = Integer.parseInt(campos[1]);
            int ects            = Integer.parseInt(campos[2]);
            String siglaDocente = campos.length >= 4 ? campos[3] : "";
            boolean ativa       = campos.length >= 5 && Boolean.parseBoolean(campos[4]);

            UnidadeCurricular uc = new UnidadeCurricular(nome, anoCurricular, ects, new ArrayList<>(), siglaDocente);
            if (!siglaDocente.isEmpty()) uc.setDocenteResponsavel(siglaDocente);
            uc.setAtiva(ativa);

            if (campos.length >= 6 && !campos[5].isBlank()) {
                for (String parte : campos[5].split("\\|")) {
                    String[] mv = parte.split(":");
                    if (mv.length == 2) {
                        try {
                            uc.adicionarMomento(new Model.MomentoAvaliacao(mv[0].trim(), Double.parseDouble(mv[1].trim())));
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }

            unidades.add(uc);
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO)) {
            for (UnidadeCurricular uc : unidades) {
                StringBuilder momentosSB = new StringBuilder();
                List<Model.MomentoAvaliacao> momentos = uc.getMomentosAvaliacao();
                for (int i = 0; i < momentos.size(); i++) {
                    Model.MomentoAvaliacao m = momentos.get(i);
                    momentosSB.append(m.getNome()).append(":").append(m.getPeso());
                    if (i < momentos.size() - 1) momentosSB.append("|");
                }
                pw.println(
                        uc.getNome()          + SEPARADOR +
                                uc.getAnoCurricular() + SEPARADOR +
                                uc.getEts()           + SEPARADOR +
                                (uc.getDocenteResponsavel() != null ? uc.getDocenteResponsavel() : "") + SEPARADOR +
                                uc.isAtiva()          + SEPARADOR +
                                momentosSB
                );
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar unidades curriculares no CSV: " + e.getMessage());
        }
    }
}