package Model;

public class BlocoHorario {

    private String diaSemana;
    private String horaInicio;
    private int duracao; // minutos: 60 ou 120
    private String nomeUC;

    public BlocoHorario(String diaSemana, String horaInicio, int duracao, String nomeUC) {
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.duracao = duracao;
        this.nomeUC = nomeUC;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public int getDuracao() {
        return duracao;
    }

    public String getNomeUC() {
        return nomeUC;
    }

    public String getHoraFim() {
        String[] partes = horaInicio.split(":");
        int totalMin = Integer.parseInt(partes[0]) * 60 + Integer.parseInt(partes[1]) + duracao;
        return String.format("%02d:%02d", totalMin / 60, totalMin % 60);
    }

    @Override
    public String toString() {
        return diaSemana + "  " + horaInicio + "–" + getHoraFim()
                + "  |  " + nomeUC + "  (" + (duracao / 60) + "h)";
    }
}
