package Model;

public class UCNoCurso {

    private final UnidadeCurricular uc;
    private final int anoCurricular;

    public UCNoCurso(UnidadeCurricular uc, int anoCurricular) {
        this.uc = uc;
        this.anoCurricular = anoCurricular;
    }

    public UnidadeCurricular getUc() { return uc; }
    public int getAnoCurricular() { return anoCurricular; }
}
