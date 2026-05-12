package View;

import Controller.*;
import Model.Docente;
import Model.Estudante;
import Model.Gestor;
import Utils.Utils;

import java.util.Scanner;

public class LoginView {

    private final EstudanteController         estudanteController;
    private final GestorController            gestorController;
    private final DocenteController           docenteController;
    private final DepartamentoController      departamentoController;
    private final CursoController             cursoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final AvaliacaoController         avaliacaoController;
    private final InscricaoController         inscricaoController;
    private final AnoLetivoController anoLetivoController;
    private final Scanner                     scanner;
    private boolean vemDeRecuperacao = false;


    public LoginView() {
        this.gestorController            = new GestorController();
        this.estudanteController         = new EstudanteController();
        this.docenteController           = new DocenteController();
        this.departamentoController      = new DepartamentoController();
        this.cursoController             = new CursoController();
        this.unidadeCurricularController = new UnidadeCurricularController();
        this.avaliacaoController         = new AvaliacaoController();
        this.inscricaoController         = new InscricaoController();
        this.anoLetivoController = new AnoLetivoController();
        this.scanner                     = new Scanner(System.in);
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
                case 1: efetuarLogin(); break;
                case 2: recuperarPassword(); break;
                case 0: break;
            }
        } while (opcao != 0);

        System.out.println("\n  Até breve!");
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    private void efetuarLogin() {
        Utils.limparEcra();
        System.out.print("\n--- Login ---\n  E-mail: ");
        String email = scanner.nextLine().trim();
        System.out.print("Palavra-passe: ");
        String password = lerPassword();

        String prefixo = extrairPrefixo(email);
        if (prefixo == null) {
            System.out.println("[!] Formato de e-mail inválido. Use xxxxx@issmf.pt");
            Utils.pausar(scanner);
            return;
        }

        try {
            if (prefixo.equalsIgnoreCase("gestor")) {
                Gestor gestor = gestorController.autenticar(email, password);
                if (gestor.isPrimeiroLogin()) {
                    tratarPrimeiroLoginGestor(gestor);
                }
                new GestorView(gestorController, estudanteController, docenteController, departamentoController, cursoController, unidadeCurricularController, avaliacaoController, inscricaoController, anoLetivoController, scanner).iniciar(gestor);

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
                System.out.println("[!] Tipo de utilizador não reconhecido.");
                Utils.pausar(scanner);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("[!] " + e.getMessage());
            Utils.pausar(scanner);
        } finally {
            vemDeRecuperacao = false;
        }
    }

    private void recuperarPassword() {
        Utils.limparEcra();
        System.out.println("\n--- Recuperar Password ---");
        System.out.println("Introduza o seu e-mail institucional (@issmf.pt).");
        System.out.print("E-mail: ");
        String email = scanner.nextLine().trim();

        String prefixo = extrairPrefixo(email);
        if (prefixo == null) {
            System.out.println("[!] Formato de e-mail inválido. Use xxxxx@issmf.pt");
            Utils.pausar(scanner);
            return;
        }

        try {
            if (prefixo.equalsIgnoreCase("gestor")) {
                gestorController.recuperarPassword(email);
            } else if (prefixo.matches("[A-Za-z]{3}")) {
                docenteController.recuperarPassword(email);
            } else if (prefixo.matches("\\d+")) {
                estudanteController.recuperarPassword(email);
            } else {
                System.out.println("  [!] Tipo de utilizador não reconhecido.");
                Utils.pausar(scanner);
                return;
            }

            vemDeRecuperacao = true;  // marca que o próximo login vem de recuperação

            System.out.println(" [✓] Foi enviada uma password temporária para " + email + ".");
            System.out.println("Use-a para fazer login e defina uma nova password.");
        } catch (IllegalArgumentException e) {
            System.out.println("[!] " + e.getMessage());
        }

        Utils.pausar(scanner);
    }


    private void tratarPrimeiroLoginGestor(Gestor gestor) {
        mostrarMensagemPrimeiroAcesso();
        boolean alterada = false;
        while (!alterada) {
            try {
                String novaPassword = pedirNovaPassword();
                gestorController.alterarPassword(gestor, novaPassword);
                System.out.println("[✓] Password alterada com sucesso!");
                alterada = true;
            } catch (IllegalArgumentException e) {
                System.out.println("[!] " + e.getMessage());
            }
        }
    }

    private void tratarPrimeiroLoginDocente(Docente docente) {
        mostrarMensagemPrimeiroAcesso();
        boolean alterada = false;
        while (!alterada) {
            try {
                String novaPassword = pedirNovaPassword();
                docenteController.alterarPassword(docente, novaPassword);
                System.out.println("[✓] Password alterada com sucesso!");
                alterada = true;
            } catch (IllegalArgumentException e) {
                System.out.println("[!] " + e.getMessage());
            }
        }
    }

    private void tratarPrimeiroLoginEstudante(Estudante estudante) {
        mostrarMensagemPrimeiroAcesso();
        boolean alterada = false;
        while (!alterada) {
            try {
                String novaPassword = pedirNovaPassword();
                estudanteController.alterarPassword(estudante, novaPassword);
                System.out.println("[✓] Password alterada com sucesso!");
                alterada = true;
            } catch (IllegalArgumentException e) {
                System.out.println("[!] " + e.getMessage());
            }
        }
    }



    private String pedirNovaPassword() {
        System.out.print("Nova password: ");
        String p1 = scanner.nextLine().trim();
        System.out.print("Confirmar password: ");
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

    private void mostrarMensagemPrimeiroAcesso() {
        if (vemDeRecuperacao) {
            System.out.println("\n  [!] A sua password foi redefinida.");
            System.out.println("      Defina agora uma nova password para continuar.");
        } else {
            System.out.println("\n  [!] É o seu primeiro acesso. Deve alterar a sua password.");
        }
    }
}