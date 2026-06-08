package Model;

import java.time.LocalDate;

public class JustificacaoFalta {

    public static final String PENDENTE = "PENDENTE";
    public static final String APROVADA = "APROVADA";
    public static final String REJEITADA = "REJEITADA";

    private String numMecanografico;
    private String nomeUC;
    private String nomeCurso;
    private int anoLetivo;
    private LocalDate dataAula;
    private String horaInicio;
    private String nomeTipoJustificacao;
    private String estado;
    private LocalDate dataPedido;

    public JustificacaoFalta(String numMecanografico, String nomeUC, String nomeCurso, int anoLetivo, LocalDate dataAula, String horaInicio, String nomeTipoJustificacao) {
        this.numMecanografico = numMecanografico;
        this.nomeUC = nomeUC;
        this.nomeCurso = nomeCurso;
        this.anoLetivo = anoLetivo;
        this.dataAula = dataAula;
        this.horaInicio = horaInicio;
        this.nomeTipoJustificacao = nomeTipoJustificacao;
        this.estado = PENDENTE;
        this.dataPedido = LocalDate.now();
    }

    public JustificacaoFalta(String numMecanografico, String nomeUC, String nomeCurso, int anoLetivo, LocalDate dataAula, String horaInicio, String nomeTipoJustificacao, String estado, LocalDate dataPedido) {
        this.numMecanografico = numMecanografico;
        this.nomeUC = nomeUC;
        this.nomeCurso = nomeCurso;
        this.anoLetivo = anoLetivo;
        this.dataAula = dataAula;
        this.horaInicio = horaInicio;
        this.nomeTipoJustificacao = nomeTipoJustificacao;
        this.estado = estado;
        this.dataPedido = dataPedido;
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

    public LocalDate getDataAula() {
        return dataAula;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getNomeTipoJustificacao() {
        return nomeTipoJustificacao;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return numMecanografico + "  |  " + nomeUC + "  |  " + dataAula + " " + horaInicio
                + "  |  " + nomeTipoJustificacao + "  |  " + estado;
    }
}
