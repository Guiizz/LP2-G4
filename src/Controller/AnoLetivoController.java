package Controller;

import BLL.AnoLetivoBLL;
import Model.AnoLetivo;
import Model.Estudante;
import Model.RelatorioFechoAnoLetivo;

import java.util.ArrayList;
import java.util.List;

public class AnoLetivoController {

    private final AnoLetivoBLL anoLetivoBLL;

    public AnoLetivoController(AnoLetivoBLL anoLetivoBLL) {
        this.anoLetivoBLL = anoLetivoBLL;
    }

    public AnoLetivo consultarAnoAtual() {
        return anoLetivoBLL.consultarAnoAtual();
    }

    public AnoLetivo consultarMaisRecente() {
        return anoLetivoBLL.consultarMaisRecente();
    }

    public AnoLetivo abrirAnoLetivo(int ano) {
        return anoLetivoBLL.abrirAnoLetivo(ano);
    }

    /** Path BD: usa leitura SQL estruturada (JOIN + UPDATE/INSERT via SQL). */
    public RelatorioFechoAnoLetivo fecharAnoAtual() {
        return anoLetivoBLL.fecharAnoAtual();
    }

    /** Path CSV: usa objetos Estudante carregados em memória. */
    public RelatorioFechoAnoLetivo fecharAnoAtual(List<Estudante> estudantes) {
        return anoLetivoBLL.fecharAnoAtual(estudantes);
    }

    public ArrayList<AnoLetivo> listarTodos() {
        return anoLetivoBLL.listarTodos();
    }

    public void removerAnoLetivo(int ano) {
        anoLetivoBLL.removerAnoLetivo(ano);
    }

    public List<String[]> listarHistorico() {
        return anoLetivoBLL.listarHistorico();
    }
}