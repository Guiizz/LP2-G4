package Model;

import java.util.ArrayList;

public class Inscricao {
    private int anoLetivo;
    private int anoDeCurso;
    private Curso curso;
    private ArrayList<Avaliacao> avaliacoes;
    private Propina propina;

    public Inscricao(int anoLetivo, int anoDeCurso, Curso curso) {
        this.anoLetivo = anoLetivo;
        this.anoDeCurso = anoDeCurso;
        this.curso = curso;
        this.avaliacoes = new ArrayList<>();
        double valor = (curso != null) ? curso.getValorPropina() : 0.0;
        this.propina = new Propina(valor);
    }

    public int getAnoLetivo() {
        return anoLetivo;
    }

    public int getAnoDeCurso() {
        return anoDeCurso;
    }

    public Curso getCurso() {
        return curso;
    }

    public ArrayList<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(ArrayList<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes != null ? avaliacoes : new ArrayList<>();
    }

    public void adicionarAvaliacao(Avaliacao avaliacao){
        this.avaliacoes.add(avaliacao);
    }

    public Propina getPropina() { return propina; }

    public void setPropina(Propina propina) { this.propina = propina; }

    public boolean isPropinaPaga() {
        return propina != null && propina.isTotalmentePaga();
    }

    public void setPropinaPaga(boolean paga) {
        if (paga && propina != null && !propina.isTotalmentePaga()) {
            try { propina.pagar(propina.getSaldoEmDebito()); }
            catch (IllegalArgumentException ignored) {}
        }
    }

    public int getTotalAvaliacoesLancadas() {
        int total = 0;
        for (Avaliacao avaliacao : avaliacoes) {
            if (avaliacao != null && avaliacao.isLancada()) {
                total++;
            }
        }
        return total;
    }

    public int getTotalAvaliacoesAprovadas() {
        int total = 0;
        for (Avaliacao avaliacao : avaliacoes) {
            if (avaliacao != null && avaliacao.isLancada() && avaliacao.getNota() >= 10) {
                total++;
            }
        }
        return total;
    }

    public boolean temNotasPorLancar() {
        if (avaliacoes == null || avaliacoes.isEmpty()) {
            return true;
        }

        for (Avaliacao avaliacao : avaliacoes) {
            if (avaliacao == null || !avaliacao.isLancada()) {
                return true;
            }
        }
        return false;
    }

    public double calcularAproveitamento() {
        if (avaliacoes == null || avaliacoes.isEmpty()) {
            return 0;
        }
        return (double) getTotalAvaliacoesAprovadas() / avaliacoes.size();
    }

    /**
     * Devolve as avaliações lançadas com nota inferior a 10 (reprovadas).
     * Usado para identificar UCs em atraso de anos anteriores.
     */
    public ArrayList<Avaliacao> getAvaliacoesReprovadas() {
        ArrayList<Avaliacao> reprovadas = new ArrayList<>();
        if (avaliacoes == null) return reprovadas;
        for (Avaliacao avaliacao : avaliacoes) {
            if (avaliacao != null && avaliacao.isLancada() && avaliacao.getNota() < 10) {
                reprovadas.add(avaliacao);
            }
        }
        return reprovadas;
    }

    /**
     * Devolve o número de avaliações reprovadas nesta inscrição.
     */
    public int getTotalReprovadas() {
        return getAvaliacoesReprovadas().size();
    }

    @Override
    public String toString() {
        return "=== Inscricao ===\n" +
                "Ano Letivo: " + anoLetivo + "/" + (anoLetivo + 1) + "\n" +
                "Ano de Curso: " + anoDeCurso + "\n" +
                "Curso: " + (curso != null ? curso.getNomeCurso() : "Sem curso") + "\n" +
                (propina != null ? propina.toString() : "Sem propina") + "\n" +
                "Avaliações lançadas: " + getTotalAvaliacoesLancadas() + "/" + (avaliacoes == null ? 0 : avaliacoes.size()) + "\n" +
                "================";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Inscricao)) {
            return false;
        }
        Inscricao that = (Inscricao) o;
        return this.anoLetivo == that.anoLetivo
                && this.anoDeCurso == that.anoDeCurso
                && (this.curso != null ? this.curso.equals(that.curso) : that.curso == null);
    }

    @Override
    public int hashCode() {
        int result = anoLetivo;
        result = 31 * result + anoDeCurso;
        result = 31 * result + (curso != null ? curso.hashCode() : 0);
        return result;
    }
}