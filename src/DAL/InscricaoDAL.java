package DAL;

import Model.Avaliacao;
import Model.Curso;
import Model.Estudante;
import Model.Inscricao;
import Utils.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Implementação da persistência de Inscricao em ficheiro CSV,
 * extraída de EstudanteDAL para permitir a substituição por InscricaoDAL_BD.
 */
public class InscricaoDAL implements IInscricaoDAL {

    private static final String FICHEIRO_CSV = "csv/inscricoes.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "numMecanografico;anoLetivo;anoDeCurso;nomeCurso;valorPago;notas";

    public InscricaoDAL() {
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);
    }

    @Override
    public void carregarInscricoes(ArrayList<Estudante> estudantes, ICursoDAL cursoDAL) {
        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR)) {
            if (campos.length < 5) continue;
            try {
                String numMecanografico = campos[0];
                int anoLetivo = Integer.parseInt(campos[1]);
                int anoDeCurso = Integer.parseInt(campos[2]);
                String nomeCurso = campos[3];
                double valorPago;
                try {
                    valorPago = Double.parseDouble(campos[4]);
                } catch (NumberFormatException e) {
                    valorPago = Boolean.parseBoolean(campos[4]) ? -1 : 0;
                }
                String notas = campos.length >= 6 ? campos[5] : "";

                Estudante estudante = procurarEstudante(estudantes, numMecanografico);
                Curso curso = cursoDAL.procurarPorNome(nomeCurso);
                if (estudante == null || curso == null) continue;

                Inscricao inscricao = new Inscricao(anoLetivo, anoDeCurso, curso);
                if (valorPago == -1) {
                    inscricao.setPropinaPaga(true);
                } else if (valorPago > 0 && inscricao.getPropina() != null) {
                    try {
                        inscricao.getPropina().pagar(
                                Math.min(valorPago, inscricao.getPropina().getSaldoEmDebito())
                        );
                    } catch (IllegalArgumentException ignored) {}
                }
                inscricao.setAvaliacoes(deserializarAvaliacoes(notas));
                estudante.adicionarInscricao(inscricao);

            } catch (NumberFormatException e) {
                System.err.println("Erro ao carregar inscrições do CSV: " + e.getMessage());
            }
        }
    }

    @Override
    public void guardarInscricoes(ArrayList<Estudante> estudantes) {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO)) {
            for (Estudante estudante : estudantes) {
                if (estudante.getInscricoes() == null) continue;
                for (Inscricao inscricao : estudante.getInscricoes()) {
                    if (inscricao == null || inscricao.getCurso() == null) continue;
                    pw.println(
                            estudante.getNumMecanografico() + SEPARADOR +
                            inscricao.getAnoLetivo() + SEPARADOR +
                            inscricao.getAnoDeCurso() + SEPARADOR +
                            inscricao.getCurso().getNomeCurso() + SEPARADOR +
                            (inscricao.getPropina() != null ? inscricao.getPropina().getValorPago() : 0.0) + SEPARADOR +
                            serializarAvaliacoes(inscricao.getAvaliacoes())
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar inscrições no CSV: " + e.getMessage());
        }
    }

    @Override
    public void adicionarInscricao(String numMecanografico, Inscricao inscricao) {
        // Na versão CSV a persistência é feita em batch pelo guardarInscricoes;
        // este método é no-op para manter compatibilidade com a interface.
    }

    @Override
    public void atualizarInscricao(String numMecanografico, Inscricao inscricao) {
        // Idem — o guardarInscricoes já reflecte o estado actualizado.
    }

    // -------------------------------------------------------------------------
    // Auxiliares
    // -------------------------------------------------------------------------

    private Estudante procurarEstudante(ArrayList<Estudante> estudantes, String numMecanografico) {
        for (Estudante e : estudantes) {
            if (e.getNumMecanografico().equals(numMecanografico)) return e;
        }
        return null;
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
                avaliacoes.add(new Avaliacao(new ArrayList<>(), 100, new Date()));
            } else {
                try {
                    double nota = Double.parseDouble(notaStr);
                    avaliacoes.add(new Avaliacao(new ArrayList<>(), 100, new Date(), nota, nota >= 10));
                } catch (NumberFormatException ignored) {}
            }
        }
        return avaliacoes;
    }
}
