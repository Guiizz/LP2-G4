package DAL.BD;

import DAL.CursoDAL;
import DAL.IInscricaoDAL;
import Model.Avaliacao;
import Model.Curso;
import Model.Estudante;
import Model.Inscricao;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Implementação da persistência de Inscricao em base de dados (SQL Server).
 *
 * Esquema esperado:
 *   CREATE TABLE Inscricao (
 *       numMecanografico VARCHAR(20)  NOT NULL,
 *       anoLetivo        INT          NOT NULL,
 *       anoDeCurso       INT          NOT NULL,
 *       nomeCurso        VARCHAR(100) NOT NULL,
 *       valorPago        FLOAT        NOT NULL DEFAULT 0,
 *       notas            VARCHAR(MAX) NULL,
 *       CONSTRAINT PK_Inscricao PRIMARY KEY (numMecanografico, anoLetivo)
 *   );
 *
 * Nota: as avaliações são guardadas como string serializada (mesmo formato do CSV)
 * enquanto a entidade Avaliacao não for migrada para tabela própria.
 */
public class InscricaoDAL_BD implements IInscricaoDAL {

    private final ConexaoBD conexao;

    public InscricaoDAL_BD() {
        this.conexao = new ConexaoBD();
    }

    @Override
    public void carregarInscricoes(ArrayList<Estudante> estudantes, CursoDAL cursoDAL) {
        for (Estudante estudante : estudantes) {
            ArrayList<Inscricao> inscricoes = conexao.select(
                    "SELECT anoLetivo, anoDeCurso, nomeCurso, valorPago, notas " +
                    "FROM Inscricao WHERE numMecanografico = ? ORDER BY anoLetivo",
                    rs -> {
                        int anoLetivo   = rs.getInt("anoLetivo");
                        int anoDeCurso  = rs.getInt("anoDeCurso");
                        String nomeCurso = rs.getString("nomeCurso");
                        double valorPago = rs.getDouble("valorPago");
                        String notas    = rs.getString("notas");

                        Curso curso = cursoDAL.procurarPorNome(nomeCurso);
                        if (curso == null) return null;

                        Inscricao inscricao = new Inscricao(anoLetivo, anoDeCurso, curso);
                        if (valorPago < 0) {
                            inscricao.setPropinaPaga(true);
                        } else if (valorPago > 0 && inscricao.getPropina() != null) {
                            try {
                                inscricao.getPropina().pagar(
                                        Math.min(valorPago, inscricao.getPropina().getSaldoEmDebito())
                                );
                            } catch (IllegalArgumentException ignored) {}
                        }
                        inscricao.setAvaliacoes(deserializarAvaliacoes(notas));
                        return inscricao;
                    },
                    estudante.getNumMecanografico()
            );
            for (Inscricao i : inscricoes) {
                if (i != null) estudante.adicionarInscricao(i);
            }
        }
    }

    @Override
    public void guardarInscricoes(ArrayList<Estudante> estudantes) {
        for (Estudante estudante : estudantes) {
            if (estudante.getInscricoes() == null) continue;
            for (Inscricao inscricao : estudante.getInscricoes()) {
                if (inscricao == null || inscricao.getCurso() == null) continue;
                upsertInscricao(estudante.getNumMecanografico(), inscricao);
            }
        }
    }

    @Override
    public void adicionarInscricao(String numMecanografico, Inscricao inscricao) {
        upsertInscricao(numMecanografico, inscricao);
    }

    @Override
    public void atualizarInscricao(String numMecanografico, Inscricao inscricao) {
        conexao.execute(
                "UPDATE Inscricao SET anoDeCurso = ?, nomeCurso = ?, valorPago = ?, notas = ? " +
                "WHERE numMecanografico = ? AND anoLetivo = ?",
                inscricao.getAnoDeCurso(),
                inscricao.getCurso().getNomeCurso(),
                inscricao.getPropina() != null ? inscricao.getPropina().getValorPago() : 0.0,
                serializarAvaliacoes(inscricao.getAvaliacoes()),
                numMecanografico,
                inscricao.getAnoLetivo()
        );
    }

    // -------------------------------------------------------------------------
    // Auxiliares
    // -------------------------------------------------------------------------

    private void upsertInscricao(String numMecanografico, Inscricao inscricao) {
        conexao.execute(
                "MERGE Inscricao AS alvo " +
                "USING (SELECT ? AS numMecanografico, ? AS anoLetivo) AS origem " +
                "   ON alvo.numMecanografico = origem.numMecanografico AND alvo.anoLetivo = origem.anoLetivo " +
                "WHEN MATCHED THEN " +
                "   UPDATE SET anoDeCurso = ?, nomeCurso = ?, valorPago = ?, notas = ? " +
                "WHEN NOT MATCHED THEN " +
                "   INSERT (numMecanografico, anoLetivo, anoDeCurso, nomeCurso, valorPago, notas) " +
                "   VALUES (?, ?, ?, ?, ?, ?);",
                // USING params
                numMecanografico, inscricao.getAnoLetivo(),
                // UPDATE params
                inscricao.getAnoDeCurso(),
                inscricao.getCurso().getNomeCurso(),
                inscricao.getPropina() != null ? inscricao.getPropina().getValorPago() : 0.0,
                serializarAvaliacoes(inscricao.getAvaliacoes()),
                // INSERT params
                numMecanografico, inscricao.getAnoLetivo(),
                inscricao.getAnoDeCurso(),
                inscricao.getCurso().getNomeCurso(),
                inscricao.getPropina() != null ? inscricao.getPropina().getValorPago() : 0.0,
                serializarAvaliacoes(inscricao.getAvaliacoes())
        );
    }

    private String serializarAvaliacoes(ArrayList<Avaliacao> avaliacoes) {
        if (avaliacoes == null || avaliacoes.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < avaliacoes.size(); i++) {
            Avaliacao av = avaliacoes.get(i);
            String nomeUC = "";
            if (av != null && av.getUc() != null && !av.getUc().isEmpty()) {
                nomeUC = av.getUc().get(0).getNome().replace(",", "-").replace(":", "-");
            }
            String nota = (av == null || !av.isLancada()) ? "P" : String.valueOf(av.getNota());
            sb.append(nomeUC).append(":").append(nota);
            if (i < avaliacoes.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    private ArrayList<Avaliacao> deserializarAvaliacoes(String notas) {
        ArrayList<Avaliacao> avaliacoes = new ArrayList<>();
        if (notas == null || notas.isBlank()) return avaliacoes;
        for (String par : notas.split(",")) {
            par = par.trim();
            String[] partes = par.split(":", 2);
            String notaStr = partes.length > 1 ? partes[1].trim() : par;
            if (notaStr.equalsIgnoreCase("P")) {
                avaliacoes.add(new Avaliacao(new ArrayList<>(), 100, new java.util.Date()));
            } else {
                try {
                    double nota = Double.parseDouble(notaStr);
                    avaliacoes.add(new Avaliacao(new ArrayList<>(), 100, new java.util.Date(), nota, nota >= 10));
                } catch (NumberFormatException ignored) {}
            }
        }
        return avaliacoes;
    }
}
