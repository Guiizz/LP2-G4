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
    private final String notasSerialized; // coluna "notas" da tabela Inscricao

    public DadosEstudanteFecho(String numMecanografico, String nome, int anoAtual,
                               String estado, int anoDeCurso, String nomeCurso,
                               boolean propinaPaga, String notasSerialized) {
        this.numMecanografico  = numMecanografico;
        this.nome              = nome;
        this.anoAtual          = anoAtual;
        this.estado            = estado;
        this.anoDeCurso        = anoDeCurso;
        this.nomeCurso         = nomeCurso;
        this.propinaPaga       = propinaPaga;
        this.notasSerialized   = notasSerialized;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getNumMecanografico() { return numMecanografico; }
    public String getNome()             { return nome; }
    public int    getAnoAtual()         { return anoAtual; }
    public String getEstado()           { return estado; }
    public int    getAnoDeCurso()       { return anoDeCurso; }
    public String getNomeCurso()        { return nomeCurso; }
    public boolean isPropinaPaga()      { return propinaPaga; }
    public String getNotasSerialized()  { return notasSerialized; }

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
     * Calcula a taxa de aprovação (aprovadas / total) a partir das notas serializadas.
     * Retorna 0.0 se não houver avaliações.
     */
    public double calcularAproveitamento() {
        if (notasSerialized == null || notasSerialized.isBlank()) return 0.0;
        String[] pares = notasSerialized.split(",");
        if (pares.length == 0) return 0.0;

        int total = 0;
        int aprovadas = 0;
        for (String par : pares) {
            String[] partes = par.trim().split(":", 2);
            String valor = partes.length > 1 ? partes[1].trim() : par.trim();
            if (valor.equalsIgnoreCase("P")) {
                total++;                    // pendente conta como reprovação temporária
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
}
