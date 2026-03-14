package Model;

public class Inscricao {
    private int anoLetivo;
    private int anoDeCurso;
    private Curso curso;

    public Inscricao(int anoLetivo, int anoDeCurso, Curso curso) {
        this.anoLetivo = anoLetivo;
        this.anoDeCurso = anoDeCurso;
        this.curso = curso;
    }

    public int getAnoLetivo() {
        return anoLetivo;
    }

    public void setAnoLetivo(int anoLetivo) {
        this.anoLetivo = anoLetivo;
    }

    public int getAnoDeCurso() {
        return anoDeCurso;
    }

    public void setAnoDeCurso(int anoDeCurso) {
        this.anoDeCurso = anoDeCurso;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {
        return "=== Inscricao ===" +
                "Ano Letivo: " + anoLetivo + "\n" +
                "Ano de Curso: " + anoDeCurso + "\n" +
                "Curso: " + curso.getNomeCurso() + "\n" +
                "================";
    }
}

