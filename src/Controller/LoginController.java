package Controller;

import Config.ModoPersistencia;
import DAL.*;
import DAL.BD.AnoLetivoDAL_BD;
import DAL.BD.HorarioDAL_BD;
import DAL.BD.PresencaDAL_BD;
import DAL.BD.RegistoAulaDAL_BD;
import DAL.BD.JustificacaoDAL_BD;
import DAL.BD.TipoJustificacaoDAL_BD;
import DAL.BD.AvaliacaoDAL_BD;
import DAL.BD.CursoDAL_BD;
import DAL.BD.DepartamentoDAL_BD;
import DAL.BD.DocenteDAL_BD;
import DAL.BD.EstudanteDAL_BD;
import DAL.BD.GestorDAL_BD;
import DAL.BD.InscricaoDAL_BD;
import DAL.BD.MomentoAvaliacaoDAL_BD;
import DAL.BD.PagamentoDAL_BD;
import DAL.BD.PropinaDAL_BD;
import DAL.BD.UnidadeCurricularDAL_BD;
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

        IMomentoAvaliacaoDAL momentoDAL = ModoPersistencia.isBaseDados()
                ? new MomentoAvaliacaoDAL_BD()
                : new MomentoAvaliacaoDAL();
        IUnidadeCurricularDAL ucDAL = ModoPersistencia.isBaseDados()
                ? new UnidadeCurricularDAL_BD(momentoDAL)
                : new UnidadeCurricularDAL();
        IDepartamentoDAL depDAL = ModoPersistencia.isBaseDados()
                ? new DepartamentoDAL_BD()
                : new DepartamentoDAL();
        ICursoDAL cursoDAL = ModoPersistencia.isBaseDados()
                ? new CursoDAL_BD(depDAL, ucDAL)
                : new CursoDAL(depDAL, ucDAL);
        IDocenteDAL docenteDAL = ModoPersistencia.isBaseDados()
                ? new DocenteDAL_BD(ucDAL)
                : new DocenteDAL(ucDAL);
        IPagamentoDAL pagamentoDAL = ModoPersistencia.isBaseDados()
                ? new PagamentoDAL_BD()
                : new PagamentoDAL();
        IPropinaDAL propinaDAL = ModoPersistencia.isBaseDados()
                ? new PropinaDAL_BD(pagamentoDAL)
                : new PropinaDAL();
        IInscricaoDAL inscricaoDAL = ModoPersistencia.isBaseDados()
                ? new InscricaoDAL_BD(propinaDAL)
                : new InscricaoDAL();
        IEstudanteDAL estudanteDAL = ModoPersistencia.isBaseDados()
                ? new EstudanteDAL_BD(cursoDAL, inscricaoDAL)
                : new EstudanteDAL(cursoDAL, inscricaoDAL);
        IGestorDAL gestorDAL = ModoPersistencia.isBaseDados()
                ? new GestorDAL_BD()
                : new GestorDAL();
        IAnoLetivoDAL anoLetivoDAL = ModoPersistencia.isBaseDados()
                ? new AnoLetivoDAL_BD()
                : new AnoLetivoDAL();
        IHorarioDAL horarioDAL = ModoPersistencia.isBaseDados()
                ? new HorarioDAL_BD()
                : new DAL.HorarioDAL();
        IRegistoAulaDAL registoAulaDAL = ModoPersistencia.isBaseDados()
                ? new RegistoAulaDAL_BD()
                : new DAL.RegistoAulaDAL();
        IPresencaDAL presencaDAL = ModoPersistencia.isBaseDados()
                ? new PresencaDAL_BD()
                : new DAL.PresencaDAL();
        ITipoJustificacaoDAL tipoJustDAL = ModoPersistencia.isBaseDados()
                ? new TipoJustificacaoDAL_BD()
                : new DAL.TipoJustificacaoDAL();
        IJustificacaoDAL justificacaoDAL = ModoPersistencia.isBaseDados()
                ? new JustificacaoDAL_BD()
                : new DAL.JustificacaoDAL();

        UnidadeCurricularBLL ucBLL = new UnidadeCurricularBLL(ucDAL, anoLetivoDAL);
        DepartamentoBLL depBLL = new DepartamentoBLL(depDAL);
        CursoBLL cursoBLL = new CursoBLL(cursoDAL, estudanteDAL, anoLetivoDAL, ucDAL);
        DocenteBLL docenteBLL = new DocenteBLL(docenteDAL, estudanteDAL);
        EstudanteBLL estBLL = new EstudanteBLL(estudanteDAL, docenteDAL, anoLetivoDAL);
        GestorBLL gestorBLL = new GestorBLL(gestorDAL, docenteDAL, estudanteDAL);
        AnoLetivoBLL anoLetBLL = new AnoLetivoBLL(anoLetivoDAL);
        IAvaliacaoDAL avaliacaoDAL = ModoPersistencia.isBaseDados()
                ? new AvaliacaoDAL_BD(ucDAL)
                : new AvaliacaoDAL(ucDAL);
        AvaliacaoBLL avalBLL = new AvaliacaoBLL(avaliacaoDAL, anoLetivoDAL);
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