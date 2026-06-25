package Model;

import java.util.ArrayList;
import java.util.List;

public class UnidadeCurricular {

    private String nome;
    private int ects;
    private List<Avaliacao> avaliacoes;
    private String docenteResponsavel;
    private List<MomentoAvaliacao> momentosAvaliacao;
    private boolean ativa;

    public UnidadeCurricular(String nome, int ects) {
        this.nome = nome;
        this.ects = ects;
        this.avaliacoes = new ArrayList<>();
        this.docenteResponsavel = null;
        this.momentosAvaliacao = new ArrayList<>();
        this.ativa = false;
    }

    public UnidadeCurricular(String nome, int ects, List<Avaliacao> avaliacoes, String docenteResponsavel) {
        this.nome = nome;
        this.ects = ects;
        this.avaliacoes = avaliacoes != null ? avaliacoes : new ArrayList<>();
        this.docenteResponsavel = docenteResponsavel;
        this.momentosAvaliacao = new ArrayList<>();
        this.ativa = false;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getEts() { return ects; }
    public void setEts(int ects) { this.ects = ects; }

    public List<Avaliacao> getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(List<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes != null ? avaliacoes : new ArrayList<>();
    }

    public String getDocenteResponsavel() { return docenteResponsavel; }
    public void setDocenteResponsavel(String docenteResponsavel) {
        this.docenteResponsavel = docenteResponsavel;
    }

    public boolean temDocenteResponsavel() {
        return this.docenteResponsavel != null && !this.docenteResponsavel.isEmpty();
    }

    public List<MomentoAvaliacao> getMomentosAvaliacao() { return momentosAvaliacao; }
    public void setMomentosAvaliacao(List<MomentoAvaliacao> momentosAvaliacao) {
        this.momentosAvaliacao = momentosAvaliacao;
    }

    public void adicionarMomento(MomentoAvaliacao momento) {
        this.momentosAvaliacao.add(momento);
    }

    public List<MomentoAvaliacao> getMomentosParaAno(int anoLetivo) {
        List<MomentoAvaliacao> especificos = new ArrayList<>();
        List<MomentoAvaliacao> legados     = new ArrayList<>();
        for (MomentoAvaliacao m : momentosAvaliacao) {
            if (m.getAnoLetivo() == anoLetivo) especificos.add(m);
            else if (m.getAnoLetivo() == 0) legados.add(m);
        }
        return especificos.isEmpty() ? legados : especificos;
    }

    public List<MomentoAvaliacao> getMomentosParaAno(int anoLetivo, String nomeCurso) {
        List<MomentoAvaliacao> resultado = new ArrayList<>();
        for (MomentoAvaliacao m : getMomentosParaAno(anoLetivo)) {
            if (m.pertenceAoCurso(nomeCurso)) resultado.add(m);
        }
        return resultado;
    }

    public List<String> cursosComMomentosParaAno(int anoLetivo) {
        List<String> cursos = new ArrayList<>();
        for (MomentoAvaliacao m : getMomentosParaAno(anoLetivo)) {
            String c = m.getNomeCurso();
            boolean existe = false;
            for (String s : cursos) {
                if (s == null ? c == null : s.equalsIgnoreCase(c)) { existe = true; break; }
            }
            if (!existe) cursos.add(c);
        }
        return cursos;
    }

    public boolean momentosValidosParaAno(int anoLetivo, String nomeCurso) {
        List<MomentoAvaliacao> lista = getMomentosParaAno(anoLetivo, nomeCurso);
        if (lista.isEmpty() || lista.size() > 3) return false;
        double soma = 0;
        for (MomentoAvaliacao m : lista) soma += m.getPeso();
        return Math.abs(soma - 100.0) < 0.01;
    }

    public double somaPesosParaAno(int anoLetivo) {
        double soma = 0;
        for (MomentoAvaliacao m : getMomentosParaAno(anoLetivo)) soma += m.getPeso();
        return soma;
    }

    public boolean momentosValidosParaAno(int anoLetivo) {
        List<String> cursos = cursosComMomentosParaAno(anoLetivo);
        if (cursos.isEmpty()) return false;
        for (String curso : cursos) {
            if (momentosValidosParaAno(anoLetivo, curso)) return true;
        }
        return false;
    }

    public boolean momentosValidos() {
        if (momentosAvaliacao == null || momentosAvaliacao.isEmpty()) return false;
        if (momentosAvaliacao.size() > 3) return false;
        double soma = 0;
        for (MomentoAvaliacao m : momentosAvaliacao) soma += m.getPeso();
        return Math.abs(soma - 100.0) < 0.01;
    }

    public double somaPesos() {
        double soma = 0;
        for (MomentoAvaliacao m : momentosAvaliacao) soma += m.getPeso();
        return soma;
    }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    @Override
    public String toString() {
        String docente = temDocenteResponsavel() ? docenteResponsavel : "(sem docente responsável)";
        return  "=== Unidade Curricular ===\n" +
                "Nome: " + this.nome + "\n" +
                "ECTS: " + this.ects + "\n" +
                "Docente: " + docente + "\n" +
                "Momentos: " + this.momentosAvaliacao.size() + "\n" +
                "Estado: " + (this.ativa ? "Ativa" : "Inativa") + "\n" +
                "===========================";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnidadeCurricular)) return false;
        UnidadeCurricular that = (UnidadeCurricular) o;
        return nome != null && nome.equalsIgnoreCase(that.nome);
    }

    @Override
    public int hashCode() {
        return nome != null ? nome.toLowerCase().hashCode() : 0;
    }
}
