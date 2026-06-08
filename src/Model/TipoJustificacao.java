package Model;

public class TipoJustificacao {

    public static final String CATEGORIA_SAUDE = "SAUDE";
    public static final String CATEGORIA_ESTATUTO = "ESTATUTO";

    private String nome;
    private String categoria;

    public TipoJustificacao(String nome, String categoria) {
        this.nome = nome;
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return nome + "  [" + categoria + "]";
    }
}
