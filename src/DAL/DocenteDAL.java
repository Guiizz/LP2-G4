package DAL;

import Model.Docente;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DocenteDAL {

    private static final String FICHEIRO_CSV = "csv/docentes.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "nome;dataNascimento;nif;morada;sigla;ucsLecionadas;password;primeiroLogin";

    private ArrayList<Docente> docentes;
    private IUnidadeCurricularDAL unidadeCurricularDAL;

    public DocenteDAL() {
        this(new UnidadeCurricularDAL());
    }

    public DocenteDAL(IUnidadeCurricularDAL unidadeCurricularDAL) {
        this.unidadeCurricularDAL = unidadeCurricularDAL;
        this.docentes = new ArrayList<>();
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);
        carregarDoCSV();
    }

    public void adicionarDocente(Docente docente) {
        docentes.add(docente);
        guardarNoCSV();
    }

    public boolean atualizarDocente(Docente docenteAtualizado) {
        for (int i = 0; i < docentes.size(); i++) {
            if (docentes.get(i).getSigla().equalsIgnoreCase(docenteAtualizado.getSigla())) {
                docentes.set(i, docenteAtualizado);
                guardarNoCSV();
                return true;
            }
        }
        return false;
    }

    public ArrayList<Docente> listarDocentes() {
        return new ArrayList<>(docentes);
    }

    public void removerDocente(Docente docente) {
        docentes.remove(docente);
        guardarNoCSV();
    }

    public Docente procurarPorSigla(String sigla) {
        for (Docente d : docentes) {
            if (d.getSigla().equalsIgnoreCase(sigla)) return d;
        }
        return null;
    }

    public Docente procurarPorNif(String nif) {
        for (Docente d : docentes) {
            if (d.getNif().equals(nif)) return d;
        }
        return null;
    }

    public Docente procurarPorEmail(String email) {
        for (Docente d : docentes) {
            if (d.getEmail().equalsIgnoreCase(email)) return d;
        }
        return null;
    }

    private void carregarDoCSV() {
        docentes.clear();
        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR)) {
            if (campos.length < 8) continue;
            try {
                String nome = campos[0];
                LocalDate dataNascimento = LocalDate.parse(campos[1]);
                String nif = campos[2];
                String morada = campos[3];
                String sigla = campos[4];
                String nomesUCs = campos[5];
                String password = campos[6];
                boolean primeiroLogin = Boolean.parseBoolean(campos[7]);

                List<UnidadeCurricular> unidades = new ArrayList<>();
                if (!nomesUCs.isBlank()) {
                    for (String nomeUC : nomesUCs.split(",")) {
                        UnidadeCurricular uc = unidadeCurricularDAL.procurarPorNome(nomeUC.trim());
                        if (uc != null) unidades.add(uc);
                    }
                }

                Docente docente = new Docente(nome, dataNascimento, nif, morada, sigla, unidades);
                docente.setPassword(password);
                docente.setPrimeiroLogin(primeiroLogin);
                docentes.add(docente);
            } catch (Exception e) {
                System.err.println("Erro ao carregar docentes do CSV: " + e.getMessage());
            }
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO)) {
            for (Docente docente : docentes) {
                List<UnidadeCurricular> unidades = docente.getUnidadesLecionadas();
                StringBuilder nomesUCs = new StringBuilder();
                if (unidades != null) {
                    for (int i = 0; i < unidades.size(); i++) {
                        nomesUCs.append(unidades.get(i).getNome());
                        if (i < unidades.size() - 1) nomesUCs.append(",");
                    }
                }
                pw.println(
                        docente.getNome()           + SEPARADOR +
                                docente.getDataNascimento() + SEPARADOR +
                                docente.getNif()            + SEPARADOR +
                                docente.getMorada()         + SEPARADOR +
                                docente.getSigla()          + SEPARADOR +
                                nomesUCs                    + SEPARADOR +
                                docente.getPassword()       + SEPARADOR +
                                docente.isPrimeiroLogin()
                );
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar docentes no CSV: " + e.getMessage());
        }
    }
}
