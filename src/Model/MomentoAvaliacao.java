package Model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MomentoAvaliacao {
    private String nome;
    private double peso;
    /**
     * Ano letivo a que este momento pertence.
     * 0 = legado (criado antes de momentos por ano letivo existirem).
     */
    private int anoLetivo;
    /**
     * Curso a que este momento pertence. A mesma UC noutro curso
     * tem momentos próprios (datas possivelmente diferentes).
     * null = legado (criado antes de momentos por curso existirem).
     */
    private String nomeCurso;
    /** Data agendada do momento de avaliação. null = legado. */
    private Date data;

    /** Construtor completo (uso normal). */
    public MomentoAvaliacao(String nome, double peso, int anoLetivo, String nomeCurso, Date data) {
        this.nome      = nome;
        this.peso      = peso;
        this.anoLetivo = anoLetivo;
        this.nomeCurso = nomeCurso;
        this.data      = data;
    }

    /** Construtor sem curso/data — mantido por compatibilidade. */
    public MomentoAvaliacao(String nome, double peso, int anoLetivo) {
        this(nome, peso, anoLetivo, null, null);
    }

    /** Construtor legado sem ano letivo — anoLetivo fica a 0. */
    public MomentoAvaliacao(String nome, double peso) {
        this(nome, peso, 0, null, null);
    }

    public String getNome()  { return nome; }
    public void   setNome(String nome)   { this.nome = nome; }

    public double getPeso()  { return peso; }
    public void   setPeso(double peso)   { this.peso = peso; }

    public int  getAnoLetivo()            { return anoLetivo; }
    public void setAnoLetivo(int anoLetivo) { this.anoLetivo = anoLetivo; }

    public String getNomeCurso() { return nomeCurso; }
    public void   setNomeCurso(String nomeCurso) { this.nomeCurso = nomeCurso; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public String getDataFormatada() {
        if (data == null) return "-";
        return new SimpleDateFormat("dd/MM/yyyy").format(data);
    }

    /** True se este momento pertence ao curso indicado (momentos legados sem curso contam para todos). */
    public boolean pertenceAoCurso(String curso) {
        return nomeCurso == null || (curso != null && nomeCurso.equalsIgnoreCase(curso));
    }

    @Override
    public String toString() {
        String sufixo = anoLetivo > 0 ? " [" + anoLetivo + "/" + (anoLetivo + 1) + "]" : "";
        return "--- Momento de Avaliação ---" +
               "\nNome: " + nome +
               (nomeCurso != null ? "\nCurso: " + nomeCurso : "") +
               "\nPeso: " + peso + "%" +
               (data != null ? "\nData: " + getDataFormatada() : "") +
               sufixo;
    }
}
