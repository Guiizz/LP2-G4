package Model;

import java.util.ArrayList;
import java.util.List;

public class Curso {

    private String nomeCurso;
    private Departamento departamento;
    private int duracao;
    private List<UCNoCurso> unidades;
    private String estado;
    private double valorPropina;

    public Curso(String nomeCurso, Departamento departamento) {
        this.nomeCurso = nomeCurso;
        this.departamento = departamento;
        this.duracao = 3;
        this.unidades = new ArrayList<>();
        this.estado = "PENDENTE";
        this.valorPropina = 0.0;
    }

    public String getNomeCurso() { return nomeCurso; }
    public void setNomeCurso(String nomeCurso) { this.nomeCurso = nomeCurso; }

    public Departamento getDepartamento() { return departamento; }

    public int getDuracao() { return duracao; }

    public List<UCNoCurso> getUCsNoCurso() { return unidades; }

    public List<UnidadeCurricular> getUnidades() {
        List<UnidadeCurricular> lista = new ArrayList<>();
        for (UCNoCurso u : unidades) lista.add(u.getUc());
        return lista;
    }

    public int getAnoCurricularDe(UnidadeCurricular uc) {
        for (UCNoCurso u : unidades) {
            if (u.getUc().equals(uc)) return u.getAnoCurricular();
        }
        return -1;
    }

    public void adicionarUnidadeCurricular(UnidadeCurricular uc, int anoCurricular) {
        this.unidades.add(new UCNoCurso(uc, anoCurricular));
    }

    public void removerUnidadeCurricular(UnidadeCurricular uc) {
        unidades.removeIf(u -> u.getUc().equals(uc));
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public double getValorPropina() { return valorPropina; }
    public void setValorPropina(double valorPropina) { this.valorPropina = valorPropina; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Curso ===\n");
        sb.append("Nome       : ").append(nomeCurso).append("\n");
        sb.append("Departamento: ").append(departamento != null ? departamento.getNome() : "Sem departamento").append("\n");
        sb.append("Duração    : ").append(duracao).append(" anos\n");
        sb.append("Propina    : ").append(String.format("%.2f €", valorPropina)).append("\n");
        sb.append("Estado     : ").append(estado != null ? estado : "PENDENTE").append("\n");
        sb.append("UCs (").append(unidades.size()).append("):\n");
        if (unidades.isEmpty()) {
            sb.append("  (sem UCs associadas)\n");
        } else {
            for (int ano = 1; ano <= duracao; ano++) {
                sb.append("  Ano ").append(ano).append(": ");
                boolean temUC = false;
                for (UCNoCurso u : unidades) {
                    if (u.getAnoCurricular() == ano) {
                        if (temUC) sb.append(", ");
                        sb.append(u.getUc().getNome());
                        if (u.getUc().isAtiva()) sb.append(" [A]");
                        temUC = true;
                    }
                }
                if (!temUC) sb.append("(sem UCs)");
                sb.append("\n");
            }
        }
        sb.append("=============");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Curso)) return false;
        Curso that = (Curso) o;
        return nomeCurso != null && nomeCurso.equalsIgnoreCase(that.nomeCurso);
    }

    @Override
    public int hashCode() {
        return nomeCurso != null ? nomeCurso.toLowerCase().hashCode() : 0;
    }
}
