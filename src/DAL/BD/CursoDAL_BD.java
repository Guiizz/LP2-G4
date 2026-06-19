package DAL.BD;

import DAL.ICursoDAL;
import DAL.IDepartamentoDAL;
import DAL.IUnidadeCurricularDAL;
import Model.Curso;
import Model.Departamento;
import Model.UnidadeCurricular;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da persistência de Curso em base de dados (SQL Server).
 */
public class CursoDAL_BD implements ICursoDAL {

    private final ConexaoBD conexao;
    private final IDepartamentoDAL departamentoDAL;
    private final IUnidadeCurricularDAL unidadeCurricularDAL;

    public CursoDAL_BD(IDepartamentoDAL departamentoDAL, IUnidadeCurricularDAL unidadeCurricularDAL) {
        this.conexao              = new ConexaoBD();
        this.departamentoDAL      = departamentoDAL;
        this.unidadeCurricularDAL = unidadeCurricularDAL;
    }

    @Override
    public void adicionarCurso(Curso curso) {
        conexao.executarEmTransacao(() -> {
            conexao.execute(
                    "INSERT INTO Curso (nomeCurso, siglaDepartamento, estado, valorPropina) " +
                    "VALUES (?, ?, ?, ?)",
                    curso.getNomeCurso(),
                    curso.getDepartamento() != null ? curso.getDepartamento().getSigla() : null,
                    curso.getEstado(),
                    curso.getValorPropina()
            );
            guardarUCsDoCurso(curso.getNomeCurso(), curso.getUnidades());
        });
        if (curso.getDepartamento() != null) {
            curso.getDepartamento().adicionarCurso(curso);
        }
    }

    @Override
    public boolean atualizarCurso(Curso cursoAtualizado) {
        final boolean[] atualizado = {false};
        conexao.executarEmTransacao(() -> {
            int linhas = conexao.execute(
                    "UPDATE Curso SET siglaDepartamento = ?, estado = ?, valorPropina = ? " +
                    "WHERE nomeCurso = ?",
                    cursoAtualizado.getDepartamento() != null ? cursoAtualizado.getDepartamento().getSigla() : null,
                    cursoAtualizado.getEstado(),
                    cursoAtualizado.getValorPropina(),
                    cursoAtualizado.getNomeCurso()
            );
            if (linhas > 0) {
                conexao.execute("DELETE FROM CursoUC WHERE nomeCurso = ?", cursoAtualizado.getNomeCurso());
                guardarUCsDoCurso(cursoAtualizado.getNomeCurso(), cursoAtualizado.getUnidades());
                atualizado[0] = true;
            }
        });
        return atualizado[0];
    }

