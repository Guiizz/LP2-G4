package DAL;

import Model.Departamento;

import java.io.*;
import java.util.ArrayList;

/**
 * Camada DAL para a entidade Departamento.
 * Responsável por armazenar e recuperar departamentos com persistência em ficheiro CSV.
 */
public class DepartamentoDAL {

    private static final String FICHEIRO_CSV = "csv/departamentos.csv";
    private static final String SEPARADOR = ";";

    private ArrayList<Departamento> listaDepartamentos;

    public DepartamentoDAL() {
        this.listaDepartamentos = new ArrayList<>();
        criarFicheiroCsvSeNaoExistir();
        carregarDoCSV();
    }

    public Departamento adicionarDepartamento(Departamento departamento) {
        listaDepartamentos.add(departamento);
        guardarNoCSV();
        return departamento;
    }

    public boolean atualizarDepartamento(Departamento departamentoAtualizado) {
        for (int i = 0; i < listaDepartamentos.size(); i++) {
            if (listaDepartamentos.get(i).getSigla().equalsIgnoreCase(departamentoAtualizado.getSigla())) {
                listaDepartamentos.set(i, departamentoAtualizado);
                guardarNoCSV();
                return true;
            }
        }
        return false;
    }

    public void removerDepartamento(Departamento departamento) {
        listaDepartamentos.remove(departamento);
        guardarNoCSV();
    }

    public Departamento procurarPorSigla(String sigla) {
        for (Departamento d : listaDepartamentos) {
            if (d.getSigla().equalsIgnoreCase(sigla)) {
                return d;
            }
        }
        return null;
    }

    public ArrayList<Departamento> listarDepartamentos() {
        return new ArrayList<>(listaDepartamentos);
    }

    private void criarFicheiroCsvSeNaoExistir() {
        File ficheiro = new File(FICHEIRO_CSV);
        if (ficheiro.getParentFile() != null) {
            ficheiro.getParentFile().mkdirs();
        }

        if (!ficheiro.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ficheiro))) {
                pw.println("nome;sigla");
            } catch (IOException e) {
                System.err.println("Erro ao criar ficheiro CSV de departamentos: " + e.getMessage());
            }
        }
    }

    private void carregarDoCSV() {
        listaDepartamentos.clear();
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
                if (campos.length < 2) continue;

                String nome = campos[0];
                String sigla = campos[1];

                listaDepartamentos.add(new Departamento(nome, sigla));
            }

        } catch (IOException e) {
            System.err.println("Erro ao carregar departamentos do CSV: " + e.getMessage());
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FICHEIRO_CSV))) {
            pw.println("nome;sigla");

            for (Departamento departamento : listaDepartamentos) {
                pw.println(
                        departamento.getNome() + SEPARADOR +
                                departamento.getSigla()
                );
            }

        } catch (IOException e) {
            System.err.println("Erro ao guardar departamentos no CSV: " + e.getMessage());
        }
    }
}