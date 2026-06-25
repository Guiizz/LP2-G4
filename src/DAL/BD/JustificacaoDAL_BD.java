package DAL.BD;

import DAL.IJustificacaoDAL;
import Model.JustificacaoFalta;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da persistência de JustificacaoFalta em base de dados (SQL Server).
 *
 * Esquema esperado:
 *   CREATE TABLE JustificacaoFalta (
 *       numMecanografico      VARCHAR(20)  NOT NULL,
 *       nomeUC                VARCHAR(100) NOT NULL,
 *       nomeCurso             VARCHAR(100) NOT NULL,
 *       anoLetivo             INT          NOT NULL,
 *       dataAula              DATE         NOT NULL,
 *       horaInicio            VARCHAR(5)   NOT NULL,
 *       nomeTipoJustificacao  VARCHAR(100) NOT NULL,
 *       estado                VARCHAR(20)  NOT NULL DEFAULT 'PENDENTE',
 *       dataPedido            DATE         NOT NULL,
 *       CONSTRAINT PK_JustificacaoFalta PRIMARY KEY (numMecanografico, nomeUC, nomeCurso, anoLetivo, dataAula, horaInicio),
 *       CONSTRAINT FK_JustFalta_Estudante FOREIGN KEY (numMecanografico)     REFERENCES Estudante(numMecanografico),
 *       CONSTRAINT FK_JustFalta_UC        FOREIGN KEY (nomeUC)               REFERENCES UnidadeCurricular(nome),
 *       CONSTRAINT FK_JustFalta_Curso     FOREIGN KEY (nomeCurso)            REFERENCES Curso(nomeCurso),
 *       CONSTRAINT FK_JustFalta_Tipo      FOREIGN KEY (nomeTipoJustificacao) REFERENCES TipoJustificacao(nome)
 *   );
 */
public class JustificacaoDAL_BD implements IJustificacaoDAL {

    private static final String SELECT_ALL =
            "SELECT numMecanografico, nomeUC, nomeCurso, anoLetivo, dataAula, " +
            "       horaInicio, nomeTipoJustificacao, estado, dataPedido " +
            "FROM JustificacaoFalta ";

    private static final RowMapper<JustificacaoFalta> MAPPER = rs -> new JustificacaoFalta(
            rs.getString("numMecanografico"),
            rs.getString("nomeUC"),
            rs.getString("nomeCurso"),
            rs.getInt("anoLetivo"),
            rs.getDate("dataAula").toLocalDate(),
            rs.getString("horaInicio"),
            rs.getString("nomeTipoJustificacao"),
            rs.getString("estado"),
            rs.getDate("dataPedido").toLocalDate()
    );

    private final ConexaoBD conexao;

    public JustificacaoDAL_BD() {
        this.conexao = new ConexaoBD();
    }

    @Override
    public void adicionar(JustificacaoFalta justificacao) {
        conexao.execute(
                "INSERT INTO JustificacaoFalta " +
                "(numMecanografico, nomeUC, nomeCurso, anoLetivo, dataAula, horaInicio, " +
                " nomeTipoJustificacao, estado, dataPedido) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                justificacao.getNumMecanografico(), justificacao.getNomeUC(),
                justificacao.getNomeCurso(), justificacao.getAnoLetivo(),
                Date.valueOf(justificacao.getDataAula()), justificacao.getHoraInicio(),
                justificacao.getNomeTipoJustificacao(), justificacao.getEstado(),
                Date.valueOf(justificacao.getDataPedido())
        );
    }

    @Override
    public void atualizar(JustificacaoFalta justificacao) {
        conexao.execute(
                "UPDATE JustificacaoFalta SET estado = ? " +
                "WHERE numMecanografico = ? AND nomeUC = ? AND nomeCurso = ? " +
                "  AND anoLetivo = ? AND dataAula = ? AND horaInicio = ?",
                justificacao.getEstado(),
                justificacao.getNumMecanografico(), justificacao.getNomeUC(),
                justificacao.getNomeCurso(), justificacao.getAnoLetivo(),
                Date.valueOf(justificacao.getDataAula()), justificacao.getHoraInicio()
        );
    }

    @Override
    public List<JustificacaoFalta> listarPorEstudante(String numMecanografico) {
        return conexao.select(
                SELECT_ALL + "WHERE numMecanografico = ? ORDER BY dataAula DESC, horaInicio",
                MAPPER, numMecanografico
        );
    }

    @Override
    public List<JustificacaoFalta> listarPendentes() {
        return conexao.select(
                SELECT_ALL + "WHERE estado = ? ORDER BY dataPedido",
                MAPPER, JustificacaoFalta.PENDENTE
        );
    }

    @Override
    public List<JustificacaoFalta> listarTodas() {
        return conexao.select(SELECT_ALL + "ORDER BY dataPedido DESC", MAPPER);
    }
}
