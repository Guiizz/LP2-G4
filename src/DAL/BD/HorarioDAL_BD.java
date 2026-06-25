package DAL.BD;

import DAL.IHorarioDAL;
import Model.BlocoHorario;
import Model.Horario;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Implementação da persistência de Horário em base de dados (SQL Server).
 *
 * Esquema esperado:
 *   CREATE TABLE Horario (
 *       nomeCurso       VARCHAR(100) NOT NULL,
 *       anoCurricular   INT          NOT NULL,
 *       anoLetivo       INT          NOT NULL,
 *       CONSTRAINT PK_Horario PRIMARY KEY (nomeCurso, anoCurricular, anoLetivo),
 *       CONSTRAINT FK_Horario_Curso FOREIGN KEY (nomeCurso)
 *           REFERENCES Curso(nomeCurso) ON DELETE CASCADE
 *   );
 *
 *   CREATE TABLE BlocoHorario (
 *       id              INT IDENTITY(1,1) NOT NULL,
 *       nomeCurso       VARCHAR(100) NOT NULL,
 *       anoCurricular   INT          NOT NULL,
 *       anoLetivo       INT          NOT NULL,
 *       diaSemana       VARCHAR(20)  NOT NULL,
 *       horaInicio      VARCHAR(5)   NOT NULL,
 *       duracao         INT          NOT NULL,
 *       nomeUC          VARCHAR(100) NOT NULL,
 *       CONSTRAINT PK_BlocoHorario PRIMARY KEY (id),
 *       CONSTRAINT FK_BlocoHorario_Horario FOREIGN KEY (nomeCurso, anoCurricular, anoLetivo)
 *           REFERENCES Horario(nomeCurso, anoCurricular, anoLetivo) ON DELETE CASCADE,
 *       CONSTRAINT FK_BlocoHorario_UC FOREIGN KEY (nomeUC)
 *           REFERENCES UnidadeCurricular(nome)
 *   );
 */
public class HorarioDAL_BD implements IHorarioDAL {

    private static final String SELECT_BLOCOS =
            "SELECT h.nomeCurso, h.anoCurricular, h.anoLetivo, " +
            "       b.diaSemana, b.horaInicio, b.duracao, b.nomeUC " +
            "FROM   Horario h " +
            "LEFT   JOIN BlocoHorario b " +
            "       ON  b.nomeCurso     = h.nomeCurso " +
            "       AND b.anoCurricular = h.anoCurricular " +
            "       AND b.anoLetivo     = h.anoLetivo ";

    private final ConexaoBD conexao;

    public HorarioDAL_BD() {
        this.conexao = new ConexaoBD();
    }

    @Override
    public Horario procurarOuCriar(String nomeCurso, int anoCurricular, int anoLetivo) {
        ArrayList<Object[]> rows = conexao.select(
                SELECT_BLOCOS +
                "WHERE h.nomeCurso = ? AND h.anoCurricular = ? AND h.anoLetivo = ? " +
                "ORDER BY b.diaSemana, b.horaInicio",
                rs -> new Object[]{
                        rs.getString("nomeCurso"),
                        rs.getInt("anoCurricular"),
                        rs.getInt("anoLetivo"),
                        rs.getString("diaSemana"),
                        rs.getString("horaInicio"),
                        rs.getObject("duracao"),
                        rs.getString("nomeUC")
                },
                nomeCurso, anoCurricular, anoLetivo
        );

        if (rows.isEmpty()) {
            conexao.execute(
                    "INSERT INTO Horario (nomeCurso, anoCurricular, anoLetivo) VALUES (?, ?, ?)",
                    nomeCurso, anoCurricular, anoLetivo
            );
            return new Horario(nomeCurso, anoCurricular, anoLetivo);
        }

        Horario horario = new Horario(nomeCurso, anoCurricular, anoLetivo);
        for (Object[] row : rows) {
            if (row[3] != null) {
                horario.adicionarBloco(new BlocoHorario(
                        (String) row[3],
                        (String) row[4],
                        (int) row[5],
                        (String) row[6]
                ));
            }
        }
        return horario;
    }

    @Override
    public List<Horario> listarTodos() {
        return carregarHorarios(SELECT_BLOCOS + "ORDER BY h.nomeCurso, h.anoCurricular, b.diaSemana, b.horaInicio");
    }

    @Override
    public List<Horario> listarPorCurso(String nomeCurso) {
        return carregarHorarios(
                SELECT_BLOCOS +
                "WHERE h.nomeCurso = ? " +
                "ORDER BY h.anoCurricular, b.diaSemana, b.horaInicio",
                nomeCurso
        );
    }

    @Override
    public void guardar(Horario horario) {
        String nomeCurso     = horario.getNomeCurso();
        int    anoCurricular = horario.getAnoCurricular();
        int    anoLetivo     = horario.getAnoLetivo();

        conexao.executarEmTransacao(() -> {
            conexao.execute(
                    "IF NOT EXISTS (" +
                    "   SELECT 1 FROM Horario " +
                    "   WHERE nomeCurso=? AND anoCurricular=? AND anoLetivo=?" +
                    ") INSERT INTO Horario (nomeCurso, anoCurricular, anoLetivo) VALUES (?,?,?)",
                    nomeCurso, anoCurricular, anoLetivo,
                    nomeCurso, anoCurricular, anoLetivo
            );
            conexao.execute(
                    "DELETE FROM BlocoHorario WHERE nomeCurso=? AND anoCurricular=? AND anoLetivo=?",
                    nomeCurso, anoCurricular, anoLetivo
            );
            for (BlocoHorario b : horario.getBlocos()) {
                conexao.execute(
                        "INSERT INTO BlocoHorario " +
                        "(nomeCurso, anoCurricular, anoLetivo, diaSemana, horaInicio, duracao, nomeUC) " +
                        "VALUES (?,?,?,?,?,?,?)",
                        nomeCurso, anoCurricular, anoLetivo,
                        b.getDiaSemana(), b.getHoraInicio(), b.getDuracao(), b.getNomeUC()
                );
            }
        });
    }

    // ── Auxiliar ──────────────────────────────────────────────────────────────

    private List<Horario> carregarHorarios(String sql, Object... params) {
        ArrayList<Object[]> rows = conexao.select(
                sql,
                rs -> new Object[]{
                        rs.getString("nomeCurso"),
                        rs.getInt("anoCurricular"),
                        rs.getInt("anoLetivo"),
                        rs.getString("diaSemana"),
                        rs.getString("horaInicio"),
                        rs.getObject("duracao"),
                        rs.getString("nomeUC")
                },
                params
        );

        LinkedHashMap<String, Horario> porChave = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String chave = row[0] + "|" + row[1] + "|" + row[2];
            porChave.computeIfAbsent(chave, k -> new Horario((String) row[0], (int) row[1], (int) row[2]));
            if (row[3] != null) {
                porChave.get(chave).adicionarBloco(new BlocoHorario(
                        (String) row[3],
                        (String) row[4],
                        (int) row[5],
                        (String) row[6]
                ));
            }
        }
        return new ArrayList<>(porChave.values());
    }
}
