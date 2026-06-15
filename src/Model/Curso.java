package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um Curso disponível no ISSMF.
 */
public class Curso {

    private String nomeCurso;
    private Departamento departamento;
    private int duracao;
    private List <UnidadeCurricular> unidades;
    private String estado;
    private double valorPropina;

    /**
     * Construtor da classe Curso.
     * @param nomeCurso O nome do curso.
     * @param departamento O Departamento que o curso pertence.
     */
    public Curso(String nomeCurso, Departamento departamento) {
        this.nomeCurso = nomeCurso;
        this.departamento = departamento;
        this.duracao = 3;
        this.unidades = new ArrayList<>();
        this.estado = "PENDENTE";
        this.valorPropina = 0.0;
    }

    /**
     * Obtém o nome do curso.
     * @return O nome do curso.
     */
    public String getNomeCurso() {
        return nomeCurso;
    }

    /**
     * Define o nome do curso.
     * @param nomeCurso O novo nome do curso.
     */
    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    /**
     * Obtém o departamento responsável pelo curso.
     * @return O objeto Departamento.
     */
    public Departamento getDepartamento() {
        return departamento;
    }

    /**
     * Obtém a duração do curso em anos.
     * @return A duração (Sempre 3 anos).
     */
    public int getDuracao() {
        return duracao;
    }


    /**
     * Obtém a lista de todas as unidades curriculares deste curso.
     * @return Uma lista de Unidades Curriculares.
     */
    public List<UnidadeCurricular> getUnidades() {
        return unidades;
    }

    /**
     * Adiciona uma nova unidade curricular ao curso.
     * @param uc A Unidade Curricular adicionada.
     */
    public void adicionarUnidadeCurricular(UnidadeCurricular uc) {
        this.unidades.add(uc);
    }

    public void removerUnidadeCurricular(UnidadeCurricular uc) {
        this.unidades.remove(uc);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getValorPropina() {
        return valorPropina;
    }

    public void setValorPropina(double valorPropina) {
        this.valorPropina = valorPropina;
    }

    /**
     * Formato de texto da ficha Curso.
     * @return Uma String formatada com os detalhes do curso.
     */
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
                for (UnidadeCurricular uc : unidades) {
                    if (uc.getAnoCurricular() == ano) {
                        if (temUC) sb.append(", ");
                        sb.append(uc.getNome());
                        if (uc.isAtiva()) sb.append(" [A]");
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
        if (this == o) {
            return true;
        }
        if (!(o instanceof Curso)) {
            return false;
        }
        Curso that = (Curso) o;
        return nomeCurso != null && nomeCurso.equalsIgnoreCase(that.nomeCurso);
    }

    @Override
    public int hashCode() {
        return nomeCurso != null ? nomeCurso.toLowerCase().hashCode() : 0;
    }
}
