package View;

import Controller.*;
import Model.Docente;
import Model.Estudante;
import Model.Gestor;
import Utils.Utils;

import java.util.Scanner;

public class LoginView {

    private final LoginController loginController;
    private final Scanner scanner;
    private boolean vemDeRecuperacao = false;


    public LoginView() {
        this.loginController = new LoginController();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        String[] opcoes = {
                "Login",
                "Recuperar Password"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ISSMF - PORTAL", opcoes, scanner);
            switch (opcao) {
                case 1:
                    efetuarLogin();
                    break;
                case 2:
                    recuperarPassword();
                    break;
                case 0:
                    break;
            }
        } while (opcao != 0);

        System.out.println("\n  Até breve!");
    }


    private void efetuarLogin() {
        Utils.tituloPagina("ISSMF", "Login");
        System.out.print("  E-mail: ");
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
                Gestor gestor = loginController.getGestorController().autenticar(email, password);
                if (gestor.isPrimeiroLogin()) {
                    tratarPrimeiroLoginGestor(gestor);
                }
                new GestorView(
                        loginController.getGestorController(),
                        loginController.getEstudanteController(),
                        loginController.getDocenteController(),
                        loginController.getDepartamentoController(),
                        loginController.getCursoController(),
                        loginController.getUnidadeCurricularController(),
                        loginController.getAvaliacaoController(),
                        loginController.getInscricaoController(),
                        loginController.getAnoLetivoController(),
                        loginController.getHorarioController(),
                        loginController.getJustificacaoController(),
                        scanner).iniciar(gestor);

            } else if (prefixo.matches("[A-Za-z]{3}")) {
                Docente docente = loginController.getDocenteController().autenticar(email, password);
                if (docente.isPrimeiroLogin()) {
                    tratarPrimeiroLoginDocente(docente);
                }
                new DocenteView(
                        loginController.getDocenteController(),
                        loginController.getEstudanteController(),
                        loginController.getAvaliacaoController(),
                        loginController.getUnidadeCurricularController(),
                        loginController.getAnoLetivoController(),
                        loginController.getCursoController(),
                        loginController.getHorarioController(),
                        loginController.getPresencaController(),
                        scanner).iniciar(docente);

            } else if (prefixo.matches("\\d+")) {
                Estudante estudante = loginController.getEstudanteController().autenticarEstudante(email, password);
                if (estudante.isPrimeiroLogin()) {
                    tratarPrimeiroLoginEstudante(estudante);
                }
                new EstudanteView(
                        loginController.getEstudanteController(),
                        loginController.getInscricaoController(),
                        loginController.getHorarioController(),
                        loginController.getPresencaController(),
                        loginController.getJustificacaoController(),
                        loginController.getAnoLetivoController(),
                        loginController.getCursoController(),
                        scanner).iniciar(estudante);

            } else {
                System.out.println("  [!] Tipo de utilizador não reconhecido.");
                Utils.pausar(scanner);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("  [!] " + e.getMessage());
            Utils.pausar(scanner);
        } finally {
            vemDeRecuperacao = false;
        }
    }

    private void recuperarPassword() {
        Utils.tituloPagina("ISSMF", "Recuperar Password");
        System.out.println("  Introduza o seu e-mail institucional (@issmf.pt).");
        System.out.print("  E-mail: ");
        String email = scanner.nextLine().trim();

        String prefixo = extrairPrefixo(email);
        if (prefixo == null) {
            System.out.println("  [!] Formato de e-mail inválido. Use xxxxx@issmf.pt");
            Utils.pausar(scanner);
            return;
        }

        try {
            if (prefixo.equalsIgnoreCase("gestor")) {
                loginController.getGestorController().recuperarPassword(email);
            } else if (prefixo.matches("[A-Za-z]{3}")) {
                loginController.getDocenteController().recuperarPassword(email);
            } else if (prefixo.matches("\\d+")) {
                loginController.getEstudanteController().recuperarPassword(email);
            } else {
                System.out.println("  [!] Tipo de utilizador não reconhecido.");
                Utils.pausar(scanner);
                return;
            }

            vemDeRecuperacao = true;

            System.out.println("  [✓] Foi enviada uma password temporária para " + email + ".");
            System.out.println("  Use-a para fazer login e defina uma nova password.");
        } catch (IllegalArgumentException e) {
            System.out.println("  [!] " + e.getMessage());
        }

        Utils.pausar(scanner);
    }


    private void tratarPrimeiroLoginGestor(Gestor gestor) {
        mostrarMensagemPrimeiroAcesso();
        boolean alterada = false;
        while (!alterada) {
            try {
                String novaPassword = pedirNovaPassword();
                loginController.getGestorController().alterarPassword(gestor, novaPassword);
                System.out.println("  [✓] Password alterada com sucesso!");
                alterada = true;
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }

    private void tratarPrimeiroLoginDocente(Docente docente) {
        mostrarMensagemPrimeiroAcesso();
        boolean alterada = false;
        while (!alterada) {
            try {
                String novaPassword = pedirNovaPassword();
                loginController.getDocenteController().alterarPassword(docente, novaPassword);
                System.out.println("  [✓] Password alterada com sucesso!");
                alterada = true;
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }

    private void tratarPrimeiroLoginEstudante(Estudante estudante) {
        mostrarMensagemPrimeiroAcesso();
        boolean alterada = false;
        while (!alterada) {
            try {
                String novaPassword = pedirNovaPassword();
                loginController.getEstudanteController().alterarPassword(estudante, novaPassword);
                System.out.println("  [✓] Password alterada com sucesso!");
                alterada = true;
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }


    private String pedirNovaPassword() {
        System.out.print("  Nova password: ");
        String p1 = lerPassword();
        System.out.print("  Confirmar password: ");
        String p2 = lerPassword();
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

    private void mostrarMensagemPrimeiroAcesso() {
        if (vemDeRecuperacao) {
            System.out.println("\n  [!] A sua password foi redefinida.");
            System.out.println("      Defina agora uma nova password para continuar.");
        } else {
            System.out.println("\n  [!] É o seu primeiro acesso. Deve alterar a sua password.");
        }
    }
}