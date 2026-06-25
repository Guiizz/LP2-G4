package DAL;

import Model.Curso;
import Model.Departamento;
import Model.UCNoCurso;
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
                for (String entrada : nomesUCs.split(",")) {
                    entrada = entrada.trim();
                    String nomeUC;
                    int anoCurricular = 1;
                    int at = entrada.lastIndexOf('@');
                    if (at >= 0) {
                        nomeUC = entrada.substring(0, at).trim();
                        try { anoCurricular = Integer.parseInt(entrada.substring(at + 1).trim()); }
                        catch (NumberFormatException ignored) {}
                    } else {
                        nomeUC = entrada;
                    }
                    UnidadeCurricular uc = unidadeCurricularDAL.procurarPorNome(nomeUC);
                    if (uc != null) curso.adicionarUnidadeCurricular(uc, anoCurricular);
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
                List<UCNoCurso> ucsNoCurso = curso.getUCsNoCurso();
                StringBuilder nomesUCs = new StringBuilder();
                if (ucsNoCurso != null) {
                    for (int i = 0; i < ucsNoCurso.size(); i++) {
                        UCNoCurso u = ucsNoCurso.get(i);
                        nomesUCs.append(u.getUc().getNome()).append("@").append(u.getAnoCurricular());
                        if (i < ucsNoCurso.size() - 1) nomesUCs.append(",");
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