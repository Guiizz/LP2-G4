package Model;

/**
 * DTO (Data Transfer Object) usado no fecho de ano letivo em modo BD.
 *
 * Populado por uma única query JOIN que combina Estudante + Inscricao + Propina,
 * evitando carregar objetos completos para memória.
 */
public class DadosEstudanteFecho {

    private final String numMecanografico;
    private final String nome;
    private final int    anoAtual;        // campo anoAtual da tabela Estudante
    private final String estado;
    private final int    anoDeCurso;      // da tabela Inscricao
    private final String nomeCurso;       // da tabela Inscricao
    private final boolean propinaPaga;    // valorPago >= valorTotal (calculado em SQL)
    private final String notasSerialized;         // notas do ano corrente
    private final String notasAnosAnteriores;     // notas concatenadas de anos anteriores

    public DadosEstudanteFecho(String numMecanografico, String nome, int anoAtual,
                               String estado, int anoDeCurso, String nomeCurso,
                               boolean propinaPaga, String notasSerialized) {
        this(numMecanografico, nome, anoAtual, estado, anoDeCurso, nomeCurso,
             propinaPaga, notasSerialized, null);
    }

    public DadosEstudanteFecho(String numMecanografico, String nome, int anoAtual,
                               String estado, int anoDeCurso, String nomeCurso,
                               boolean propinaPaga, String notasSerialized,
                               String notasAnosAnteriores) {
        this.numMecanografico   = numMecanografico;
        this.nome               = nome;
        this.anoAtual           = anoAtual;
        this.estado             = estado;
        this.anoDeCurso         = anoDeCurso;
        this.nomeCurso          = nomeCurso;
        this.propinaPaga        = propinaPaga;
        this.notasSerialized    = notasSerialized;
        this.notasAnosAnteriores = notasAnosAnteriores;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getNumMecanografico() { return numMecanografico; }
    public String getNome()             { return nome; }
    public int    getAnoAtual()         { return anoAtual; }
    public String getEstado()           { return estado; }
    public int    getAnoDeCurso()       { return anoDeCurso; }
    public String getNomeCurso()        { return nomeCurso; }
    public boolean isPropinaPaga()      { return propinaPaga; }
    public String getNotasSerialized()       { return notasSerialized; }
    public String getNotasAnosAnteriores()   { return notasAnosAnteriores; }

    // ── Lógica derivada (calculada a partir das notas serializadas) ───────────

    /**
     * Devolve true se não houver notas lançadas ou se existirem avaliações pendentes.
     * O formato serializado é: "UC1:nota,UC2:P,UC3:nota" onde "P" = pendente.
     */
    public boolean temNotasPorLancar() {
        if (notasSerialized == null || notasSerialized.isBlank()) {
            return true; // sem avaliações = por lançar
        }
        for (String par : notasSerialized.split(",")) {
            String[] partes = par.trim().split(":", 2);
            String valor = partes.length > 1 ? partes[1].trim() : par.trim();
            if (valor.equalsIgnoreCase("P")) return true;
        }
        return false;
    }

    /**
     * Calcula a taxa de aprovação global (aprovadas / total) combinando o ano
     * corrente com todos os anos anteriores — equivalente a
     * Estudante.calcularAproveitamentoGlobal() no modo CSV.
     */
    public double calcularAproveitamento() {
        String todasNotas = combinarNotas(notasSerialized, notasAnosAnteriores);
        if (todasNotas == null || todasNotas.isBlank()) return 0.0;

        int total = 0;
        int aprovadas = 0;
        for (String par : todasNotas.split(",")) {
            String[] partes = par.trim().split(":", 2);
            String valor = partes.length > 1 ? partes[1].trim() : par.trim();
            if (valor.equalsIgnoreCase("P")) {
                total++;
            } else {
                try {
                    double nota = Double.parseDouble(valor);
                    total++;
                    if (nota >= 10.0) aprovadas++;
                } catch (NumberFormatException ignored) {}
            }
        }
        return total == 0 ? 0.0 : (double) aprovadas / total;
    }

    private static String combinarNotas(String atual, String anteriores) {
        if (atual == null || atual.isBlank()) return anteriores;
        if (anteriores == null || anteriores.isBlank()) return atual;
        return atual + "," + anteriores;
    }
}
