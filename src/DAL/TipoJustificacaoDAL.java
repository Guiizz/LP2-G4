package DAL;

import Model.TipoJustificacao;
import Utils.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TipoJustificacaoDAL implements ITipoJustificacaoDAL {

    private static final String FICHEIRO_CSV = "csv/tipos_justificacao.csv";
    private static final String SEPARADOR = ";";
    private static final String CABECALHO = "nome;categoria";

    private List<TipoJustificacao> tipos;

    public TipoJustificacaoDAL() {
        this.tipos = new ArrayList<>();
        Utils.criarFicheiroSeNaoExistir(FICHEIRO_CSV, CABECALHO);
        carregarDoCSV();
    }

    public void adicionar(TipoJustificacao tipo) {
        tipos.add(tipo);
        guardarNoCSV();
    }

    public void remover(String nome) {
        tipos.removeIf(t -> t.getNome().equalsIgnoreCase(nome));
        guardarNoCSV();
    }

    public TipoJustificacao procurarPorNome(String nome) {
        for (TipoJustificacao t : tipos) {
            if (t.getNome().equalsIgnoreCase(nome)) return t;
        }
        return null;
    }

    public List<TipoJustificacao> listarTodos() {
        return new ArrayList<>(tipos);
    }

    private void carregarDoCSV() {
        tipos.clear();
        for (String[] campos : Utils.lerLinhasCSV(FICHEIRO_CSV, SEPARADOR)) {
            if (campos.length < 2) continue;
            try {
                tipos.add(new TipoJustificacao(campos[0], campos[1]));
            } catch (Exception e) {
                System.err.println("Erro ao carregar tipos de justificação do CSV: " + e.getMessage());
            }
        }
    }

    private void guardarNoCSV() {
        try (PrintWriter pw = Utils.abrirEscritorCSV(FICHEIRO_CSV, CABECALHO)) {
            for (TipoJustificacao t : tipos) {
                pw.println(t.getNome() + SEPARADOR + t.getCategoria());
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar tipos de justificação no CSV: " + e.getMessage());
        }
    }
}
