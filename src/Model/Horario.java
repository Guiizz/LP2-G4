package Model;

import java.util.ArrayList;
import java.util.List;

public class Horario {

    private String nomeCurso;
    private int anoCurricular;
    private int anoLetivo;
    private List<BlocoHorario> blocos;

    public Horario(String nomeCurso, int anoCurricular, int anoLetivo) {
        this.nomeCurso = nomeCurso;
        this.anoCurricular = anoCurricular;
        this.anoLetivo = anoLetivo;
        this.blocos = new ArrayList<>();
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public int getAnoCurricular() {
        return anoCurricular;
    }

    public int getAnoLetivo() {
        return anoLetivo;
    }

    public List<BlocoHorario> getBlocos() {
        return blocos;
    }

    public void adicionarBloco(BlocoHorario bloco) {
        blocos.add(bloco);
    }

    public void removerBloco(int indice) {
        if (indice >= 0 && indice < blocos.size()) blocos.remove(indice);
    }

    public List<BlocoHorario> getBlocosParaDia(String diaSemana) {
        List<BlocoHorario> resultado = new ArrayList<>();
        for (BlocoHorario b : blocos) {
            if (b.getDiaSemana().equalsIgnoreCase(diaSemana)) resultado.add(b);
        }
        return resultado;
    }

    public int getMinutosTotaisParaDia(String diaSemana) {
        int total = 0;
        for (BlocoHorario b : getBlocosParaDia(diaSemana)) total += b.getDuracao();
        return total;
    }

    public int getMinutosTotaisParaUC(String nomeUC) {
        int total = 0;
        for (BlocoHorario b : blocos) {
            if (b.getNomeUC().equalsIgnoreCase(nomeUC)) total += b.getDuracao();
        }
        return total;
    }
}
