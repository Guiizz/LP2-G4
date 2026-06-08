package Controller;

import DAL.*;
import BLL.*;

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
    private final HorarioController horarioController;
    private final PresencaController presencaController;
    private final JustificacaoController justificacaoController;

    public LoginController() {

        UnidadeCurricularDAL ucDAL = new UnidadeCurricularDAL();
        DepartamentoDAL depDAL = new DepartamentoDAL();
        CursoDAL cursoDAL = new CursoDAL(depDAL, ucDAL);
        DocenteDAL docenteDAL = new DocenteDAL(ucDAL);
        EstudanteDAL estudanteDAL = new EstudanteDAL(cursoDAL);
        GestorDAL gestorDAL = new GestorDAL();
        AnoLetivoDAL anoLetivoDAL = new AnoLetivoDAL();
        DAL.HorarioDAL horarioDAL = new DAL.HorarioDAL();
        DAL.RegistoAulaDAL registoAulaDAL = new DAL.RegistoAulaDAL();
        DAL.PresencaDAL presencaDAL = new DAL.PresencaDAL();
        DAL.TipoJustificacaoDAL tipoJustDAL = new DAL.TipoJustificacaoDAL();
        DAL.JustificacaoDAL justificacaoDAL = new DAL.JustificacaoDAL();

        UnidadeCurricularBLL ucBLL = new UnidadeCurricularBLL(ucDAL, anoLetivoDAL);
        DepartamentoBLL depBLL = new DepartamentoBLL(depDAL);
        CursoBLL cursoBLL = new CursoBLL(cursoDAL, estudanteDAL, anoLetivoDAL, ucDAL);
        DocenteBLL docenteBLL = new DocenteBLL(docenteDAL, estudanteDAL);
        EstudanteBLL estBLL = new EstudanteBLL(estudanteDAL, docenteDAL, anoLetivoDAL);
        GestorBLL gestorBLL = new GestorBLL(gestorDAL);
        AnoLetivoBLL anoLetBLL = new AnoLetivoBLL(anoLetivoDAL);
        AvaliacaoBLL avalBLL = new AvaliacaoBLL(new AvaliacaoDAL(ucDAL));
        BLL.HorarioBLL horarioBLL = new BLL.HorarioBLL(horarioDAL, ucDAL);
        BLL.PresencaBLL presencaBLL = new BLL.PresencaBLL(registoAulaDAL, presencaDAL);
        BLL.JustificacaoBLL justificacaoBLL = new BLL.JustificacaoBLL(justificacaoDAL, tipoJustDAL);

        this.unidadeCurricularController = new UnidadeCurricularController(ucBLL);
        this.departamentoController = new DepartamentoController(depBLL);
        this.cursoController = new CursoController(cursoBLL);
        this.docenteController = new DocenteController(docenteBLL);
        this.estudanteController = new EstudanteController(estBLL);
        this.gestorController = new GestorController(gestorBLL);
        this.anoLetivoController = new AnoLetivoController(anoLetBLL);
        this.avaliacaoController = new AvaliacaoController(avalBLL);
        this.inscricaoController = new InscricaoController(estBLL);
        this.horarioController = new HorarioController(horarioBLL);
        this.presencaController = new PresencaController(presencaBLL);
        this.justificacaoController = new JustificacaoController(justificacaoBLL);
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

    public HorarioController getHorarioController() {
        return horarioController;
    }

    public PresencaController getPresencaController() {
        return presencaController;
    }

    public JustificacaoController getJustificacaoController() {
        return justificacaoController;
    }
}