package DAL;

import Model.Docente;
import Model.UnidadeCurricular;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Camada DAL para a entidade Docente.
 * Responsável por armazenar e recuperar docentes com persistência em ficheiro CSV.
 */
public class DocenteDAL {

    private static final String FICHEIRO_CSV = "csv/docentes.csv";
    private static final String SEPARADOR = ";";

    private ArrayList<Docente> docentes;
    private UnidadeCurricularDAL unidadeCurricularDAL;

    public DocenteDAL() {
        this(new UnidadeCurricularDAL());
    }

    public DocenteDAL(UnidadeCurricularDAL unidadeCurricularDAL) {
        this.unidadeCurricularDAL = unidadeCurricularDAL;
        this.docentes = new ArrayList<>();
        criarFicheiroCsvSeNaoExistir();
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
        for (Docente docente : docentes) {
            if (docente.getSigla().equalsIgnoreCase(sigla)) {
                return docente;
            }
        }
        return null;
    }

    public Docente procurarPorNif(String nif) {
        for (Docente docente : docentes) {
            if (docente.getNif().equals(nif)) {
                return docente;
            }
        }
        return null;
    }

    public Docente procurarPorEmail(String email) {
        for (Docente docente : docentes) {
            if (docente.getEmail().equalsIgnoreCase(email)) {
                return docente;
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
                pw.println("nome;dataNascimento;nif;morada;sigla;ucsLecionadas");
            } catch (IOException e) {
                System.err.println("Erro ao criar ficheiro CSV de docentes: " + e.getMessage());
            }
        }
    }

    private void carregarDoCSV() {
        docentes.clear();
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
                if (campos.length < 6) continue;

                String nome = campos[0];
                LocalDate dataNascimento = LocalDate.parse(campos[1]);
                String nif = campos[2];
                String morada = campos[3];
                String sigla = campos[4];
                String nomesUCs = campos[5];

                List<UnidadeCurricular> unidades = new ArrayList<>();
                if (!nomesUCs.isBlank()) {
                    String[] nomes = nomesUCs.split(",");
                    for (String nomeUC : nomes) {
                        UnidadeCurricular uc = unidadeCurricularDAL.procurarPorNome(nomeUC.trim());
                        if (uc != null) {
                            unidades.add(uc);
                        }
                    }
                }

                docentes.add(new Docente(nome, dataNascimento, nif, morada, sigla, unidades));
            }

        } catch (IOException e) {
            System.err.println("Erro ao carregar docentes do CSV: " + e.getMessage());
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FICHEIRO_CSV))) {
            pw.println("nome;dataNascimento;nif;morada;sigla;ucsLecionadas");

            for (Docente docente : docentes) {
                List<UnidadeCurricular> unidades = docente.getUnidadesLecionadas();
                StringBuilder nomesUCs = new StringBuilder();

                if (unidades != null) {
                    for (int i = 0; i < unidades.size(); i++) {
                        nomesUCs.append(unidades.get(i).getNome());
                        if (i < unidades.size() - 1) {
                            nomesUCs.append(",");
                        }
                    }
                }

                pw.println(
                        docente.getNome() + SEPARADOR +
                                docente.getDataNascimento() + SEPARADOR +
                                docente.getNif() + SEPARADOR +
                                docente.getMorada() + SEPARADOR +
                                docente.getSigla() + SEPARADOR +
                                nomesUCs
                );
            }

        } catch (IOException e) {
            System.err.println("Erro ao guardar docentes no CSV: " + e.getMessage());
        }
    }
}