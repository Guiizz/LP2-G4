package View;

import Controller.*;
import Model.Docente;
import Model.Estudante;
import Model.Gestor;
import Utils.Utils;

import java.util.Scanner;

public class LoginView {

    private final EstudanteController estudanteController;
    private final GestorController gestorController;
    private final DocenteController  docenteController;
    private final DepartamentoController departamentoController;
    private final CursoController cursoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final AvaliacaoController avaliacaoController;
    private final InscricaoController inscricaoController;
    private final Scanner scanner;

    public LoginView(GestorController gestorController, EstudanteController estudanteController, DocenteController docenteController, DepartamentoController departamentoController, CursoController cursoController, UnidadeCurricularController unidadeCurricularController, AvaliacaoController avaliacaoController, InscricaoController inscricaoController, Scanner scanner) {
        this.gestorController = gestorController;
        this.estudanteController = estudanteController;
        this.docenteController = docenteController;
        this.departamentoController = departamentoController;
        this.cursoController = cursoController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.avaliacaoController = avaliacaoController;
        this.inscricaoController = inscricaoController;
        this.scanner = scanner;
    }

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
                if (gestor.isPrimeiroLogin()) tratarPrimeiroLoginGestor(gestor);
                new GestorView(gestorController, estudanteController, docenteController, departamentoController, cursoController, unidadeCurricularController, avaliacaoController, inscricaoController, scanner).iniciar(gestor);

            } else if (prefixo.matches("[A-Za-z]{3}")) {
                Docente docente = docenteController.autenticar(email, password);
                if (docente.isPrimeiroLogin()) tratarPrimeiroLoginDocente(docente);
                new DocenteView(docenteController, estudanteController, avaliacaoController, unidadeCurricularController, scanner).iniciar(docente);

            } else if (prefixo.matches("\\d+")) {
                Estudante estudante = estudanteController.autenticarEstudante(email, password);
                if (estudante.isPrimeiroLogin()) tratarPrimeiroLoginEstudante(estudante);
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

    private String pedirNovaPassword() {
        System.out.print("  Nova password : ");
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
