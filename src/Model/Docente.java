package Model;

import java.time.LocalDate;
import java.util.List;

public class Docente extends Utilizador {

    /**
     * Atributos
     */
    private String sigla;
    private List<UnidadeCurricular> unidadesLecionadas;

    /**
     * Construtor
     * @param nome
     * @param dataNascimento
     * @param nif
     * @param morada
     * @param sigla
     * @param unidadesLecionadas
     */
    public Docente(String nome, LocalDate dataNascimento, String nif, String morada, String sigla, List<UnidadeCurricular> unidadesLecionadas) {
        this.sigla = sigla;
        super(nome, dataNascimento, nif, morada,"", "");
        String emailAutomatico = this.sigla + "@issmf.pt";
        String passwordAutomatico = "Issmf" + this.sigla;
        this.setEmail(emailAutomatico);
        this.setPassword(passwordAutomatico);
        this.unidadesLecionadas = unidadesLecionadas;
    }

    /**
     * Gets e Sets
     * @return
     */
    public String getSigla() {
        return sigla;
    }

    public List<UnidadeCurricular> getUnidadesLecionadas() {
        return unidadesLecionadas;
    }

    public void setUnidadesLecionadas(List<UnidadeCurricular> unidadesLecionadas) {
        this.unidadesLecionadas = unidadesLecionadas;
    }

    /**
     * toString
     * @return
     */
    @Override
    public String toString() {
        return "===== Ficha do Docente =====\n" +
                "Nome: " + getNome() + "\n" +
                "Email: " + getEmail() + "\n" +
                "Unidades Lecionadas: " + unidadesLecionadas + "\n" +
                "============================";
    }

    /**
     * toStringDetalhado
     * @return
     */
    public String toStringDetalhado() {
        return  "===== Ficha do Docente =====\n" +
                "Nome: " + getNome() + "\n" +
                "Data de Nascimento: " + getDataNascimento() + "\n" +
                "NIF: " + getNif() + "\n" +
                "Morada: " + getMorada() + "\n" +
                "Email: " + getEmail() + "\n" +
                "Sigla: " + sigla + "\n" +
                "Unidades Lecionadas: " + unidadesLecionadas + "\n" +
                "============================";
    }
}
