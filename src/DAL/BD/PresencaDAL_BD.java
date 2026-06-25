package DAL.BD;

import DAL.IPresencaDAL;
import Model.Presenca;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da persistência de Presenca em base de dados (SQL Server).
 *
 * Esquema esperado:
 *   CREATE TABLE Presenca (
 *       numMecanografico  VARCHAR(20)  NOT NULL,
 *       nomeUC            VARCHAR(100) NOT NULL,
 *       nomeCurso         VARCHAR(100) NOT NULL,
 *       anoLetivo         INT          NOT NULL,
 *       data              DATE         NOT NULL,
 *       horaInicio        VARCHAR(5)   NOT NULL,
 *       presente          BIT          NOT NULL DEFAULT 1,
 *       CONSTRAINT PK_Presenca PRIMARY KEY (numMecanografico, nomeUC, nomeCurso, anoLetivo, data, horaInicio),
 *       CONSTRAINT FK_Presenca_Estudante FOREIGN KEY (numMecanografico) REFERENCES Estudante(numMecanografico),
 *       CONSTRAINT FK_Presenca_UC        FOREIGN KEY (nomeUC)           REFERENCES UnidadeCurricular(nome),
 *       CONSTRAINT FK_Presenca_Curso     FOREIGN KEY (nomeCurso)        REFERENCES Curso(nomeCurso)
 *   );
 */
public class PresencaDAL_BD implements IPresencaDAL {

    private static final String SELECT_ALL =
            "SELECT numMecanografico, nomeUC, nomeCurso, anoLetivo, data, horaInicio, presente FROM Presenca ";

    private static final RowMapper<Presenca> MAPPER = rs -> new Presenca(
            rs.getString("numMecanografico"),
            rs.getString("nomeUC"),
            rs.getString("nomeCurso"),
            rs.getInt("anoLetivo"),
            rs.getDate("data").toLocalDate(),
            rs.getString("horaInicio"),
            rs.getBoolean("presente")
    );

    private final ConexaoBD conexao;

    public PresencaDAL_BD() {
        this.conexao = new ConexaoBD();
    }

    @Override
    public void adicionar(Presenca presenca) {
        conexao.execute(
                "INSERT INTO Presenca (numMecanografico, nomeUC, nomeCurso, anoLetivo, data, horaInicio, presente) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                presenca.getNumMecanografico(), presenca.getNomeUC(), presenca.getNomeCurso(),
                presenca.getAnoLetivo(), Date.valueOf(presenca.getData()),
                presenca.getHoraInicio(), presenca.isPresente()
        );
    }

    @Override
    public boolean existe(String numMecanografico, String nomeUC, String nomeCurso,
                          int anoLetivo, LocalDate data, String horaInicio) {
        ArrayList<Presenca> resultado = conexao.select(
                SELECT_ALL + "WHERE numMecanografico = ? AND nomeUC = ? AND nomeCurso = ? " +
                             "  AND anoLetivo = ? AND data = ? AND horaInicio = ?",
                MAPPER, numMecanografico, nomeUC, nomeCurso, anoLetivo, Date.valueOf(data), horaInicio
        );
        return !resultado.isEmpty();
    }

    @Override
    public List<Presenca> listarPorEstudante(String numMecanografico) {
        return conexao.select(
                SELECT_ALL + "WHERE numMecanografico = ? ORDER BY data DESC, horaInicio",
                MAPPER, numMecanografico
        );
    }

    @Override
    public List<Presenca> listarPorUCeCurso(String nomeUC, String nomeCurso, int anoLetivo) {
        return conexao.select(
                SELECT_ALL + "WHERE nomeUC = ? AND nomeCurso = ? AND anoLetivo = ? ORDER BY data, horaInicio",
                MAPPER, nomeUC, nomeCurso, anoLetivo
        );
    }

    @Override
    public List<Presenca> listarTodas() {
        return conexao.select(SELECT_ALL + "ORDER BY data DESC, horaInicio", MAPPER);
    }
}
