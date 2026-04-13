import BLL.CursoBLL;
import BLL.DepartamentoBLL;
import BLL.EstudanteBLL;
import BLL.GestorBLL;
import BLL.UnidadeCurricularBLL;
import Controller.CursoController;
import Controller.DepartamentoController;
import Controller.DocenteController;
import Controller.EstudanteController;
import Controller.GestorController;
import Controller.UnidadeCurricularController;
import DAL.EstudanteDAL;
import View.LoginView;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        EstudanteDAL estudanteDAL = new EstudanteDAL();

        EstudanteBLL estudanteBLL = new EstudanteBLL(estudanteDAL);
        GestorBLL gestorBLL = new GestorBLL();

        EstudanteController estudanteController = new EstudanteController(estudanteBLL);
        DocenteController docenteController = new DocenteController();
        GestorController gestorController = new GestorController(gestorBLL);
        DepartamentoController departamentoController = new DepartamentoController(new DepartamentoBLL());
        CursoController cursoController = new CursoController(new CursoBLL());
        UnidadeCurricularController unidadeCurricularController = new UnidadeCurricularController(new UnidadeCurricularBLL());

        bootstrapDados(gestorBLL, estudanteBLL);

        Scanner scanner = new Scanner(System.in);

        new LoginView(
                estudanteController,
                docenteController,
                gestorController,
                departamentoController,
                cursoController,
                unidadeCurricularController,
                scanner
        ).iniciar();

        scanner.close();
    }

    private static void bootstrapDados(GestorBLL gestorBLL, EstudanteBLL estudanteBLL) {
        try {
            gestorBLL.registarGestor(
                    "Administrador",
                    LocalDate.of(1980, 1, 1),
                    "999999999",
                    "ISSMF",
                    "gestor@issmf.pt",
                    "Gestor123"
            );
        } catch (IllegalArgumentException ignored) {
        }

        try {
            estudanteBLL.registarEstudante(
                    "Ana Silva",
                    LocalDate.of(2003, 5, 10),
                    "123456789",
                    "Porto"
            );
        } catch (IllegalArgumentException ignored) {
        }
    }
}