package DAL;

import Model.AnoLetivo;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class AnoLetivoDAL {
    private static final String FICHEIRO_CSV = "csv/anos_letivos.csv";
    private static final String SEPARADOR = ";";

    private final ArrayList<AnoLetivo> anosLetivos;

    public AnoLetivoDAL() {
        this.anosLetivos = new ArrayList<>();
        criarFicheiroCsvSeNaoExistir();
        carregarDoCSV();
    }

    public void adicionarAnoLetivo(AnoLetivo anoLetivo) {
        anosLetivos.add(anoLetivo);
        guardarNoCSV();
    }

    public boolean atualizarAnoLetivo(AnoLetivo anoLetivoAtualizado) {
        for (int i = 0; i < anosLetivos.size(); i++) {
            if (anosLetivos.get(i).getAno() == anoLetivoAtualizado.getAno()) {
                anosLetivos.set(i, anoLetivoAtualizado);
                guardarNoCSV();
                return true;
            }
        }
        return false;
    }

    public ArrayList<AnoLetivo> listarAnosLetivos() {
        return new ArrayList<>(anosLetivos);
    }

    public AnoLetivo procurarPorAno(int ano) {
        for (AnoLetivo a : anosLetivos) {
            if (a.getAno() == ano) return a;
        }
        return null;
    }

    public AnoLetivo procurarAnoAberto() {
        for (AnoLetivo a : anosLetivos) {
            if (a.isAberto()) return a;
        }
        return null;
    }

    public AnoLetivo procurarMaisRecente() {
        AnoLetivo maisRecente = null;
        for (AnoLetivo a : anosLetivos) {
            if (maisRecente == null || a.getAno() > maisRecente.getAno()) {
                maisRecente = a;
            }
        }
        return maisRecente;
    }

    private void criarFicheiroCsvSeNaoExistir() {
        File ficheiro = new File(FICHEIRO_CSV);
        if (ficheiro.getParentFile() != null) {
            ficheiro.getParentFile().mkdirs();
        }

        if (!ficheiro.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ficheiro))) {
                pw.println("ano;estado;dataAbertura;dataFecho");
            } catch (IOException e) {
                System.err.println("Erro ao criar ficheiro CSV de anos letivos: " + e.getMessage());
            }
        }
    }

    private void carregarDoCSV() {
        anosLetivos.clear();
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

                int ano = Integer.parseInt(campos[0]);
                String estado = campos[1];
                LocalDate dataAbertura = LocalDate.parse(campos[2]);
                LocalDate dataFecho = campos.length >= 4 && !campos[3].isBlank()
                        ? LocalDate.parse(campos[3])
                        : null;

                anosLetivos.add(new AnoLetivo(ano, estado, dataAbertura, dataFecho));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao carregar anos letivos do CSV: " + e.getMessage());
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FICHEIRO_CSV))) {
            pw.println("ano;estado;dataAbertura;dataFecho");

            for (AnoLetivo a : anosLetivos) {
                pw.println(
                        a.getAno() + SEPARADOR +
                                a.getEstado() + SEPARADOR +
                                a.getDataAbertura() + SEPARADOR +
                                (a.getDataFecho() == null ? "" : a.getDataFecho())
                );
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar anos letivos no CSV: " + e.getMessage());
        }
    }
}
