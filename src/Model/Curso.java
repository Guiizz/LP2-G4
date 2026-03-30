package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um Curso disponível no ISSMF.
 */
public class Curso {

    private String nomeCurso;
    private Departamento departamento;
    private int duracao;
    private List <UnidadeCurricular> unidades;

    /**
     * Construtor da classe Curso.
     * @param nomeCurso O nome do curso.
     * @param departamento O Departamento que o curso pertence.
     */
    public Curso(String nomeCurso, Departamento departamento) {
        this.nomeCurso = nomeCurso;
        this.departamento = departamento;
        this.duracao = 3;
        this.unidades = new ArrayList<>();
    }

    /**
     * Obtém o nome do curso.
     * @return O nome do curso.
     */
    public String getNomeCurso() {
        return nomeCurso;
    }

    /**
     * Define o nome do curso.
     * @param nomeCurso O novo nome do curso.
     */
    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    /**
     * Obtém o departamento responsável pelo curso.
     * @return O objeto Departamento.
     */
    public Departamento getDepartamento() {
        return departamento;
    }

    /**
     * Obtém a duração do curso em anos.
     * @return A duração (Sempre 3 anos).
     */
    public int getDuracao() {
        return duracao;
    }


    /**
     * Obtém a lista de todas as unidades curriculares deste curso.
     * @return Uma lista de Unidades Curriculares.
     */
    public List<UnidadeCurricular> getUnidades() {
        return unidades;
    }

    /**
     * Adiciona uma nova unidade curricular ao curso.
     * @param uc A Unidade Curricular adicionada.
     */
    public void adicionarUnidadeCurricular(UnidadeCurricular uc) {
        this.unidades.add(uc);
    }

    /**
     * Formato de texto da ficha Curso.
     * @return Uma String formatada com os detalhes do curso.
     */
    @Override
    public String toString() {
        return "=== Curso ===\n" +
                "Nome do Curso: " + nomeCurso + "\n" +
                "Departamento: " + (departamento != null ? departamento.getNome() : "Sem departamento") + "\n" +
                "Duracao:" + duracao + " anos\n" +
                "Unidades:" + unidades.size() + "\n" +
                "=============";
    }
}
