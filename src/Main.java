import BLL.*;
import Controller.*;
import DAL.*;
import View.LoginView;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // ── DAL ───────────────────────────────────────────────────────────────
        UnidadeCurricularDAL unidadeCurricularDAL = new UnidadeCurricularDAL();
        EstudanteDAL         estudanteDAL         = new EstudanteDAL();
        GestorDAL            gestorDAL            = new GestorDAL();
        DepartamentoDAL      departamentoDAL      = new DepartamentoDAL();
        DocenteDAL           docenteDAL           = new DocenteDAL(unidadeCurricularDAL);
        CursoDAL             cursoDAL             = new CursoDAL(departamentoDAL, unidadeCurricularDAL);
        AvaliacaoDAL         avaliacaoDAL         = new AvaliacaoDAL(unidadeCurricularDAL);

        // ── BLL ───────────────────────────────────────────────────────────────
        EstudanteBLL           estudanteBLL           = new EstudanteBLL(estudanteDAL);
        GestorBLL              gestorBLL              = new GestorBLL(gestorDAL);
        DepartamentoBLL        departamentoBLL        = new DepartamentoBLL(departamentoDAL);
        CursoBLL               cursoBLL               = new CursoBLL(cursoDAL, estudanteDAL);
        DocenteBLL             docenteBLL             = new DocenteBLL(docenteDAL);
        UnidadeCurricularBLL   unidadeCurricularBLL   = new UnidadeCurricularBLL(unidadeCurricularDAL);
        AvaliacaoBLL           avaliacaoBLL           = new AvaliacaoBLL(avaliacaoDAL);

        // ── Controllers ───────────────────────────────────────────────────────
        EstudanteController          estudanteController        = new EstudanteController(estudanteBLL);
        GestorController             gestorController           = new GestorController(gestorBLL);
        DepartamentoController       departamentoController     = new DepartamentoController(departamentoBLL);
        CursoController              cursoController            = new CursoController(cursoBLL);
        DocenteController            docenteController          = new DocenteController(docenteBLL);
        UnidadeCurricularController  unidadeCurricularController = new UnidadeCurricularController(unidadeCurricularBLL);
        AvaliacaoController          avaliacaoController        = new AvaliacaoController(avaliacaoBLL);
        InscricaoController          inscricaoController        = new InscricaoController(estudanteBLL);

        // ── Scanner partilhado ────────────────────────────────────────────────
        Scanner scanner = new Scanner(System.in);

        // ── Arranque ──────────────────────────────────────────────────────────
        new LoginView(
                gestorController,
                estudanteController,
                docenteController,
                departamentoController,
                cursoController,
                unidadeCurricularController,
                avaliacaoController,
                inscricaoController,
                scanner
        ).iniciar();
    }
}