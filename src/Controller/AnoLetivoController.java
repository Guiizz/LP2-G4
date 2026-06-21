package Controller;

import BLL.AnoLetivoBLL;
import Model.AnoLetivo;
import Model.Estudante;
import Model.RelatorioFechoAnoLetivo;

import java.time.LocalDate;
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

    public AnoLetivo abrirAnoLetivo(int ano, LocalDate dataAbertura) {
        return anoLetivoBLL.abrirAnoLetivo(ano, dataAbertura);
    }

    /** Path BD: usa leitura SQL estruturada (JOIN + UPDATE/INSERT via SQL). */
    public RelatorioFechoAnoLetivo fecharAnoAtual(LocalDate dataFecho) {
        return anoLetivoBLL.fecharAnoAtual(dataFecho);
    }

    /** Path CSV: usa objetos Estudante carregados em memória. */
    public RelatorioFechoAnoLetivo fecharAnoAtual(List<Estudante> estudantes, LocalDate dataFecho) {
        return anoLetivoBLL.fecharAnoAtual(estudantes, dataFecho);
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