package DAL;

import Model.Curso;
import Model.Departamento;
import Model.UnidadeCurricular;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Camada DAL para a entidade Curso.
 * Responsável por armazenar e recuperar cursos com persistência em ficheiro CSV.
 */
public class CursoDAL {

    private static final String FICHEIRO_CSV = "csv/cursos.csv";
    private static final String SEPARADOR = ";";

    private ArrayList<Curso> cursos;
    private DepartamentoDAL departamentoDAL;
    private UnidadeCurricularDAL unidadeCurricularDAL;

    public CursoDAL() {
        this(new DepartamentoDAL(), new UnidadeCurricularDAL());
    }

    public CursoDAL(DepartamentoDAL departamentoDAL, UnidadeCurricularDAL unidadeCurricularDAL) {
        this.departamentoDAL = departamentoDAL;
        this.unidadeCurricularDAL = unidadeCurricularDAL;
        this.cursos = new ArrayList<>();
        criarFicheiroCsvSeNaoExistir();
        carregarDoCSV();
    }

    public void adicionarCurso(Curso curso) {
        cursos.add(curso);
        guardarNoCSV();
    }

    public boolean atualizarCurso(Curso cursoAtualizado) {
        for (int i = 0; i < cursos.size(); i++) {
            Curso atual = cursos.get(i);

            if (atual.getNomeCurso().equalsIgnoreCase(cursoAtualizado.getNomeCurso())
                    && atual.getDepartamento() != null
                    && cursoAtualizado.getDepartamento() != null
                    && atual.getDepartamento().getSigla().equalsIgnoreCase(cursoAtualizado.getDepartamento().getSigla())) {
                cursos.set(i, cursoAtualizado);
                guardarNoCSV();
                return true;
            }
        }
        return false;
    }

    public ArrayList<Curso> listarCursos() {
        return new ArrayList<>(cursos);
    }

    public void removerCurso(Curso curso) {
        cursos.remove(curso);
        guardarNoCSV();
    }

    public Curso procurarPorNome(String nomeCurso) {
        for (Curso curso : cursos) {
            if (curso.getNomeCurso().equalsIgnoreCase(nomeCurso)) {
                return curso;
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
                pw.println("nomeCurso;siglaDepartamento;nomesUCs;estado");
            } catch (IOException e) {
                System.err.println("Erro ao criar ficheiro CSV de cursos: " + e.getMessage());
            }
        }
    }

    private void carregarDoCSV() {
        cursos.clear();
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

                String nomeCurso = campos[0];
                String siglaDepartamento = campos[1];
                String nomesUCs = campos[2];

                String estado = "PENDENTE";
                if (campos.length > 3 && !campos[3].isBlank()) {
                    estado = campos[3];
                }

                Departamento departamento = departamentoDAL.procurarPorSigla(siglaDepartamento);
                if (departamento == null) continue;

                Curso curso = new Curso(nomeCurso, departamento);
                curso.setEstado(estado);
                if (!nomesUCs.isBlank()) {
                    String[] nomes = nomesUCs.split(",");
                    for (String nomeUC : nomes) {
                        UnidadeCurricular uc = unidadeCurricularDAL.procurarPorNome(nomeUC.trim());
                        if (uc != null) {
                            curso.adicionarUnidadeCurricular(uc);
                        }
                    }
                }

                cursos.add(curso);
                departamento.adicionarCurso(curso);
            }

        } catch (IOException e) {
            System.err.println("Erro ao carregar cursos do CSV: " + e.getMessage());
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FICHEIRO_CSV))) {
            pw.println("nomeCurso;siglaDepartamento;nomesUCs;estado");
            for (Curso curso : cursos) {
                String siglaDepartamento = "";
                if (curso.getDepartamento() != null) {
                    siglaDepartamento = curso.getDepartamento().getSigla();
                }

                List<UnidadeCurricular> ucs = curso.getUnidades();
                StringBuilder nomesUCs = new StringBuilder();

                if (ucs != null) {
                    for (int i = 0; i < ucs.size(); i++) {
                        nomesUCs.append(ucs.get(i).getNome());
                        if (i < ucs.size() - 1) {
                            nomesUCs.append(",");
                        }
                    }
                }

                pw.println(
                        curso.getNomeCurso() + SEPARADOR +
                                siglaDepartamento + SEPARADOR +
                                nomesUCs + SEPARADOR +
                                curso.getEstado()
                );
            }

        } catch (IOException e) {
            System.err.println("Erro ao guardar cursos no CSV: " + e.getMessage());
        }
    }
}