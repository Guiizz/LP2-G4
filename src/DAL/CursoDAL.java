package DAL;

import Model.Curso;
import Model.Departamento;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAL implements ICursoDAL {

    private static final String FICHEIRO_CSV = "csv/cursos.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "nomeCurso;siglaDepartamento;nomesUCs;estado;valorPropina";

    private ArrayList<Curso> cursos;
    private IDepartamentoDAL departamentoDAL;
    private IUnidadeCurricularDAL unidadeCurricularDAL;

    public CursoDAL() {
        this(new DepartamentoDAL(), new UnidadeCurricularDAL());
    }

    public CursoDAL(IDepartamentoDAL departamentoDAL, IUnidadeCurricularDAL unidadeCurricularDAL) {
        this.departamentoDAL = departamentoDAL;
        this.unidadeCurricularDAL = unidadeCurricularDAL;
        this.cursos = new ArrayList<>();
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);
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
            if (curso.getNomeCurso().equalsIgnoreCase(nomeCurso)) return curso;
        }
        return null;
    }

    private void carregarDoCSV() {
        cursos.clear();
        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR)) {
            if (campos.length < 3) continue;

            String nomeCurso = campos[0];
            String siglaDepartamento = campos[1];
            String nomesUCs = campos[2];
            String estado = campos.length > 3 && !campos[3].isBlank() ? campos[3] : "PENDENTE";
            double valorPropina = 0.0;
            if (campos.length > 4 && !campos[4].isBlank()) {
                try { valorPropina = Double.parseDouble(campos[4]); }
                catch (NumberFormatException ignored) {}
            }

            Departamento departamento = departamentoDAL.procurarPorSigla(siglaDepartamento);
            if (departamento == null) continue;

            Curso curso = new Curso(nomeCurso, departamento);
            curso.setEstado(estado);
            curso.setValorPropina(valorPropina);

            if (!nomesUCs.isBlank()) {
                for (String nomeUC : nomesUCs.split(",")) {
                    UnidadeCurricular uc = unidadeCurricularDAL.procurarPorNome(nomeUC.trim());
                    if (uc != null) curso.adicionarUnidadeCurricular(uc);
                }
            }

            cursos.add(curso);
            departamento.adicionarCurso(curso);
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO)) {
            for (Curso curso : cursos) {
                String siglaDepartamento = curso.getDepartamento() != null ? curso.getDepartamento().getSigla() : "";
                List<UnidadeCurricular> ucs = curso.getUnidades();
                StringBuilder nomesUCs = new StringBuilder();
                if (ucs != null) {
                    for (int i = 0; i < ucs.size(); i++) {
                        nomesUCs.append(ucs.get(i).getNome());
                        if (i < ucs.size() - 1) nomesUCs.append(",");
                    }
                }
                pw.println(
                        curso.getNomeCurso() + SEPARADOR +
                                siglaDepartamento + SEPARADOR +
                                nomesUCs + SEPARADOR +
                                curso.getEstado()
                                + SEPARADOR + curso.getValorPropina()
                );
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar cursos no CSV: " + e.getMessage());
        }
    }
}