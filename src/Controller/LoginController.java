package Controller;

public class LoginController {
    private final EstudanteController estudanteController;
    private final GestorController gestorController;
    private final DocenteController docenteController;
    private final DepartamentoController departamentoController;
    private final CursoController cursoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final AvaliacaoController avaliacaoController;
    private final InscricaoController inscricaoController;
    private final AnoLetivoController anoLetivoController;

    public LoginController() {
        this.gestorController = new GestorController();
        this.estudanteController = new EstudanteController();
        this.docenteController = new DocenteController();
        this.departamentoController = new DepartamentoController();
        this.cursoController = new CursoController();
        this.unidadeCurricularController = new UnidadeCurricularController();
        this.avaliacaoController = new AvaliacaoController();
        this.inscricaoController = new InscricaoController();
        this.anoLetivoController = new AnoLetivoController();
    }

    public EstudanteController getEstudanteController() {
        return estudanteController;
    }

    public GestorController getGestorController() {
        return gestorController;
    }

    public DocenteController getDocenteController() {
        return docenteController;
    }

    public DepartamentoController getDepartamentoController() {
        return departamentoController;
    }

    public CursoController getCursoController() {
        return cursoController;
    }

    public UnidadeCurricularController getUnidadeCurricularController() {
        return unidadeCurricularController;
    }

    public AvaliacaoController getAvaliacaoController() {
        return avaliacaoController;
    }

    public InscricaoController getInscricaoController() {
        return inscricaoController;
    }

    public AnoLetivoController getAnoLetivoController() {
        return anoLetivoController;
    }
}
