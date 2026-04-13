package View;

import Controller.CursoController;
import Controller.DepartamentoController;
import Controller.DocenteController;
import Controller.EstudanteController;
import Controller.GestorController;
import Controller.UnidadeCurricularController;
import Model.Docente;
import Model.Estudante;
import Model.Gestor;
import Utils.Utils;

import java.util.Scanner;

public class LoginView {
    private final EstudanteController estudanteController;
    private final DocenteController docenteController;
    private final GestorController gestorController;
    private final DepartamentoController departamentoController;
    private final CursoController cursoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final Scanner scanner;

    public LoginView(EstudanteController estudanteController,
                     DocenteController docenteController,
                     GestorController gestorController,
                     DepartamentoController departamentoController,
                     CursoController cursoController,
                     UnidadeCurricularController unidadeCurricularController,
                     Scanner scanner) {
        this.estudanteController = estudanteController;
        this.docenteController = docenteController;
        this.gestorController = gestorController;
        this.departamentoController = departamentoController;
        this.cursoController = cursoController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.scanner = scanner;
    }

    public void iniciar() {
        String[] opcoes = {"Login"};
        int opcao;
        do {
            opcao = Utils.mostrarMenu("ISSMF - PORTAL", opcoes, scanner);
            if (opcao == 1) {
                efetuarLogin();
            }
        } while (opcao != 0);

        System.out.println("\n Até breve!");
    }

    private void efetuarLogin() {
        System.out.print("\n--- Login ---\n E-mail: ");
        String email = scanner.nextLine().trim();

        System.out.print(" Palavra-passe: ");
        String password = lerPassword();

        String prefixo = extrairPrefixo(email);
        if (prefixo == null) {
            System.out.println(" [!] Formato de e-mail inválido. Use o formato xxxxx@issmf.pt");
            return;
        }

        try {
            if (prefixo.equalsIgnoreCase("gestor")) {
                Gestor gestor = gestorController.autenticar(email, password);
                new GestorView(
                        gestorController,
                        estudanteController,
                        docenteController,
                        departamentoController,
                        cursoController,
                        unidadeCurricularController,
                        scanner
                ).iniciar(gestor);

            } else if (prefixo.matches("[A-Za-z]{3}")) {
                Docente docente = docenteController.autenticar(email, password);
                new DocenteView(docenteController, scanner).iniciar(docente);

            } else if (prefixo.matches("\\d+")) {
                Estudante estudante = estudanteController.autenticarEstudante(email, password);
                new EstudanteView(estudanteController, scanner).iniciar(estudante);

            } else {
                System.out.println("  [!] Tipo de utilizador não reconhecido.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(" [!] " + e.getMessage());
        }
    }

    private String extrairPrefixo(String email) {
        if (email == null || !email.toLowerCase().endsWith("@issmf.pt")) {
            return null;
        }
        return email.substring(0, email.indexOf('@'));
    }

    private String lerPassword() {
        if (System.console() != null) {
            return new String(System.console().readPassword());
        }
        return scanner.nextLine().trim();
    }
}