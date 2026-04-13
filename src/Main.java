import BLL.EstudanteBLL;
import BLL.GestorBLL;
import Controller.DocenteController;
import Controller.EstudanteController;
import Controller.GestorController;
import DAL.EstudanteDAL;
import View.LoginView;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Ponto de arranque da aplicação ISSMF.
 * Hierarquia:  DAL → BLL → Controller → View
 */
public class Main {

    public static void main(String[] args) {

        // ── DAL ──────────────────────────────────────────────────────────────
        EstudanteDAL estudanteDAL = new EstudanteDAL();
        // DocenteDAL é instanciado internamente no DocenteController
        // GestorDAL é instanciado internamente na GestorBLL

        // ── BLL ──────────────────────────────────────────────────────────────
        EstudanteBLL estudanteBLL = new EstudanteBLL(estudanteDAL);
        GestorBLL    gestorBLL    = new GestorBLL();

        // ── Controllers ──────────────────────────────────────────────────────
        EstudanteController  estudanteController  = new EstudanteController(estudanteBLL);
        DocenteController docenteController = new DocenteController();
        GestorController gestorController = new GestorController(gestorBLL);

        // ── Dados iniciais (bootstrap) ────────────────────────────────────────
        bootstrapDados(gestorBLL, estudanteBLL);

        // ── Scanner partilhado ───────────────────────────────────────────────
        Scanner scanner = new Scanner(System.in);

        // ── Arranque ─────────────────────────────────────────────────────────
        new LoginView(estudanteController, docenteController, gestorController, scanner).iniciar();

        scanner.close();
    }

    /**
     * Popula o sistema com dados mínimos para ser possível fazer login
     * sem precisar de registar utilizadores manualmente de raiz.
     *
     * Gestor por defeito:
     *   E-mail   : gestor@issmf.pt
     *   Password : Gestor123
     *
     * Estudante de teste:
     *   E-mail   : 260001@issmf.pt
     *   Password : Issmf260001
     */
    private static void bootstrapDados(GestorBLL gestorBLL, EstudanteBLL estudanteBLL) {

        // Gestor por defeito
        try {
            gestorBLL.registarGestor(
                    "Administrador",
                    LocalDate.of(1980, 1, 1),
                    "999999999",
                    "ISSMF",
                    "gestor@issmf.pt",
                    "Gestor123"
            );
        } catch (IllegalArgumentException e) {
            // Já existe — ignora
        }

        // Estudante de teste
        try {
            estudanteBLL.registarEstudante(
                    "Ana Silva",
                    LocalDate.of(2003, 5, 10),
                    "123456789",
                    "Porto"
            );
        } catch (IllegalArgumentException e) {
            // Já existe — ignora
        }
    }
}