package DAL;

import Model.Avaliacao;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AvaliacaoDAL implements IAvaliacaoDAL {

    private static final String FICHEIRO_CSV = "csv/avaliacoes.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "uc_nomes;peso;data;nota;aprovado;curso;lancada";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    private ArrayList<Avaliacao> avaliacoes;
    private IUnidadeCurricularDAL unidadeCurricularDAL;

    public AvaliacaoDAL() {
        this(new UnidadeCurricularDAL());
    }

    public AvaliacaoDAL(IUnidadeCurricularDAL unidadeCurricularDAL) {
        this.unidadeCurricularDAL = unidadeCurricularDAL;
        this.avaliacoes = new ArrayList<>();
        SDF.setLenient(false);
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);
        carregarDoCSV();
    }

    public void adicionarAvaliacao(Avaliacao avaliacao) {
        avaliacoes.add(avaliacao);
        guardarNoCSV();
    }

    public ArrayList<Avaliacao> listarAvaliacoes() {
        return new ArrayList<>(avaliacoes);
    }

    public boolean atualizarAvaliacao(Avaliacao avaliacaoAntiga, Avaliacao avaliacaoNova) {
        for (int i = 0; i < avaliacoes.size(); i++) {
            if (avaliacoes.get(i) == avaliacaoAntiga) {
                avaliacoes.set(i, avaliacaoNova);
                guardarNoCSV();
                return true;
            }
        }
        for (int i = 0; i < avaliacoes.size(); i++) {
            if (mesmaAvaliacao(avaliacoes.get(i), avaliacaoAntiga)) {
                avaliacoes.set(i, avaliacaoNova);
                guardarNoCSV();
                return true;
            }
        }
        return false;
    }

    public void removerAvaliacao(Avaliacao avaliacao) {
        avaliacoes.remove(avaliacao);
        guardarNoCSV();
    }

    public ArrayList<Avaliacao> procurarPorUC(UnidadeCurricular uc) {
        ArrayList<Avaliacao> resultado = new ArrayList<>();
        for (Avaliacao avaliacao : avaliacoes) {
            if (avaliacao.getUc() != null && avaliacao.getUc().contains(uc)) {
                resultado.add(avaliacao);
            }
        }
        return resultado;
    }

    public ArrayList<Avaliacao> procurarPorData(Date data) {
        ArrayList<Avaliacao> resultado = new ArrayList<>();
        if (data == null) return resultado;
        String dataAlvo = SDF.format(data);
        for (Avaliacao avaliacao : avaliacoes) {
            if (avaliacao.getData() != null && SDF.format(avaliacao.getData()).equals(dataAlvo)) {
                resultado.add(avaliacao);
            }
        }
        return resultado;
    }

    private void carregarDoCSV() {
        avaliacoes.clear();
        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR)) {
            if (campos.length < 5) continue;
            try {
                List<UnidadeCurricular> ucs = new ArrayList<>();
                if (!campos[0].isBlank()) {
                    for (String nomeUC : campos[0].split(",")) {
                        UnidadeCurricular uc = unidadeCurricularDAL.procurarPorNome(nomeUC.trim());
                        if (uc != null) ucs.add(uc);
                    }
                }
                double peso = Double.parseDouble(campos[1]);
                Date data = SDF.parse(campos[2]);
                double nota = Double.parseDouble(campos[3]);
                boolean aprovado = Boolean.parseBoolean(campos[4]);

                boolean lancada = campos.length < 7 || Boolean.parseBoolean(campos[6]);

                Avaliacao avaliacao = lancada
                        ? new Avaliacao(ucs, peso, data, nota, aprovado)
                        : new Avaliacao(ucs, peso, data);
                if (campos.length >= 6 && !campos[5].isBlank()) {
                    avaliacao.setNomeCurso(campos[5]);
                }
                avaliacoes.add(avaliacao);

                for (UnidadeCurricular uc : ucs) {
                    if (uc.getAvaliacoes() != null && !uc.getAvaliacoes().contains(avaliacao)) {
                        uc.getAvaliacoes().add(avaliacao);
                    }
                }
            } catch (ParseException | NumberFormatException e) {
                System.err.println("Erro ao carregar avaliações do CSV: " + e.getMessage());
            }
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO)) {
            for (Avaliacao avaliacao : avaliacoes) {
                List<UnidadeCurricular> ucs = avaliacao.getUc();
                StringBuilder nomesUC = new StringBuilder();
                if (ucs != null) {
                    for (int i = 0; i < ucs.size(); i++) {
                        nomesUC.append(ucs.get(i).getNome());
                        if (i < ucs.size() - 1) nomesUC.append(",");
                    }
                }
                pw.println(
                        nomesUC + SEPARADOR +
                                avaliacao.getPeso() + SEPARADOR +
                                SDF.format(avaliacao.getData()) + SEPARADOR +
                                avaliacao.getNota() + SEPARADOR +
                                (avaliacao.getNota() >= 10.0) + SEPARADOR +
                                (avaliacao.getNomeCurso() != null ? avaliacao.getNomeCurso() : "") + SEPARADOR +
                                avaliacao.isLancada()
                );
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar avaliações no CSV: " + e.getMessage());
        }
    }

    private boolean mesmaAvaliacao(Avaliacao a1, Avaliacao a2) {
        if (a1 == null || a2 == null) return false;
        if (Double.compare(a1.getPeso(), a2.getPeso()) != 0) return false;
        if (Double.compare(a1.getNota(), a2.getNota()) != 0) return false;
        if (a1.getData() == null && a2.getData() != null) return false;
        if (a1.getData() != null && a2.getData() == null) return false;
        if (a1.getData() != null && !SDF.format(a1.getData()).equals(SDF.format(a2.getData()))) return false;

        List<UnidadeCurricular> ucs1 = a1.getUc();
        List<UnidadeCurricular> ucs2 = a2.getUc();
        if (ucs1 == null && ucs2 == null) return true;
        if (ucs1 == null || ucs2 == null) return false;
        if (ucs1.size() != ucs2.size()) return false;
        for (int i = 0; i < ucs1.size(); i++) {
            UnidadeCurricular uc1 = ucs1.get(i);
            UnidadeCurricular uc2 = ucs2.get(i);
            if (uc1 == null && uc2 == null) continue;
            if (uc1 == null || uc2 == null) return false;
            if (!uc1.getNome().equalsIgnoreCase(uc2.getNome())) return false;
        }
        return true;
    }
}