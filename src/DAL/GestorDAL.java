package DAL;

import Model.Gestor;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Camada DAL para a entidade Gestor.
 * Responsável por armazenar e recuperar gestores com persistência em ficheiro CSV.
 */
public class GestorDAL {

    private static final String FICHEIRO_CSV = "csv/gestores.csv";
    private static final String SEPARADOR = ";";

    private ArrayList<Gestor> gestores;

    public GestorDAL() {
        this.gestores = new ArrayList<>();
        criarFicheiroCsvSeNaoExistir();
        carregarDoCSV();
    }

    public void adicionarGestor(Gestor gestor) {
        gestores.add(gestor);
        guardarNoCSV();
    }

    public boolean atualizarGestor(Gestor gestorAtualizado) {
        for (int i = 0; i < gestores.size(); i++) {
            if (gestores.get(i).getNif().equals(gestorAtualizado.getNif())) {
                gestores.set(i, gestorAtualizado);
                guardarNoCSV();
                return true;
            }
        }
        return false;
    }

    public ArrayList<Gestor> listarGestores() {
        return new ArrayList<>(gestores);
    }

    public void removerGestor(Gestor gestor) {
        gestores.remove(gestor);
        guardarNoCSV();
    }

    public Gestor procurarPorNif(String nif) {
        for (Gestor gestor : gestores) {
            if (gestor.getNif().equals(nif)) {
                return gestor;
            }
        }
        return null;
    }

    public Gestor procurarPorEmail(String email) {
        for (Gestor gestor : gestores) {
            if (gestor.getEmail().equalsIgnoreCase(email)) {
                return gestor;
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
                pw.println("nome;nif;dataNascimento;morada;email;password;primeiroLogin");
            } catch (IOException e) {
                System.err.println("Erro ao criar ficheiro CSV de gestores: " + e.getMessage());
            }
        }
    }

    private void carregarDoCSV() {
        gestores.clear();
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
                if (campos.length < 7) continue;  // era 6, passa a 7

                String nome          = campos[0];
                String nif           = campos[1];
                LocalDate dataNascimento = LocalDate.parse(campos[2]);
                String morada        = campos[3];
                String email         = campos[4];
                String password      = campos[5];
                boolean primeiroLogin = Boolean.parseBoolean(campos[6]);

                Gestor gestor = new Gestor(nome, dataNascimento, nif, morada, email, password);
                gestor.setPrimeiroLogin(primeiroLogin);
                gestores.add(gestor);
            }

        } catch (IOException e) {
            System.err.println("Erro ao carregar gestores do CSV: " + e.getMessage());
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FICHEIRO_CSV))) {
            pw.println("nome;nif;dataNascimento;morada;email;password;primeiroLogin");

            for (Gestor gestor : gestores) {
                pw.println(
                        gestor.getNome()            + SEPARADOR +
                                gestor.getNif()             + SEPARADOR +
                                gestor.getDataNascimento()  + SEPARADOR +
                                gestor.getMorada()          + SEPARADOR +
                                gestor.getEmail()           + SEPARADOR +
                                gestor.getPassword()        + SEPARADOR +
                                gestor.isPrimeiroLogin()
                );
            }

        } catch (IOException e) {
            System.err.println("Erro ao guardar gestores no CSV: " + e.getMessage());
        }
    }
}