package DAL;

import Model.Estudante;
import Utils.PasswordUtils;
import Utils.Utils;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class EstudanteDAL {

    private static final String FICHEIRO_CSV = "csv/estudantes.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO_ESTUDANTES = "nome;dataNascimento;nif;morada;numMecanografico;anoAtual;email;password;primeiroLogin;estado";

    private ArrayList<Estudante> estudantes;
    private final ICursoDAL cursoDAL;
    private final IInscricaoDAL inscricaoDAL;

    public EstudanteDAL(ICursoDAL cursoDAL, IInscricaoDAL inscricaoDAL) {
        this.estudantes   = new ArrayList<>();
        this.cursoDAL     = cursoDAL;
        this.inscricaoDAL = inscricaoDAL;
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO_ESTUDANTES);
        carregarDoCSV();
        inscricaoDAL.carregarInscricoes(estudantes, cursoDAL);
    }

    /** Construtor de conveniência para o modo ficheiro (sem injeção manual). */
    public EstudanteDAL(ICursoDAL cursoDAL) {
        this(cursoDAL, new InscricaoDAL());
    }

    public EstudanteDAL() {
        this(new CursoDAL());
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
            if (e.getNumMecanografico().equals(numMecanografico)) return e;
        }
        return null;
    }

    public Estudante procurarPorNif(String nif) {
        for (Estudante e : estudantes) {
            if (e.getNif().equals(nif)) return e;
        }
        return null;
    }

    public Estudante procurarPorEmail(String email) {
        for (Estudante e : estudantes) {
            if (e.getEmail().equalsIgnoreCase(email)) return e;
        }
        return null;
    }

    private void carregarDoCSV() {
        estudantes.clear();
        int maiorNumero = Estudante.getContadorSequencial();

        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR)) {
            if (campos.length < 9) continue;
            try {
                String nome              = campos[0];
                LocalDate dataNascimento = LocalDate.parse(campos[1]);
                String nif               = campos[2];
                String morada            = campos[3];
                String numMecanografico  = campos[4];
                int anoAtual             = Integer.parseInt(campos[5]);
                String email             = campos[6];
                String password          = campos[7];

                if (!PasswordUtils.estaHasheada(password)) {
                    password = PasswordUtils.hashPassword(password);
                }

                boolean primeiroLogin = Boolean.parseBoolean(campos[8]);
                String estado = campos.length >= 10 && !campos[9].isBlank() ? campos[9] : "ATIVO";

                Estudante estudante = new Estudante(nome, dataNascimento, nif, morada, true);
                estudante.setNumMecanografico(numMecanografico);
                estudante.setAnoAtual(anoAtual);
                estudante.setEmail(email);
                estudante.setPassword(password);
                estudante.setPrimeiroLogin(primeiroLogin);
                estudante.setEstado(estado);
                estudantes.add(estudante);

                try {
                    int numero = Integer.parseInt(numMecanografico);
                    if (numero >= maiorNumero) maiorNumero = numero + 1;
                } catch (NumberFormatException ignored) {}

            } catch (Exception e) {
                System.err.println("Erro ao carregar estudantes do CSV: " + e.getMessage());
            }
        }
        Estudante.setContadorSequencial(maiorNumero);
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO_ESTUDANTES)) {
            for (Estudante estudante : estudantes) {
                pw.println(
                        estudante.getNome()             + SEPARADOR +
                        estudante.getDataNascimento()   + SEPARADOR +
                        estudante.getNif()              + SEPARADOR +
                        estudante.getMorada()           + SEPARADOR +
                        estudante.getNumMecanografico() + SEPARADOR +
                        estudante.getAnoAtual()         + SEPARADOR +
                        estudante.getEmail()            + SEPARADOR +
                        estudante.getPassword()         + SEPARADOR +
                        estudante.isPrimeiroLogin()     + SEPARADOR +
                        estudante.getEstado()
                );
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar estudantes no CSV: " + e.getMessage());
        }
        inscricaoDAL.guardarInscricoes(estudantes);
    }
}
