package DAL.BD;

import DAL.ICursoDAL;
import DAL.IDepartamentoDAL;
import DAL.IUnidadeCurricularDAL;
import Model.Curso;
import Model.Departamento;
import Model.UCNoCurso;
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
            guardarUCsDoCurso(curso.getNomeCurso(), curso.getUCsNoCurso());
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
                guardarUCsDoCurso(cursoAtualizado.getNomeCurso(), cursoAtualizado.getUCsNoCurso());
                atualizado[0] = true;
            }
        });
        return atualizado[0];
    }

    @Override
    public ArrayList<Curso> listarCursos() {
        // Curso + Departamento + associações CursoUC numa única query.
        ArrayList<Object[]> rows = conexao.select(
                "SELECT c.nomeCurso, c.siglaDepartamento, dep.nome AS nomeDep, " +
                "       c.estado, c.valorPropina, cu.nomeUC, cu.anoCurricular " +
                "FROM   Curso c " +
                "LEFT   JOIN Departamento dep ON dep.sigla = c.siglaDepartamento " +
                "LEFT   JOIN CursoUC cu       ON cu.nomeCurso = c.nomeCurso " +
                "ORDER  BY c.nomeCurso",
                rs -> new Object[]{
                        rs.getString("nomeCurso"),
                        rs.getString("siglaDepartamento"),
                        rs.getString("nomeDep"),
                        rs.getString("estado"),
                        rs.getDouble("valorPropina"),
                        rs.getString("nomeUC"),
                        rs.getObject("anoCurricular")
                }
        );

        // UCs completas (com momentos de avaliação, docente, etc.) carregadas
        // em lote uma única vez — evita criar UCs "vazias" sem momentos, o que
        // fazia iniciarCurso() falhar mesmo havendo momentos válidos na BD.
        java.util.Map<String, UnidadeCurricular> ucsPorNome = new java.util.HashMap<>();
        for (UnidadeCurricular uc : unidadeCurricularDAL.listarUnidades()) {
            ucsPorNome.put(uc.getNome(), uc);
        }

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
                int anoCurricular = row[6] instanceof Number ? ((Number) row[6]).intValue() : 1;
                boolean jaAdicionada = curso.getUnidades() != null &&
                        curso.getUnidades().stream().anyMatch(u -> u.getNome().equalsIgnoreCase(nomeUC));
                if (!jaAdicionada) {
                    UnidadeCurricular uc = ucsPorNome.get(nomeUC);
                    if (uc != null) curso.adicionarUnidadeCurricular(uc, anoCurricular);
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

    private void guardarUCsDoCurso(String nomeCurso, List<UCNoCurso> ucs) {
        if (ucs == null) return;
        for (UCNoCurso u : ucs) {
            conexao.execute(
                    "INSERT INTO CursoUC (nomeCurso, nomeUC, anoCurricular) VALUES (?, ?, ?)",
                    nomeCurso, u.getUc().getNome(), u.getAnoCurricular()
            );
        }
    }

    private void carregarUCsDoCurso(Curso curso) {
        ArrayList<Object[]> rows = conexao.select(
                "SELECT nomeUC, anoCurricular FROM CursoUC WHERE nomeCurso = ?",
                rs -> new Object[]{ rs.getString("nomeUC"), rs.getInt("anoCurricular") },
                curso.getNomeCurso()
        );
        for (Object[] row : rows) {
            String nomeUC = (String) row[0];
            int anoCurricular = (int) row[1];
            UnidadeCurricular uc = unidadeCurricularDAL.procurarPorNome(nomeUC);
            if (uc != null) curso.adicionarUnidadeCurricular(uc, anoCurricular);
        }
    }
}
