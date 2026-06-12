package DAL;

import Model.MomentoAvaliacao;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UnidadeCurricularDAL implements IUnidadeCurricularDAL {

    private static final String FICHEIRO_CSV = "csv/unidades_curriculares.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "nome;anoCurricular;ects;docenteResponsavel;momentos;ativa";

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
            if (atual.getNome().equalsIgnoreCase(unidadeAtualizada.getNome()) && atual.getAnoCurricular() == unidadeAtualizada.getAnoCurricular()) {
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
            if (uc.getNome().equalsIgnoreCase(nome)) return uc;
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
        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR)) {
            if (campos.length < 3) continue;

            String nome = campos[0];
            int anoCurricular = Integer.parseInt(campos[1]);
            int ects = Integer.parseInt(campos[2]);
            String sigla = campos.length >= 4 ? campos[3] : "";

            UnidadeCurricular uc = new UnidadeCurricular(nome, anoCurricular, ects, new ArrayList<>(), sigla);
            if (!sigla.isEmpty()) uc.setDocenteResponsavel(sigla);

            String momentosStr = campos.length >= 5 ? campos[4] : "";
            if (!momentosStr.isBlank()) {
                for (String parte : momentosStr.split("\\|")) {
                    String[] mv = parte.split(":");
                    try {
                        if (mv.length == 3) {
                            // novo formato: nome:peso:anoLetivo
                            uc.adicionarMomento(new MomentoAvaliacao(
                                    mv[0], Double.parseDouble(mv[1]), Integer.parseInt(mv[2])));
                        } else if (mv.length == 2) {
                            // formato legado: nome:peso  →  anoLetivo = 0
                            uc.adicionarMomento(new MomentoAvaliacao(
                                    mv[0], Double.parseDouble(mv[1]), 0));
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            boolean ativa = campos.length >= 6 && Boolean.parseBoolean(campos[5]);
            uc.setAtiva(ativa);

            unidades.add(uc);
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO)) {
            for (UnidadeCurricular uc : unidades) {

                StringBuilder momentosSB = new StringBuilder();
                if (uc.getMomentosAvaliacao() != null) {
                    List<MomentoAvaliacao> momentos = uc.getMomentosAvaliacao();
                    for (int i = 0; i < momentos.size(); i++) {
                        MomentoAvaliacao m = momentos.get(i);
                        momentosSB.append(m.getNome().replace("|", "-").replace(":", "-"))
                              .append(":").append(m.getPeso())
                              .append(":").append(m.getAnoLetivo());
                        if (i < momentos.size() - 1) momentosSB.append("|");
                    }
                }

                pw.println(
                        uc.getNome() + SEPARADOR +
                                uc.getAnoCurricular() + SEPARADOR +
                                uc.getEts() + SEPARADOR +
                                (uc.getDocenteResponsavel() != null ? uc.getDocenteResponsavel() : "") + SEPARADOR +
                                momentosSB + SEPARADOR +
                                uc.isAtiva()
                );
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar unidades curriculares no CSV: " + e.getMessage());
        }
    }
}