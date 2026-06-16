package Model;

import java.time.LocalDate;
import java.util.Objects;

public class Presenca {

    private String numMecanografico;
    private String nomeUC;
    private String nomeCurso;
    private int anoLetivo;
    private LocalDate data;
    private String horaInicio;
    private boolean presente;

    public Presenca(String numMecanografico, String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String horaInicio, boolean presente) {
        this.numMecanografico = numMecanografico;
        this.nomeUC = nomeUC;
        this.nomeCurso = nomeCurso;
        this.anoLetivo = anoLetivo;
        this.data = data;
        this.horaInicio = horaInicio;
        this.presente = presente;
    }

    public String getNumMecanografico() {
        return numMecanografico;
    }

    public String getNomeUC() {
        return nomeUC;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public int getAnoLetivo() {
        return anoLetivo;
    }

    public LocalDate getData() {
        return data;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public boolean isPresente() {
        return presente;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }

    public boolean corresponde(String numMecanografico, String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String horaInicio) {
        return this.numMecanografico.equals(numMecanografico)
                && this.nomeUC.equalsIgnoreCase(nomeUC)
                && this.nomeCurso.equalsIgnoreCase(nomeCurso)
                && this.anoLetivo == anoLetivo
                && this.data.equals(data)
                && this.horaInicio.equals(horaInicio);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Presenca)) return false;
        Presenca that = (Presenca) o;
        return anoLetivo == that.anoLetivo
                && Objects.equals(numMecanografico, that.numMecanografico)
                && Objects.equals(nomeUC, that.nomeUC)
                && Objects.equals(nomeCurso, that.nomeCurso)
                && Objects.equals(data, that.data)
                && Objects.equals(horaInicio, that.horaInicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numMecanografico, nomeUC, nomeCurso, anoLetivo, data, horaInicio);
    }

    @Override
    public String toString() {
        return data + "  " + horaInicio + "  |  " + nomeUC + "  |  "
                + (presente ? "Presente" : "Ausente");
    }
}
