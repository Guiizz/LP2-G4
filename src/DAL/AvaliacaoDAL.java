package DAL;

import Model.Avaliacao;
import Model.Estudante;
import Model.Inscricao;
import Model.UnidadeCurricular;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Camada DAL para a entidade Avaliacao.
 * Responsável por armazenar e recuperar avaliações com persistência em ficheiro CSV.
 */
public class AvaliacaoDAL {

    private static final String FICHEIRO_CSV = "csv/avaliacoes.csv";
    private static final String SEPARADOR = ";";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    private ArrayList<Avaliacao> avaliacoes;
    private UnidadeCurricularDAL unidadeCurricularDAL;

    public AvaliacaoDAL() {
        this(new UnidadeCurricularDAL());
    }

    public AvaliacaoDAL(UnidadeCurricularDAL unidadeCurricularDAL) {
        this.unidadeCurricularDAL = unidadeCurricularDAL;
        this.avaliacoes = new ArrayList<>();
        SDF.setLenient(false);
        criarFicheiroCsvSeNaoExistir();
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
            Avaliacao atual = avaliacoes.get(i);
            if (mesmaAvaliacao(atual, avaliacaoAntiga)) {
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

        if (data == null) {
            return resultado;
        }

        String dataAlvo = SDF.format(data);

        for (Avaliacao avaliacao : avaliacoes) {
            if (avaliacao.getData() != null && SDF.format(avaliacao.getData()).equals(dataAlvo)) {
                resultado.add(avaliacao);
            }
        }

        return resultado;
    }

    private void criarFicheiroCsvSeNaoExistir() {
        File ficheiro = new File(FICHEIRO_CSV);

        if (ficheiro.getParentFile() != null) {
            ficheiro.getParentFile().mkdirs();
        }

        if (!ficheiro.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ficheiro))) {
                pw.println("uc_nomes;peso;data;nota;aprovado");
            } catch (IOException e) {
                System.err.println("Erro ao criar ficheiro CSV de avaliações: " + e.getMessage());
            }
        }
    }

    private void carregarDoCSV() {
        avaliacoes.clear();
        File ficheiro = new File(FICHEIRO_CSV);
        if (!ficheiro.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(ficheiro))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                if (linha.trim().isEmpty()) continue;

                String[] campos = linha.split(SEPARADOR, -1);
                if (campos.length < 5) continue;

                List<UnidadeCurricular> ucs = new ArrayList<>();
                String nomesUC = campos[0];

                if (!nomesUC.isBlank()) {
                    String[] nomes = nomesUC.split(",");
                    for (String nomeUC : nomes) {
                        UnidadeCurricular uc = unidadeCurricularDAL.procurarPorNome(nomeUC.trim());
                        if (uc != null) {
                            ucs.add(uc);
                        }
                    }
                }

                double peso = Double.parseDouble(campos[1]);
                Date data = SDF.parse(campos[2]);
                double nota = Double.parseDouble(campos[3]);
                boolean aprovado = Boolean.parseBoolean(campos[4]);

                Avaliacao avaliacao = new Avaliacao(ucs, peso, data, nota, aprovado);
                avaliacoes.add(avaliacao);

                for (UnidadeCurricular uc : ucs) {
                    if (uc.getAvaliacoes() != null && !uc.getAvaliacoes().contains(avaliacao)) {
                        uc.getAvaliacoes().add(avaliacao);
                    }
                }
            }

        } catch (IOException | ParseException | NumberFormatException e) {
            System.err.println("Erro ao carregar avaliações do CSV: " + e.getMessage());
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FICHEIRO_CSV))) {
            pw.println("uc_nomes;peso;data;nota;aprovado");

            for (Avaliacao avaliacao : avaliacoes) {
                List<UnidadeCurricular> ucs = avaliacao.getUc();
                StringBuilder nomesUC = new StringBuilder();

                if (ucs != null) {
                    for (int i = 0; i < ucs.size(); i++) {
                        nomesUC.append(ucs.get(i).getNome());
                        if (i < ucs.size() - 1) {
                            nomesUC.append(",");
                        }
                    }
                }

                boolean aprovado = avaliacao.getNota() >= 10.0;

                pw.println(
                        nomesUC + SEPARADOR +
                                avaliacao.getPeso() + SEPARADOR +
                                SDF.format(avaliacao.getData()) + SEPARADOR +
                                avaliacao.getNota() + SEPARADOR +
                                aprovado
                );
            }

        } catch (IOException e) {
            System.err.println("Erro ao guardar avaliações no CSV: " + e.getMessage());
        }
    }

    private boolean mesmaAvaliacao(Avaliacao a1, Avaliacao a2) {
        if (a1 == null || a2 == null) {
            return false;
        }

        if (Double.compare(a1.getPeso(), a2.getPeso()) != 0) {
            return false;
        }

        if (Double.compare(a1.getNota(), a2.getNota()) != 0) {
            return false;
        }

        if (a1.getData() == null && a2.getData() != null) {
            return false;
        }

        if (a1.getData() != null && a2.getData() == null) {
            return false;
        }

        if (a1.getData() != null && a2.getData() != null) {
            if (!SDF.format(a1.getData()).equals(SDF.format(a2.getData()))) {
                return false;
            }
        }

        List<UnidadeCurricular> ucs1 = a1.getUc();
        List<UnidadeCurricular> ucs2 = a2.getUc();

        if (ucs1 == null && ucs2 == null) {
            return true;
        }

        if (ucs1 == null || ucs2 == null) {
            return false;
        }

        if (ucs1.size() != ucs2.size()) {
            return false;
        }

        for (int i = 0; i < ucs1.size(); i++) {
            UnidadeCurricular uc1 = ucs1.get(i);
            UnidadeCurricular uc2 = ucs2.get(i);

            if (uc1 == null && uc2 == null) {
                continue;
            }

            if (uc1 == null || uc2 == null) {
                return false;
            }

            if (!uc1.getNome().equalsIgnoreCase(uc2.getNome())) {
                return false;
            }
        }

        return true;
    }
    public void associarAvaliacaoAInscricao(Estudante estudante, UnidadeCurricular uc, Avaliacao av) {
        for (Inscricao inscricao : estudante.getInscricoes()) {
            if (inscricao.getCurso().getUnidades().contains(uc)) {
                inscricao.adicionarAvaliacao(av);
                return;
            }
        }
    }
}