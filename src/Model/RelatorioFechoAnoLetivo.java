package Model;

import java.util.ArrayList;
import java.util.List;

public class RelatorioFechoAnoLetivo {
    private int estudantesAvancados;
    private int estudantesMantidos;
    private int estudantesConcluidos;
    private final List<String> mensagens;
    private String caminhoFicheiroHistorico;

    private final java.util.Map<String, java.util.List<String>> ucsEmAtrasoPorEstudante = new java.util.HashMap<>();

    public RelatorioFechoAnoLetivo() {
        this.mensagens = new ArrayList<>();
    }

    public void incrementarAvancados() {
        estudantesAvancados++;
    }

    public void incrementarMantidos() {
        estudantesMantidos++;
    }

    public void incrementarConcluidos() {
        estudantesConcluidos++;
    }

    public void adicionarMensagem(String mensagem) {
        mensagens.add(mensagem);
    }

    public int getEstudantesAvancados() {
        return estudantesAvancados;
    }

    public int getEstudantesMantidos() {
        return estudantesMantidos;
    }

    public int getEstudantesConcluidos() {
        return estudantesConcluidos;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public String getCaminhoFicheiroHistorico() {
        return caminhoFicheiroHistorico;
    }

    public void setCaminhoFicheiroHistorico(String caminhoFicheiroHistorico) {
        this.caminhoFicheiroHistorico = caminhoFicheiroHistorico;
    }

    public void registarUcsEmAtraso(String nomeEstudante, java.util.List<String> ucs) {
        if (ucs != null && !ucs.isEmpty()) {
            ucsEmAtrasoPorEstudante.put(nomeEstudante, ucs);
        }
    }

    public java.util.Map<String, java.util.List<String>> getUcsEmAtrasoPorEstudante() {
        return ucsEmAtrasoPorEstudante;
    }
}
