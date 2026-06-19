package DAL.BD;

import DAL.ICursoDAL;
import DAL.IInscricaoDAL;
import DAL.IPropinaDAL;
import Model.Avaliacao;
import Model.Curso;
import Model.Estudante;
import Model.Inscricao;
import Model.Propina;

import java.util.ArrayList;

/**
 * Implementação da persistência de Inscricao em base de dados (SQL Server).
 * Usa IPropinaDAL para carregar/guardar a propina completa (com histórico de pagamentos).
 *
 * Esquema esperado:
 *   CREATE TABLE Inscricao (
 *       numMecanografico VARCHAR(20)  NOT NULL,
 *       anoLetivo        INT          NOT NULL,
 *       anoDeCurso       INT          NOT NULL,
 *       nomeCurso        VARCHAR(100) NOT NULL,
 *       notas            VARCHAR(MAX) NULL,
 *       CONSTRAINT PK_Inscricao PRIMARY KEY (numMecanografico, anoLetivo)
 *   );
 *
 * Nota: as avaliações são guardadas como string serializada enquanto a entidade
 * Avaliacao não for completamente migrada para tabela própria.
 */
public class InscricaoDAL_BD implements IInscricaoDAL {

    private final ConexaoBD conexao;
    private final IPropinaDAL propinaDAL;

    public InscricaoDAL_BD(IPropinaDAL propinaDAL) {
        this.conexao    = new ConexaoBD();
        this.propinaDAL = propinaDAL;
    }

    @Override
    public void carregarInscricoes(ArrayList<Estudante> estudantes, ICursoDAL cursoDAL) {
        if (estudantes.isEmpty()) return;

        // Pré-carregar cursos e propinas/pagamentos uma única vez (em vez de
        // 1 query de curso + 1 de propina + 1 de pagamentos por CADA inscrição).
        java.util.Map<String, Curso> cursosPorNome = new java.util.HashMap<>();
        for (Curso c : cursoDAL.listarCursos()) cursosPorNome.put(c.getNomeCurso(), c);

        java.util.Map<String, Propina> propinasPorChave = propinaDAL.listarTodas();

        // Uma única query para as inscrições de TODOS os estudantes.
        ArrayList<Object[]> linhas = conexao.select(
                "SELECT numMecanografico, anoLetivo, anoDeCurso, nomeCurso, notas " +
                "FROM Inscricao ORDER BY numMecanografico, anoLetivo",
                rs -> new Object[]{
                        rs.getString("numMecanografico"),
                        rs.getInt("anoLetivo"),
                        rs.getInt("anoDeCurso"),
                        rs.getString("nomeCurso"),
                        rs.getString("notas")
                }
        );

        java.util.Map<String, ArrayList<Inscricao>> inscricoesPorEstudante = new java.util.HashMap<>();
        for (Object[] linha : linhas) {
            String numMec    = (String) linha[0];
            int anoLetivo     = (int) linha[1];
            int anoDeCurso    = (int) linha[2];
            String nomeCurso  = (String) linha[3];
            String notas      = (String) linha[4];

            Curso curso = cursosPorNome.get(nomeCurso);
            if (curso == null) continue;

            Inscricao inscricao = new Inscricao(anoLetivo, anoDeCurso, curso);
            Propina propina = propinasPorChave.get(numMec + "|" + anoLetivo);
            if (propina != null) inscricao.setPropina(propina);
            inscricao.setAvaliacoes(deserializarAvaliacoes(notas));

            inscricoesPorEstudante.computeIfAbsent(numMec, k -> new ArrayList<>()).add(inscricao);
        }

        for (Estudante estudante : estudantes) {
            ArrayList<Inscricao> inscricoes = inscricoesPorEstudante.get(estudante.getNumMecanografico());
            if (inscricoes != null) {
                for (Inscricao i : inscricoes) estudante.adicionarInscricao(i);
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
                "UPDATE Inscricao SET anoDeCurso = ?, nomeCurso = ?, notas = ? " +
                "WHERE numMecanografico = ? AND anoLetivo = ?",
                inscricao.getAnoDeCurso(),
                inscricao.getCurso().getNomeCurso(),
                serializarAvaliacoes(inscricao.getAvaliacoes()),
                numMecanografico,
                inscricao.getAnoLetivo()
        );
        if (inscricao.getPropina() != null) {
            propinaDAL.guardarPropina(numMecanografico, inscricao.getAnoLetivo(), inscricao.getPropina());
        }
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
                "   UPDATE SET anoDeCurso = ?, nomeCurso = ?, notas = ? " +
                "WHEN NOT MATCHED THEN " +
                "   INSERT (numMecanografico, anoLetivo, anoDeCurso, nomeCurso, notas) " +
                "   VALUES (?, ?, ?, ?, ?);",
                numMecanografico, inscricao.getAnoLetivo(),
                inscricao.getAnoDeCurso(), inscricao.getCurso().getNomeCurso(),
                serializarAvaliacoes(inscricao.getAvaliacoes()),
                numMecanografico, inscricao.getAnoLetivo(),
                inscricao.getAnoDeCurso(), inscricao.getCurso().getNomeCurso(),
                serializarAvaliacoes(inscricao.getAvaliacoes())
        );
        if (inscricao.getPropina() != null) {
            propinaDAL.guardarPropina(numMecanografico, inscricao.getAnoLetivo(), inscricao.getPropina());
        }
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
