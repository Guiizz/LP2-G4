package DAL;

import Model.AnoLetivo;
import Model.RelatorioFechoAnoLetivo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class HistoricoAnoLetivoDAL {

    private static final String FICHEIRO_CSV = "csv/historico_fecho_anos_letivos.csv";
    private static final String SEPARADOR = ";";

    public HistoricoAnoLetivoDAL() {
        criarFicheiroCsvSeNaoExistir();
    }

    public String exportarFecho(AnoLetivo anoLetivo, RelatorioFechoAnoLetivo relatorio) {
        if (anoLetivo == null) {
            throw new IllegalArgumentException("Ano letivo inválido para exportação.");
        }

        if (relatorio == null) {
            throw new IllegalArgumentException("Relatório inválido para exportação.");
        }

        criarFicheiroCsvSeNaoExistir();

        try (PrintWriter pw = new PrintWriter(new FileWriter(FICHEIRO_CSV, true))) {
            pw.println(
                    limparCampo(anoLetivo.getDesignacao()) + SEPARADOR +
                            limparCampo(String.valueOf(anoLetivo.getDataFecho())) + SEPARADOR +
                            limparCampo(anoLetivo.getEstado()) + SEPARADOR +
                            relatorio.getEstudantesAvancados() + SEPARADOR +
                            relatorio.getEstudantesMantidos() + SEPARADOR +
                            relatorio.getEstudantesConcluidos() + SEPARADOR +
                            limparCampo(juntarMensagens(relatorio))
            );

            return FICHEIRO_CSV;

        } catch (IOException e) {
            throw new IllegalArgumentException("Erro ao exportar histórico do ano letivo: " + e.getMessage());
        }
    }

    private void criarFicheiroCsvSeNaoExistir() {
        File ficheiro = new File(FICHEIRO_CSV);

        if (ficheiro.getParentFile() != null) {
            ficheiro.getParentFile().mkdirs();
        }

        if (!ficheiro.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ficheiro))) {
                pw.println("anoLetivo;dataFecho;estadoFinal;estudantesAvancados;estudantesMantidos;estudantesConcluidos;detalhes");
            } catch (IOException e) {
                System.err.println("Erro ao criar ficheiro CSV de histórico do ano letivo: " + e.getMessage());
            }
        }
    }

    private String juntarMensagens(RelatorioFechoAnoLetivo relatorio) {
        if (relatorio.getMensagens() == null || relatorio.getMensagens().isEmpty()) {
            return "";
        }

        return String.join(" | ", relatorio.getMensagens());
    }

    private String limparCampo(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace(SEPARADOR, ",")
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();
    }
}