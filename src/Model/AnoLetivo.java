package Model;

import java.time.LocalDate;

public class AnoLetivo {
    public static final String ESTADO_ABERTO = "ABERTO";
    public static final String ESTADO_FECHADO = "FECHADO";

    private int ano;
    private String estado;
    private LocalDate dataAbertura;
    private LocalDate dataFecho;

    public AnoLetivo(int ano, LocalDate dataAbertura) {
        this.ano = ano;
        this.estado = ESTADO_ABERTO;
        this.dataAbertura = dataAbertura;
        this.dataFecho = null;
    }

    public AnoLetivo(int ano, String estado, LocalDate dataAbertura, LocalDate dataFecho) {
        this.ano = ano;
        this.estado = estado;
        this.dataAbertura = dataAbertura;
        this.dataFecho = dataFecho;
    }

    public int getAno() {
        return ano;
    }

    public String getDesignacao() {
        return ano + "/" + (ano + 1);
    }

    public String getEstado() {
        return estado;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public LocalDate getDataFecho() {
        return dataFecho;
    }

    public boolean isAberto() {
        return ESTADO_ABERTO.equalsIgnoreCase(estado);
    }

    public void fechar(LocalDate dataFecho) {
        this.estado = ESTADO_FECHADO;
        this.dataFecho = dataFecho;
    }

    @Override
    public String toString() {
        return "=== Ano Letivo ===\n" +
                "Ano: " + getDesignacao() + "\n" +
                "Estado: " + estado + "\n" +
                "Data de abertura: " + dataAbertura + "\n" +
                "Data de fecho: " + (dataFecho == null ? "-" : dataFecho) + "\n" +
                "==================";
    }
}