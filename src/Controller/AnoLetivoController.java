package Controller;

import BLL.AnoLetivoBLL;
import DAL.AnoLetivoDAL;
import Model.AnoLetivo;
import Model.Estudante;
import Model.RelatorioFechoAnoLetivo;

import java.util.List;

public class AnoLetivoController {
    private final AnoLetivoBLL anoLetivoBLL;

    public AnoLetivoController() {
        this.anoLetivoBLL = new AnoLetivoBLL(new AnoLetivoDAL());
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

    public RelatorioFechoAnoLetivo fecharAnoAtual(List<Estudante> estudantes) {
        return anoLetivoBLL.fecharAnoAtual(estudantes);
    }
}