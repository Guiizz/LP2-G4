package DAL;

import Model.AnoLetivo;
import Model.RelatorioFechoAnoLetivo;

import java.io.*;
import Utils.Utils;

public class HistoricoAnoLetivoDAL {

    private static final String FICHEIRO_CSV = "csv/historico_fecho_anos_letivos.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "anoLetivo;dataFecho;estadoFinal;estudantesAvancados;estudantesMantidos;estudantesConcluidos;detalhes";

    public HistoricoAnoLetivoDAL() {
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);
    }

    /**
     * Lê o histórico de fechos de anos letivos guardado no CSV.
     * @return lista de registos (cada um é um array com as colunas do cabeçalho)
     */
    public java.util.List<String[]> listarHistorico() {
        return Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR);
    }

    public String exportarFecho(AnoLetivo anoLetivo, RelatorioFechoAnoLetivo relatorio) {
        if (anoLetivo == null) {
            throw new IllegalArgumentException("Ano letivo inválido para exportação.");
        }
        if (relatorio == null) {
            throw new IllegalArgumentException("Relatório inválido para exportação.");
        }

        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);

        try (PrintWriter pw = new PrintWriter(new FileWriter(FICHEIRO_CSV, true))) {
            pw.println(
                    limparCampo(anoLetivo.getDesignacao())          + SEPARADOR +
                            limparCampo(String.valueOf(anoLetivo.getDataFecho())) + SEPARADOR +
                            limparCampo(anoLetivo.getEstado())              + SEPARADOR +
                            relatorio.getEstudantesAvancados()              + SEPARADOR +
                            relatorio.getEstudantesMantidos()               + SEPARADOR +
                            relatorio.getEstudantesConcluidos()             + SEPARADOR +
                            limparCampo(juntarMensagens(relatorio))
            );
            return FICHEIRO_CSV;
        } catch (IOException e) {
            throw new IllegalArgumentException("Erro ao exportar histórico do ano letivo: " + e.getMessage());
        }
    }

    private String juntarMensagens(RelatorioFechoAnoLetivo relatorio) {
        if (relatorio.getMensagens() == null || relatorio.getMensagens().isEmpty()) {
            return "";
        }
        return String.join(" | ", relatorio.getMensagens());
    }

    private String limparCampo(String valor) {
        if (valor == null) return "";
        return valor
                .replace(SEPARADOR, ",")
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();
    }
}