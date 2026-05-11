package Model;

import java.util.ArrayList;
import java.util.List;

public class RelatorioFechoAnoLetivo {
    private int estudantesAvancados;
    private int estudantesMantidos;
    private int estudantesConcluidos;
    private final List<String> mensagens;

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
}
