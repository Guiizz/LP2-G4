package Model;

import java.time.LocalDate;
import java.util.List;
import Utils.PasswordUtils;

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
        super(nome, dataNascimento, nif, morada, sigla + "@issmf.pt", PasswordUtils.hashPassword("Issmf" + sigla));
        this.sigla = sigla;
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

    @Override
    public String toString() {
        String ucs;
        if (unidadesLecionadas == null || unidadesLecionadas.isEmpty()) {
            ucs = "(nenhuma)";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < unidadesLecionadas.size(); i++) {
                sb.append(unidadesLecionadas.get(i).getNome());
                if (i < unidadesLecionadas.size() - 1) {
                    sb.append(", ");
                }
            }
            ucs = sb.toString();
        }
        return "===== Ficha do Docente =====\n" +
                "Nome: " + getNome() + "\n" +
                "Email: " + getEmail() + "\n" +
                "Unidades Lecionadas: " + ucs + "\n" +
                "============================";
    }

    public String toStringDetalhado() {
        String ucs;
        if (unidadesLecionadas == null || unidadesLecionadas.isEmpty()) {
            ucs = "(nenhuma)";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < unidadesLecionadas.size(); i++) {
                sb.append(unidadesLecionadas.get(i).getNome());
                if (i < unidadesLecionadas.size() - 1) {
                    sb.append(", ");
                }
            }
            ucs = sb.toString();
        }
        return "===== Ficha do Docente =====\n" +
                "Nome: " + getNome() + "\n" +
                "Data de Nascimento: " + getDataNascimento() + "\n" +
                "NIF: " + getNif() + "\n" +
                "Morada: " + getMorada() + "\n" +
                "Email: " + getEmail() + "\n" +
                "Sigla: " + sigla + "\n" +
                "Unidades Lecionadas: " + ucs + "\n" +
                "============================";
    }
}
