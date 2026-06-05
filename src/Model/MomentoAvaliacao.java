package Model;

public class MomentoAvaliacao {
    private String nome;
    private double peso;
    /**
     * Ano letivo a que este momento pertence.
     * 0 = legado (criado antes de momentos por ano letivo existirem).
     */
    private int anoLetivo;

    /** Construtor com ano letivo (uso normal). */
    public MomentoAvaliacao(String nome, double peso, int anoLetivo) {
        this.nome      = nome;
        this.peso      = peso;
        this.anoLetivo = anoLetivo;
    }

    /** Construtor legado sem ano letivo — anoLetivo fica a 0. */
    public MomentoAvaliacao(String nome, double peso) {
        this(nome, peso, 0);
    }

    public String getNome()  { return nome; }
    public void   setNome(String nome)   { this.nome = nome; }

    public double getPeso()  { return peso; }
    public void   setPeso(double peso)   { this.peso = peso; }

    public int  getAnoLetivo()            { return anoLetivo; }
    public void setAnoLetivo(int anoLetivo) { this.anoLetivo = anoLetivo; }

    @Override
    public String toString() {
        String sufixo = anoLetivo > 0 ? " [" + anoLetivo + "/" + (anoLetivo + 1) + "]" : "";
        return "--- Momento de Avaliação ---" +
               "\nNome: " + nome +
               "\nPeso: " + peso + "%" +
               sufixo;
    }
}