    @Override
    public ArrayList<Curso> listarCursos() {
        // Uma query única com todos os JOINs necessários:
        // Curso + Departamento + CursoUC + UnidadeCurricular.
        // Elimina por completo o problema N+1 (sem queries por curso nem por UC).
        ArrayList<Object[]> rows = conexao.select(
                "SELECT c.nomeCurso, c.siglaDepartamento, dep.nome AS nomeDep, " +
                "       c.estado, c.valorPropina, " +
                "       cu.nomeUC, u.anoCurricular, u.ects, u.docenteResponsavel, u.ativa " +
                "FROM   Curso c " +
                "LEFT   JOIN Departamento dep ON dep.sigla = c.siglaDepartamento " +
                "LEFT   JOIN CursoUC cu       ON cu.nomeCurso = c.nomeCurso " +
                "LEFT   JOIN UnidadeCurricular u ON u.nome = cu.nomeUC " +
                "ORDER  BY c.nomeCurso",
                rs -> new Object[]{
                        rs.getString("nomeCurso"),
                        rs.getString("siglaDepartamento"),
                        rs.getString("nomeDep"),
                        rs.getString("estado"),
                        rs.getDouble("valorPropina"),
                        rs.getString("nomeUC"),
                        rs.getObject("anoCurricular"),
                        rs.getObject("ects"),
                        rs.getString("docenteResponsavel"),
                        rs.getObject("ativa")
                }
        );

        java.util.LinkedHashMap<String, Curso> porNome = new java.util.LinkedHashMap<>();
        for (Object[] row : rows) {
            String nomeCurso = (String) row[0];
            Curso curso = porNome.get(nomeCurso);
            if (curso == null) {
                String siglaDep = (String) row[1];
                String nomeDep  = (String) row[2];
                if (siglaDep == null || nomeDep == null) continue;
                Departamento dep = new Departamento(nomeDep, siglaDep);
                curso = new Curso(nomeCurso, dep);
                curso.setEstado((String) row[3]);
                curso.setValorPropina((double) row[4]);
                dep.adicionarCurso(curso);
                porNome.put(nomeCurso, curso);
            }
            if (row[5] != null) {
                String nomeUC = (String) row[5];
                boolean jaAdicionada = curso.getUnidades() != null &&
                        curso.getUnidades().stream().anyMatch(u -> u.getNome().equalsIgnoreCase(nomeUC));
                if (!jaAdicionada) {
                    int anoCurricular = row[6] != null ? (int) row[6] : 0;
                    int ects          = row[7] != null ? (int) row[7] : 0;
                    String siglaDoc   = (String) row[8];
                    boolean ativa     = row[9] != null && (boolean) row[9];
                    UnidadeCurricular uc = new UnidadeCurricular(nomeUC, anoCurricular, ects,
                            new ArrayList<>(), siglaDoc != null ? siglaDoc : "");
                    uc.setAtiva(ativa);
                    curso.adicionarUnidadeCurricular(uc);
                }
            }
        }
        return new ArrayList<>(porNome.values());
    }

    @Override
    public void removerCurso(Curso curso) {
        // CursoUC é eliminada automaticamente pelo ON DELETE CASCADE
        conexao.execute("DELETE FROM Curso WHERE nomeCurso = ?", curso.getNomeCurso());
    }

    @Override
    public Curso procurarPorNome(String nomeCurso) {
        ArrayList<Curso> resultados = conexao.select(
                "SELECT nomeCurso, siglaDepartamento, estado, valorPropina " +
                "FROM Curso WHERE nomeCurso = ?",
                rs -> mapCurso(
                        rs.getString("nomeCurso"),
                        rs.getString("siglaDepartamento"),
                        rs.getString("estado"),
                        rs.getDouble("valorPropina")
                ),
                nomeCurso
        );
        if (resultados.isEmpty()) return null;
        Curso curso = resultados.get(0);
        if (curso != null) carregarUCsDoCurso(curso);
        return curso;
    }

    // -------------------------------------------------------------------------
    // Auxiliares
    // -------------------------------------------------------------------------

    private Curso mapCurso(String nomeCurso, String siglaDep, String estado, double valorPropina) {
        Departamento departamento = departamentoDAL.procurarPorSigla(siglaDep);
        if (departamento == null) return null;

        Curso curso = new Curso(nomeCurso, departamento);
        curso.setEstado(estado);
        curso.setValorPropina(valorPropina);
        departamento.adicionarCurso(curso);
        return curso;
    }

    private void guardarUCsDoCurso(String nomeCurso, List<UnidadeCurricular> ucs) {
        if (ucs == null) return;
        for (UnidadeCurricular uc : ucs) {
            conexao.execute(
                    "INSERT INTO CursoUC (nomeCurso, nomeUC) VALUES (?, ?)",
                    nomeCurso, uc.getNome()
            );
        }
    }

    private void carregarUCsDoCurso(Curso curso) {
        ArrayList<String> nomesUCs = conexao.select(
                "SELECT nomeUC FROM CursoUC WHERE nomeCurso = ?",
                rs -> rs.getString("nomeUC"),
                curso.getNomeCurso()
        );
        for (String nomeUC : nomesUCs) {
            UnidadeCurricular uc = unidadeCurricularDAL.procurarPorNome(nomeUC);
            if (uc != null) curso.adicionarUnidadeCurricular(uc);
        }
    }
}
