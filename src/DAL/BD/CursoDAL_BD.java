package DAL.BD;

import DAL.ICursoDAL;
import DAL.IDepartamentoDAL;
import DAL.UnidadeCurricularDAL;
import Model.Curso;
import Model.Departamento;
import Model.UnidadeCurricular;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da persistência de Curso em base de dados (SQL Server).
 *
 * Esquema esperado:
 *   CREATE TABLE Curso (
 *       nomeCurso        VARCHAR(100) NOT NULL,
 *       siglaDepartamento VARCHAR(10) NOT NULL,
 *       nomesUCs         VARCHAR(MAX) NULL,   -- serializado até UC ser migrada
 *       estado           VARCHAR(20)  NOT NULL DEFAULT 'PENDENTE',
 *       valorPropina     FLOAT        NOT NULL DEFAULT 0,
 *       CONSTRAINT PK_Curso PRIMARY KEY (nomeCurso),
 *       CONSTRAINT FK_Curso_Departamento FOREIGN KEY (siglaDepartamento)
 *           REFERENCES Departamento(sigla)
 *   );
 */
public class CursoDAL_BD implements ICursoDAL {

    private final ConexaoBD conexao;
    private final IDepartamentoDAL departamentoDAL;
    private final UnidadeCurricularDAL unidadeCurricularDAL;

    public CursoDAL_BD(IDepartamentoDAL departamentoDAL, UnidadeCurricularDAL unidadeCurricularDAL) {
        this.conexao               = new ConexaoBD();
        this.departamentoDAL       = departamentoDAL;
        this.unidadeCurricularDAL  = unidadeCurricularDAL;
    }

    @Override
    public void adicionarCurso(Curso curso) {
        conexao.execute(
                "INSERT INTO Curso (nomeCurso, siglaDepartamento, nomesUCs, estado, valorPropina) " +
                "VALUES (?, ?, ?, ?, ?)",
                curso.getNomeCurso(),
                curso.getDepartamento() != null ? curso.getDepartamento().getSigla() : null,
                serializarUCs(curso.getUnidades()),
                curso.getEstado(),
                curso.getValorPropina()
        );
        if (curso.getDepartamento() != null) {
            curso.getDepartamento().adicionarCurso(curso);
        }
    }

    @Override
    public boolean atualizarCurso(Curso cursoAtualizado) {
        int linhas = conexao.execute(
                "UPDATE Curso SET siglaDepartamento = ?, nomesUCs = ?, estado = ?, valorPropina = ? " +
                "WHERE nomeCurso = ?",
                cursoAtualizado.getDepartamento() != null ? cursoAtualizado.getDepartamento().getSigla() : null,
                serializarUCs(cursoAtualizado.getUnidades()),
                cursoAtualizado.getEstado(),
                cursoAtualizado.getValorPropina(),
                cursoAtualizado.getNomeCurso()
        );
        return linhas > 0;
    }

    @Override
    public ArrayList<Curso> listarCursos() {
        return conexao.select(
                "SELECT nomeCurso, siglaDepartamento, nomesUCs, estado, valorPropina FROM Curso",
                rs -> mapCurso(
                        rs.getString("nomeCurso"),
                        rs.getString("siglaDepartamento"),
                        rs.getString("nomesUCs"),
                        rs.getString("estado"),
                        rs.getDouble("valorPropina")
                )
        );
    }

    @Override
    public void removerCurso(Curso curso) {
        conexao.execute("DELETE FROM Curso WHERE nomeCurso = ?", curso.getNomeCurso());
    }

    @Override
    public Curso procurarPorNome(String nomeCurso) {
        ArrayList<Curso> resultados = conexao.select(
                "SELECT nomeCurso, siglaDepartamento, nomesUCs, estado, valorPropina " +
                "FROM Curso WHERE nomeCurso = ?",
                rs -> mapCurso(
                        rs.getString("nomeCurso"),
                        rs.getString("siglaDepartamento"),
                        rs.getString("nomesUCs"),
                        rs.getString("estado"),
                        rs.getDouble("valorPropina")
                ),
                nomeCurso
        );
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    // -------------------------------------------------------------------------
    // Auxiliares
    // -------------------------------------------------------------------------

    private Curso mapCurso(String nomeCurso, String siglaDep, String nomesUCs,
                           String estado, double valorPropina) {
        Departamento departamento = departamentoDAL.procurarPorSigla(siglaDep);
        if (departamento == null) return null;

        Curso curso = new Curso(nomeCurso, departamento);
        curso.setEstado(estado);
        curso.setValorPropina(valorPropina);

        if (nomesUCs != null && !nomesUCs.isBlank()) {
            for (String nomeUC : nomesUCs.split(",")) {
                UnidadeCurricular uc = unidadeCurricularDAL.procurarPorNome(nomeUC.trim());
                if (uc != null) curso.adicionarUnidadeCurricular(uc);
            }
        }

        departamento.adicionarCurso(curso);
        return curso;
    }

    private String serializarUCs(List<UnidadeCurricular> ucs) {
        if (ucs == null || ucs.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ucs.size(); i++) {
            sb.append(ucs.get(i).getNome());
            if (i < ucs.size() - 1) sb.append(",");
        }
        return sb.toString();
    }
}
