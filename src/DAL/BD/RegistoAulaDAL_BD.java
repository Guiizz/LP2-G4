package DAL.BD;

import DAL.IRegistoAulaDAL;
import Model.RegistoAula;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da persistência de RegistoAula em base de dados (SQL Server).
 *
 * Esquema esperado:
 *   CREATE TABLE RegistoAula (
 *       siglaDocente  VARCHAR(10)  NOT NULL,
 *       nomeUC        VARCHAR(100) NOT NULL,
 *       nomeCurso     VARCHAR(100) NOT NULL,
 *       anoLetivo     INT          NOT NULL,
 *       data          DATE         NOT NULL,
 *       horaInicio    VARCHAR(5)   NOT NULL,
 *       terminada     BIT          NOT NULL DEFAULT 0,
 *       CONSTRAINT PK_RegistoAula PRIMARY KEY (nomeUC, nomeCurso, anoLetivo, data, horaInicio),
 *       CONSTRAINT FK_RegistoAula_Docente FOREIGN KEY (siglaDocente) REFERENCES Docente(sigla),
 *       CONSTRAINT FK_RegistoAula_UC     FOREIGN KEY (nomeUC)        REFERENCES UnidadeCurricular(nome),
 *       CONSTRAINT FK_RegistoAula_Curso  FOREIGN KEY (nomeCurso)     REFERENCES Curso(nomeCurso)
 *   );
 */
public class RegistoAulaDAL_BD implements IRegistoAulaDAL {

    private static final String SELECT_ALL =
            "SELECT siglaDocente, nomeUC, nomeCurso, anoLetivo, data, horaInicio, terminada FROM RegistoAula ";

    private static final RowMapper<RegistoAula> MAPPER = rs -> new RegistoAula(
            rs.getString("siglaDocente"),
            rs.getString("nomeUC"),
            rs.getString("nomeCurso"),
            rs.getInt("anoLetivo"),
            rs.getDate("data").toLocalDate(),
            rs.getString("horaInicio"),
            rs.getBoolean("terminada")
    );

    private final ConexaoBD conexao;

    public RegistoAulaDAL_BD() {
        this.conexao = new ConexaoBD();
    }

    @Override
    public void adicionar(RegistoAula registo) {
        conexao.execute(
                "INSERT INTO RegistoAula (siglaDocente, nomeUC, nomeCurso, anoLetivo, data, horaInicio, terminada) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                registo.getSiglaDocente(), registo.getNomeUC(), registo.getNomeCurso(),
                registo.getAnoLetivo(), Date.valueOf(registo.getData()),
                registo.getHoraInicio(), registo.isTerminada()
        );
    }

    @Override
    public boolean existe(String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String horaInicio) {
        return procurar(nomeUC, nomeCurso, anoLetivo, data, horaInicio) != null;
    }

    @Override
    public RegistoAula procurar(String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String horaInicio) {
        ArrayList<RegistoAula> resultado = conexao.select(
                SELECT_ALL + "WHERE nomeUC = ? AND nomeCurso = ? AND anoLetivo = ? AND data = ? AND horaInicio = ?",
                MAPPER, nomeUC, nomeCurso, anoLetivo, Date.valueOf(data), horaInicio
        );
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    @Override
    public void guardar() {
        // No-op: em BD cada operação é persistida individualmente via guardarRegisto()
    }

    @Override
    public void guardarRegisto(RegistoAula registo) {
        conexao.execute(
                "UPDATE RegistoAula SET terminada = ? " +
                "WHERE nomeUC = ? AND nomeCurso = ? AND anoLetivo = ? AND data = ? AND horaInicio = ?",
                registo.isTerminada(),
                registo.getNomeUC(), registo.getNomeCurso(), registo.getAnoLetivo(),
                Date.valueOf(registo.getData()), registo.getHoraInicio()
        );
    }

    @Override
    public List<RegistoAula> listarPorDocente(String siglaDocente) {
        return conexao.select(
                SELECT_ALL + "WHERE siglaDocente = ? ORDER BY data DESC, horaInicio",
                MAPPER, siglaDocente
        );
    }

    @Override
    public List<RegistoAula> listarPorUCeCurso(String nomeUC, String nomeCurso, int anoLetivo) {
        return conexao.select(
                SELECT_ALL + "WHERE nomeUC = ? AND nomeCurso = ? AND anoLetivo = ? ORDER BY data, horaInicio",
                MAPPER, nomeUC, nomeCurso, anoLetivo
        );
    }

    @Override
    public List<RegistoAula> listarTodas() {
        return conexao.select(SELECT_ALL + "ORDER BY data DESC, horaInicio", MAPPER);
    }
}
