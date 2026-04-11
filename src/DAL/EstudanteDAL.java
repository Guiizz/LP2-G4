package DAL;

import Model.Estudante;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Camada DAL para a entidade Estudante.
 * Responsável por armazenar e recuperar estudantes com persistência em ficheiro CSV.
 */
public class EstudanteDAL {

    private static final String FICHEIRO_CSV = "data/estudantes.csv";
    private static final String SEPARADOR = ";";

    private ArrayList<Estudante> estudantes;

    public EstudanteDAL() {
        this.estudantes = new ArrayList<>();
        criarFicheiroCsvSeNaoExistir();
        carregarDoCSV();
    }

    public void adicionarEstudante(Estudante estudante) {
        estudantes.add(estudante);
        guardarNoCSV();
    }

    public ArrayList<Estudante> listarEstudantes() {
        return new ArrayList<>(estudantes);
    }

    public boolean atualizarEstudante(Estudante estudanteAtualizado) {
        for (int i = 0; i < estudantes.size(); i++) {
            if (estudantes.get(i).getNumMecanografico().equals(estudanteAtualizado.getNumMecanografico())) {
                estudantes.set(i, estudanteAtualizado);
                guardarNoCSV();
                return true;
            }
        }
        return false;
    }

    public void removerEstudante(String numMecanografico) {
        for (int i = 0; i < estudantes.size(); i++) {
            if (estudantes.get(i).getNumMecanografico().equals(numMecanografico)) {
                estudantes.remove(i);
                guardarNoCSV();
                return;
            }
        }
    }

    public Estudante procurarPorNumMecanografico(String numMecanografico) {
        for (Estudante e : estudantes) {
            if (e.getNumMecanografico().equals(numMecanografico)) {
                return e;
            }
        }
        return null;
    }

    public Estudante procurarPorNif(String nif) {
        for (Estudante e : estudantes) {
            if (e.getNif().equals(nif)) {
                return e;
            }
        }
        return null;
    }

    public Estudante procurarPorEmail(String email) {
        for (Estudante e : estudantes) {
            if (e.getEmail().equalsIgnoreCase(email)) {
                return e;
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
                pw.println("nome;dataNascimento;nif;morada;numMecanografico;anoAtual;email;password");
            } catch (IOException e) {
                System.err.println("Erro ao criar ficheiro CSV de estudantes: " + e.getMessage());
            }
        }
    }

    private void carregarDoCSV() {
        estudantes.clear();
        File ficheiro = new File(FICHEIRO_CSV);
        if (!ficheiro.exists()) return;

        int maiorNumero = Estudante.getContadorSequencial();

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
                if (campos.length < 8) continue;

                String nome = campos[0];
                LocalDate dataNascimento = LocalDate.parse(campos[1]);
                String nif = campos[2];
                String morada = campos[3];
                String numMecanografico = campos[4];
                int anoAtual = Integer.parseInt(campos[5]);
                String email = campos[6];
                String password = campos[7];

                Estudante estudante = new Estudante(nome, dataNascimento, nif, morada);
                estudante.setNumMecanografico(numMecanografico);
                estudante.setAnoAtual(anoAtual);
                estudante.setEmail(email);
                estudante.setPassword(password);

                estudantes.add(estudante);

                try {
                    int numero = Integer.parseInt(numMecanografico);
                    if (numero >= maiorNumero) {
                        maiorNumero = numero + 1;
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            Estudante.setContadorSequencial(maiorNumero);

        } catch (IOException e) {
            System.err.println("Erro ao carregar estudantes do CSV: " + e.getMessage());
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FICHEIRO_CSV))) {
            pw.println("nome;dataNascimento;nif;morada;numMecanografico;anoAtual;email;password");

            for (Estudante estudante : estudantes) {
                pw.println(
                        estudante.getNome() + SEPARADOR +
                                estudante.getDataNascimento() + SEPARADOR +
                                estudante.getNif() + SEPARADOR +
                                estudante.getMorada() + SEPARADOR +
                                estudante.getNumMecanografico() + SEPARADOR +
                                estudante.getAnoAtual() + SEPARADOR +
                                estudante.getEmail() + SEPARADOR +
                                estudante.getPassword()
                );
            }

        } catch (IOException e) {
            System.err.println("Erro ao guardar estudantes no CSV: " + e.getMessage());
        }
    }
}