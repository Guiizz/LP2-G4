package DAL;

import Model.UnidadeCurricular;

import java.io.*;
import java.util.ArrayList;

/**
 * Camada DAL para a entidade UnidadeCurricular.
 * Responsável por armazenar e recuperar UCs com persistência em ficheiro CSV.
 */
public class UnidadeCurricularDAL {

    private static final String FICHEIRO_CSV = "csv/unidades_curriculares.csv";
    private static final String SEPARADOR = ";";

    private ArrayList<UnidadeCurricular> unidades;

    public UnidadeCurricularDAL() {
        this.unidades = new ArrayList<>();
        criarFicheiroCsvSeNaoExistir();
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

    private void criarFicheiroCsvSeNaoExistir() {
        File ficheiro = new File(FICHEIRO_CSV);
        if (ficheiro.getParentFile() != null) {
            ficheiro.getParentFile().mkdirs();
        }

        if (!ficheiro.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ficheiro))) {
                pw.println("nome;anoCurricular;ects");
            } catch (IOException e) {
                System.err.println("Erro ao criar ficheiro CSV de unidades curriculares: " + e.getMessage());
            }
        }
    }

    private void carregarDoCSV() {
        unidades.clear();
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
                if (campos.length < 3) continue;

                String nome = campos[0];
                int anoCurricular = Integer.parseInt(campos[1]);
                int ects = Integer.parseInt(campos[2]);

                unidades.add(new UnidadeCurricular(nome, anoCurricular, ects, new ArrayList<>()));
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao carregar unidades curriculares do CSV: " + e.getMessage());
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FICHEIRO_CSV))) {
            pw.println("nome;anoCurricular;ects");

            for (UnidadeCurricular uc : unidades) {
                pw.println(
                        uc.getNome() + SEPARADOR +
                                uc.getAnoCurricular() + SEPARADOR +
                                uc.getEts() + SEPARADOR +
                                (uc.getDocenteResponsavel() != null ? uc.getDocenteResponsavel().getSigla() : "")
                );
            }

        } catch (IOException e) {
            System.err.println("Erro ao guardar unidades curriculares no CSV: " + e.getMessage());
        }
    }
}