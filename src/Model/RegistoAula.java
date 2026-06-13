package Model;

import java.time.LocalDate;

public class RegistoAula {

    private String siglaDocente;
    private String nomeUC;
    private String nomeCurso;
    private int anoLetivo;
    private LocalDate data;
    private String horaInicio;
    private boolean terminada;

    public RegistoAula(String siglaDocente, String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String horaInicio) {
        this(siglaDocente, nomeUC, nomeCurso, anoLetivo, data, horaInicio, false);
    }

    public RegistoAula(String siglaDocente, String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String horaInicio, boolean terminada) {
        this.siglaDocente = siglaDocente;
        this.nomeUC = nomeUC;
        this.nomeCurso = nomeCurso;
        this.anoLetivo = anoLetivo;
        this.data = data;
        this.horaInicio = horaInicio;
        this.terminada = terminada;
    }

    public boolean isTerminada() { return terminada; }
    public void setTerminada(boolean terminada) { this.terminada = terminada; }

    public String getSiglaDocente() {
        return siglaDocente;
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

    public boolean corresponde(String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String horaInicio) {
        return this.nomeUC.equalsIgnoreCase(nomeUC)
                && this.nomeCurso.equalsIgnoreCase(nomeCurso)
                && this.anoLetivo == anoLetivo
                && this.data.equals(data)
                && this.horaInicio.equals(horaInicio);
    }

    @Override
    public String toString() {
        return data + "  " + horaInicio + "  |  " + nomeUC + "  (" + nomeCurso + ")"
                + (terminada ? "  [TERMINADA]" : "");
    }
}
