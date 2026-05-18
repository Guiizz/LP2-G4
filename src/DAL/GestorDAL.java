package DAL;

import Model.Gestor;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import Utils.PasswordUtils;
import Utils.Utils;

/**
 * Camada DAL para a entidade Gestor.
 * Responsável por armazenar e recuperar gestores com persistência em ficheiro CSV.
 */
public class GestorDAL {

    private static final String FICHEIRO_CSV = "csv/gestores.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "nome;nif;dataNascimento;morada;email;password;primeiroLogin";

    private ArrayList<Gestor> gestores;

    public GestorDAL() {
        this.gestores = new ArrayList<>();
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);
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

    private void carregarDoCSV() {
        gestores.clear();
        List<String[]> linhas = Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR);

        for (String[] campos : linhas) {
            if (campos.length < 7) continue;

            String nome              = campos[0];
            String nif               = campos[1];
            LocalDate dataNascimento = LocalDate.parse(campos[2]);
            String morada            = campos[3];
            String email             = campos[4];
            String password          = campos[5];
            if (!PasswordUtils.estaHasheada(password)) {
                password = PasswordUtils.hashPassword(password);
            }
            boolean primeiroLogin = Boolean.parseBoolean(campos[6]);

            Gestor gestor = new Gestor(nome, dataNascimento, nif, morada, email, password);
            gestor.setPrimeiroLogin(primeiroLogin);
            gestores.add(gestor);
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO)) {
            for (Gestor gestor : gestores) {
                pw.println(
                        gestor.getNome()           + SEPARADOR +
                                gestor.getNif()            + SEPARADOR +
                                gestor.getDataNascimento() + SEPARADOR +
                                gestor.getMorada()         + SEPARADOR +
                                gestor.getEmail()          + SEPARADOR +
                                gestor.getPassword()       + SEPARADOR +
                                gestor.isPrimeiroLogin()
                );
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar gestores no CSV: " + e.getMessage());
        }
    }
}