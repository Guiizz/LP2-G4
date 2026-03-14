package Model;

/**
 * Registo de inscrição de um estudante num determinado ano letivo.
 */
public class Inscricao {
    private int anoLetivo;
    private int anoDeCurso;
    private Curso curso;

    /**
     * Construtor da classe Inscricao.
     * @param anoLetivo O ano letivo que a inscrição está a realizar. (ex: 2026 para o ano 2026/2027).
     * @param anoDeCurso O ano curricular do curso em que o estudante se inscreve (ex: 1, 2 ou 3).
     * @param curso O curso associado a esta inscrição.
     */
    public Inscricao(int anoLetivo, int anoDeCurso, Curso curso) {
        this.anoLetivo = anoLetivo;
        this.anoDeCurso = anoDeCurso;
        this.curso = curso;
    }

    /**
     * Obtém o ano letivo da inscrição.
     * @return O ano letivo
     */
    public int getAnoLetivo() {
        return anoLetivo;
    }

    /**
     * Obtém o ano curricular (do curso) correspondente a esta inscrição.
     * @return O ano de curso (ex: 1, 2 ou 3).
     */
    public int getAnoDeCurso() {
        return anoDeCurso;
    }

    /**
     * Obtém o curso para esta inscrição.
     * @return O objeto Curso.
     */
    public Curso getCurso() {
        return curso;
    }

    /**
     * Formato em texto da ficha Inscricao
     * @return Uma String formatada com os detalhes da inscrição.
     */
    @Override
    public String toString() {
        return "=== Inscricao ===\n" +
                "Ano Letivo: " + anoLetivo + "\n" +
                "Ano de Curso: " + anoDeCurso + "\n" +
                "Curso: " + curso.getNomeCurso() + "\n" +
                "================";
    }
}

