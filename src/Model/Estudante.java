package Model;

import java.time.LocalDate;
import java.util.ArrayList;
import Utils.PasswordUtils;

public class Estudante extends Utilizador {

    private static int contadorSequencial = 260001;
    private String numMecanografico;
    private int anoAtual;
    private String estado;
    private ArrayList<Inscricao> inscricoes;

    public Estudante(String nome, LocalDate dataNascimento, String nif, String morada) {
        super(nome, dataNascimento, nif, morada, "","");
        this.numMecanografico = String.valueOf(contadorSequencial++);
        String emailAutomatico = this.numMecanografico + "@issmf.pt";
        String passAutomatica = PasswordUtils.hashPassword("Issmf" + this.numMecanografico);
        this.setEmail(emailAutomatico);
        this.setPassword(passAutomatica);
        this.anoAtual = 1;
        this.estado = "ATIVO";
        this.inscricoes = new ArrayList<>();
    }

    public Estudante(String nome, LocalDate dataNascimento, String nif, String morada, boolean carregarDoCSV) {
        super(nome, dataNascimento, nif, morada, "", "");
        this.numMecanografico = "";
        this.anoAtual = 1;
        this.estado = "ATIVO";
        this.inscricoes = new ArrayList<>();
    }

    public static int getContadorSequencial() {
        return contadorSequencial;
    }

    public static void setContadorSequencial(int contadorSequencial) {
        Estudante.contadorSequencial = contadorSequencial;
    }

    public String getNumMecanografico() {
        return numMecanografico;
    }

    public int getAnoAtual() {
        return anoAtual;
    }

    public void setAnoAtual(int anoAtual) {
        this.anoAtual = anoAtual;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado == null || estado.isBlank() ? "ATIVO" : estado;
    }

    public boolean isConcluido() {
        return "CONCLUIDO".equalsIgnoreCase(estado);
    }

    public ArrayList<Inscricao> getInscricoes() {
        return inscricoes;
    }

    public void setInscricoes(ArrayList<Inscricao> inscricoes) {
        this.inscricoes = inscricoes != null ? inscricoes : new ArrayList<>();
    }

    public void adicionarInscricao(Inscricao inscricao) {
        this.inscricoes.add(inscricao);
    }

    public void setNumMecanografico(String numMecanografico) {
        this.numMecanografico = numMecanografico;
    }

    /**
     * Devolve todas as avaliações reprovadas em inscrições anteriores
     * que ainda não foram recuperadas (aprovadas) em anos seguintes.
     * Usado para contabilizar UCs em atraso no cálculo de progressão.
     */
    public ArrayList<Avaliacao> getUCsEmAtraso() {
        ArrayList<Avaliacao> emAtraso = new ArrayList<>();
        if (inscricoes == null || inscricoes.size() <= 1) return emAtraso;

        for (int i = 0; i < inscricoes.size() - 1; i++) {
            Inscricao inscricao = inscricoes.get(i);
            for (Avaliacao reprovada : inscricao.getAvaliacoesReprovadas()) {
                if (!foiRecuperadaDepois(reprovada, i + 1)) {
                    emAtraso.add(reprovada);
                }
            }
        }
        return emAtraso;
    }

    /**
     * Verifica se uma avaliação reprovada foi aprovada (recuperada)
     * em alguma das inscrições a partir do índice indicado.
     */
    private boolean foiRecuperadaDepois(Avaliacao reprovada, int apartirDeIndice) {
        for (int i = apartirDeIndice; i < inscricoes.size(); i++) {
            for (Avaliacao av : inscricoes.get(i).getAvaliacoes()) {
                if (av != null && av.isLancada() && av.getNota() >= 10
                        && av.getUc() != null && reprovada.getUc() != null
                        && av.getUc().equals(reprovada.getUc())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Calcula o aproveitamento global do estudante considerando
     * as UCs em atraso de anos anteriores + as avaliações do ano atual.
     * Fórmula: aprovadas_ano_atual / (total_ano_atual + UCs_em_atraso)
     */
    public double calcularAproveitamentoGlobal() {
        if (inscricoes == null || inscricoes.isEmpty()) return 0;

        Inscricao inscricaoAtual = inscricoes.get(inscricoes.size() - 1);
        int aprovadas = inscricaoAtual.getTotalAvaliacoesAprovadas();
        int totalAtual = inscricaoAtual.getAvaliacoes().size();
        int totalEmAtraso = getUCsEmAtraso().size();
        int totalGlobal = totalAtual + totalEmAtraso;

        if (totalGlobal == 0) return 0;
        return (double) aprovadas / totalGlobal;
    }
    /**
     * Devolve o curso da inscrição mais recente, ou null se não existir nenhuma.
     */
    public Curso getCursoAtual() {
        if (inscricoes == null || inscricoes.isEmpty()) {
            return null;
        }
        return inscricoes.get(inscricoes.size() - 1).getCurso();
    }

    @Override
    public String toString() {
        Curso cursoAtual = getCursoAtual();
        String nomeCurso = (cursoAtual != null) ? cursoAtual.getNomeCurso() : "(sem curso)";
        return "=== Ficha de Estudante ===\n" +
                "Nº Mecanográfico: " + numMecanografico + "\n" +
                "Nome: " + getNome() + "\n" +
                "E-mail: " + getEmail() + "\n" +
                "Curso: " + nomeCurso + "\n" +
                "Ano: " + anoAtual + "\n" +
                "Estado: " + estado + "\n" +
                "=========================";
    }

    public String toStringDetalhado() {
        Curso cursoAtual = getCursoAtual();
        String nomeCurso = (cursoAtual != null) ? cursoAtual.getNomeCurso() : "(sem curso)";
        return "=== Ficha de Estudante ===\n" +
                "Nº Mecanográfico: " + numMecanografico + "\n" +
                "Nome: " + getNome() + "\n" +
                "E-mail: " + getEmail() + "\n" +
                "Data de Nascimento: " + getDataNascimento() + "\n" +
                "NIF: " + getNif() + "\n" +
                "Morada: " + getMorada() + "\n" +
                "Curso: " + nomeCurso + "\n" +
                "Ano: " + anoAtual + "\n" +
                "Estado: " + estado + "\n" +
                "=========================";
    }
}