package View;

import Controller.*;
import Model.Docente;
import Model.Estudante;
import Model.Gestor;
import Utils.Utils;

import java.util.Scanner;

public class LoginView {
    private EstudanteController estudanteController;
    private DocenteController docenteController;
    private GestorController gestorController;
    private DepartamentoController departamentoController;
    private CursoController cursoController;
    private UnidadeCurricularController unidadeCurricularController;
    private AvaliacaoController avaliacaoController;
    private Scanner scanner;

    public LoginView(EstudanteController estudanteController, DocenteController docenteController, GestorController gestorController, DepartamentoController departamentoController, CursoController cursoController, UnidadeCurricularController unidadeCurricularController, AvaliacaoController avaliacaoController, Scanner scanner) {
        this.estudanteController = estudanteController;
        this.docenteController = docenteController;
        this.gestorController = gestorController;
        this.departamentoController = departamentoController;
        this.cursoController = cursoController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.avaliacaoController = avaliacaoController;
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

                if (gestor.isPrimeiroLogin()) {
                    System.out.println("\n [!] É o seu primeiro acesso. Deve alterar a sua password.");
                    String novaPassword = null;
                    while (novaPassword == null) {
                        System.out.print(" Nova password    : ");
                        String p1 = scanner.nextLine().trim();
                        System.out.print(" Confirmar password: ");
                        String p2 = scanner.nextLine().trim();
                        if (!p1.equals(p2)) {
                            System.out.println(" [!] As passwords não coincidem. Tente novamente.");
                        } else {
                            try {
                                gestorController.alterarPassword(gestor, p1);
                                novaPassword = p1;
                                System.out.println(" [✓] Password alterada com sucesso!");
                            } catch (IllegalArgumentException e) {
                                System.out.println(" [!] " + e.getMessage());
                            }
                        }
                    }
                }

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

                if (docente.isPrimeiroLogin()) {
                    System.out.println("\n [!] É o seu primeiro acesso. Deve alterar a sua password.");
                    String novaPassword = null;
                    while (novaPassword == null) {
                        System.out.print(" Nova password    : ");
                        String p1 = scanner.nextLine().trim();
                        System.out.print(" Confirmar password: ");
                        String p2 = scanner.nextLine().trim();
                        if (!p1.equals(p2)) {
                            System.out.println(" [!] As passwords não coincidem. Tente novamente.");
                        } else {
                            try {
                                docenteController.alterarPassword(docente, p1);
                                novaPassword = p1;
                                System.out.println(" [✓] Password alterada com sucesso!");
                            } catch (IllegalArgumentException e) {
                                System.out.println(" [!] " + e.getMessage());
                            }
                        }
                    }
                }

                new DocenteView(docenteController, scanner).iniciar(docente);

            } else if (prefixo.matches("\\d+")) {
                Estudante estudante = estudanteController.autenticarEstudante(email, password);

                if (estudante.isPrimeiroLogin()) {
                    System.out.println("\n [!] É o seu primeiro acesso. Deve alterar a sua password.");
                    String novaPassword = null;
                    while (novaPassword == null) {
                        System.out.print(" Nova password    : ");
                        String p1 = scanner.nextLine().trim();
                        System.out.print(" Confirmar password: ");
                        String p2 = scanner.nextLine().trim();
                        if (!p1.equals(p2)) {
                            System.out.println(" [!] As passwords não coincidem. Tente novamente.");
                        } else {
                            try {
                                estudanteController.alterarPassword(estudante, p1);
                                novaPassword = p1;
                                System.out.println(" [✓] Password alterada com sucesso!");
                            } catch (IllegalArgumentException e) {
                                System.out.println(" [!] " + e.getMessage());
                            }
                        }
                    }
                }

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