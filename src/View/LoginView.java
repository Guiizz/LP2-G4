package View;

import BLL.*;
import Controller.*;
import DAL.*;
import Model.Docente;
import Model.Estudante;
import Model.Gestor;
import Utils.Utils;

import java.util.Scanner;

public class LoginView {


    private final UnidadeCurricularDAL unidadeCurricularDAL = new UnidadeCurricularDAL();
    private final EstudanteDAL estudanteDAL = new EstudanteDAL();
    private final GestorDAL gestorDAL = new GestorDAL();
    private final DepartamentoDAL departamentoDAL = new DepartamentoDAL();
    private final DocenteDAL docenteDAL = new DocenteDAL(unidadeCurricularDAL);
    private final CursoDAL cursoDAL = new CursoDAL(departamentoDAL, unidadeCurricularDAL);
    private final AvaliacaoDAL avaliacaoDAL = new AvaliacaoDAL(unidadeCurricularDAL);

    private final EstudanteBLL estudanteBLL = new EstudanteBLL(estudanteDAL);
    private final GestorBLL gestorBLL = new GestorBLL(gestorDAL);
    private final DepartamentoBLL departamentoBLL = new DepartamentoBLL(departamentoDAL);
    private final CursoBLL cursoBLL = new CursoBLL(cursoDAL);
    private final DocenteBLL docenteBLL = new DocenteBLL(docenteDAL);
    private final UnidadeCurricularBLL unidadeCurricularBLL = new UnidadeCurricularBLL(unidadeCurricularDAL);
    private final AvaliacaoBLL avaliacaoBLL = new AvaliacaoBLL(avaliacaoDAL);

    private final EstudanteController estudanteController = new EstudanteController(estudanteBLL);
    private final GestorController gestorController = new GestorController(gestorBLL);
    private final DepartamentoController departamentoController = new DepartamentoController(departamentoBLL);
    private final CursoController cursoController = new CursoController(cursoBLL);
    private final DocenteController docenteController = new DocenteController(docenteBLL);
    private final UnidadeCurricularController unidadeCurricularController = new UnidadeCurricularController(unidadeCurricularBLL);
    private final AvaliacaoController avaliacaoController = new AvaliacaoController(avaliacaoBLL);
    private final InscricaoController inscricaoController = new InscricaoController(estudanteBLL);

    private final Scanner scanner = new Scanner(System.in);

    // ── Ponto de entrada ──────────────────────────────────────────────────────

    public void iniciar() {
        String[] opcoes = {"Login"};
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ISSMF - PORTAL", opcoes, scanner);
            if (opcao == 1) efetuarLogin();
        } while (opcao != 0);

        System.out.println("\n  Até breve!");
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    private void efetuarLogin() {
        Utils.limparEcra();
        System.out.print("\n--- Login ---\n  E-mail: ");
        String email = scanner.nextLine().trim();
        System.out.print("  Palavra-passe: ");
        String password = lerPassword();

        String prefixo = extrairPrefixo(email);
        if (prefixo == null) {
            System.out.println("  [!] Formato de e-mail inválido. Use xxxxx@issmf.pt");
            Utils.pausar(scanner);
            return;
        }

        try {
            if (prefixo.equalsIgnoreCase("gestor")) {
                Gestor gestor = gestorController.autenticar(email, password);
                if (gestor.isPrimeiroLogin()) {
                    tratarPrimeiroLoginGestor(gestor);
                }
                new GestorView(gestorController, estudanteController, docenteController, departamentoController, cursoController, unidadeCurricularController, avaliacaoController, inscricaoController, scanner).iniciar(gestor);

            } else if (prefixo.matches("[A-Za-z]{3}")) {
                Docente docente = docenteController.autenticar(email, password);
                if (docente.isPrimeiroLogin()) {
                    tratarPrimeiroLoginDocente(docente);
                }
                new DocenteView(docenteController, estudanteController, avaliacaoController, unidadeCurricularController, scanner).iniciar(docente);

            } else if (prefixo.matches("\\d+")) {
                Estudante estudante = estudanteController.autenticarEstudante(email, password);
                if (estudante.isPrimeiroLogin()) {
                    tratarPrimeiroLoginEstudante(estudante);
                }
                new EstudanteView(estudanteController, scanner).iniciar(estudante);

            } else {
                System.out.println("  [!] Tipo de utilizador não reconhecido.");
                Utils.pausar(scanner);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("  [!] " + e.getMessage());
            Utils.pausar(scanner);
        }
    }


    private void tratarPrimeiroLoginGestor(Gestor gestor) {
        System.out.println("\n  [!] É o seu primeiro acesso. Deve alterar a sua password.");
        boolean alterada = false;
        while (!alterada) {
            try {
                String novaPassword = pedirNovaPassword();
                gestorController.alterarPassword(gestor, novaPassword);
                System.out.println("  [✓] Password alterada com sucesso!");
                alterada = true;
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }

    private void tratarPrimeiroLoginDocente(Docente docente) {
        System.out.println("\n  [!] É o seu primeiro acesso. Deve alterar a sua password.");
        boolean alterada = false;
        while (!alterada) {
            try {
                String novaPassword = pedirNovaPassword();
                docenteController.alterarPassword(docente, novaPassword);
                System.out.println("  [✓] Password alterada com sucesso!");
                alterada = true;
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }

    private void tratarPrimeiroLoginEstudante(Estudante estudante) {
        System.out.println("\n  [!] É o seu primeiro acesso. Deve alterar a sua password.");
        boolean alterada = false;
        while (!alterada) {
            try {
                String novaPassword = pedirNovaPassword();
                estudanteController.alterarPassword(estudante, novaPassword);
                System.out.println("  [✓] Password alterada com sucesso!");
                alterada = true;
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String pedirNovaPassword() {
        System.out.print("  Nova password     : ");
        String p1 = scanner.nextLine().trim();
        System.out.print("  Confirmar password: ");
        String p2 = scanner.nextLine().trim();
        if (!p1.equals(p2)) throw new IllegalArgumentException("As passwords não coincidem. Tente novamente.");
        return p1;
    }

    private String extrairPrefixo(String email) {
        if (email == null || !email.toLowerCase().endsWith("@issmf.pt")) return null;
        return email.substring(0, email.indexOf('@'));
    }

    private String lerPassword() {
        if (System.console() != null) {
            char[] chars = System.console().readPassword();
            return chars != null ? new String(chars) : "";
        }
        return scanner.nextLine().trim();
    }
}
