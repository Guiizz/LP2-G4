package Model;

import java.util.ArrayList;

public class Inscricao {
    private int anoLetivo;
    private int anoDeCurso;
    private Curso curso;
    private ArrayList<Avaliacao> avaliacoes;
    private boolean propinaPaga;

    public Inscricao(int anoLetivo, int anoDeCurso, Curso curso) {
        this.anoLetivo = anoLetivo;
        this.anoDeCurso = anoDeCurso;
        this.curso = curso;
        this.avaliacoes = new ArrayList<>();
        this.propinaPaga = false;
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

    public boolean isPropinaPaga() {
        return propinaPaga;
    }

    public void setPropinaPaga(boolean propinaPaga) {
        this.propinaPaga = propinaPaga;
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

    @Override
    public String toString() {
        return "=== Inscricao ===\n" +
                "Ano Letivo: " + anoLetivo + "/" + (anoLetivo + 1) + "\n" +
                "Ano de Curso: " + anoDeCurso + "\n" +
                "Curso: " + (curso != null ? curso.getNomeCurso() : "Sem curso") + "\n" +
                "Propina paga: " + (propinaPaga ? "Sim" : "Não") + "\n" +
                "Avaliações lançadas: " + getTotalAvaliacoesLancadas() + "/" + (avaliacoes == null ? 0 : avaliacoes.size()) + "\n" +
                "================";
    }
}