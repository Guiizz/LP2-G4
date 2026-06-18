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
 *
 * Esquema esperado:
 *   CREATE TABLE Curso (
 *       nomeCurso         VARCHAR(100) NOT NULL,
 *       siglaDepartamento VARCHAR(10)  NOT NULL,
 *       estado            VARCHAR(20)  NOT NULL DEFAULT 'PENDENTE',
 *       valorPropina      FLOAT        NOT NULL DEFAULT 0,
 *       CONSTRAINT PK_Curso PRIMARY KEY (nomeCurso),
 *       CONSTRAINT FK_Curso_Departamento FOREIGN KEY (siglaDepartamento)
 *           REFERENCES Departamento(sigla)
 *   );
 *
 *   CREATE TABLE CursoUC (
 *       nomeCurso VARCHAR(100) NOT NULL,
 *       nomeUC    VARCHAR(100) NOT NULL,
 *       CONSTRAINT PK_CursoUC PRIMARY KEY (nomeCurso, nomeUC),
 *       CONSTRAINT FK_CursoUC_Curso FOREIGN KEY (nomeCurso)
 *           REFERENCES Curso(nomeCurso) ON DELETE CASCADE ON UPDATE CASCADE,
 *       CONSTRAINT FK_CursoUC_UnidadeCurricular FOREIGN KEY (nomeUC)
 *           REFERENCES UnidadeCurricular(nome) ON DELETE NO ACTION ON UPDATE CASCADE
 *   );
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
        conexao.execute(
                "INSERT INTO Curso (nomeCurso, siglaDepartamento, estado, valorPropina) " +
                "VALUES (?, ?, ?, ?)",
                curso.getNomeCurso(),
                curso.getDepartamento() != null ? curso.getDepartamento().getSigla() : null,
                curso.getEstado(),
                curso.getValorPropina()
        );
        guardarUCsDoCurso(curso.getNomeCurso(), curso.getUnidades());
        if (curso.getDepartamento() != null) {
            curso.getDepartamento().adicionarCurso(curso);
        }
    }

    @Override
    public boolean atualizarCurso(Curso cursoAtualizado) {
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
        }
        return linhas > 0;
    }

    @Override
    public ArrayList<Curso> listarCursos() {
        ArrayList<Curso> cursos = conexao.select(
                "SELECT nomeCurso, siglaDepartamento, estado, valorPropina FROM Curso",
                rs -> mapCurso(
                        rs.getString("nomeCurso"),
                        rs.getString("siglaDepartamento"),
                        rs.getString("estado"),
                        rs.getDouble("valorPropina")
                )
        );
        for (Curso c : cursos) {
            if (c != null) carregarUCsDoCurso(c);
        }
        return cursos;
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
