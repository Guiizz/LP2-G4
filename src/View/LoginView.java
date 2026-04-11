package View;


import Controller.DocenteController;
import Controller.EstudanteController;
import Controller.GestorController;
import Model.Docente;
import Model.Estudante;
import Model.Gestor;
import Utils.Utils;

import java.util.Scanner;

public class LoginView {
    private EstudanteController estudanteController;
    private DocenteController docenteController;
    private GestorController gestorController;
    private Scanner scanner;

    public LoginView(EstudanteController estudanteController, DocenteController docenteController, GestorController gestorController, Scanner scanner) {
        this.estudanteController = estudanteController;
        this.docenteController = docenteController;
        this.gestorController = gestorController;
        this.scanner = scanner;
    }

    /**
     * Ponto de entrada da aplicação.
     * Apresenta o menu principal e gere o fluxo de login/saída.
     */
    public void iniciar(){
        String[] opcoes = {"Login"};
        int opcao;
        do {
            opcao = Utils.mostrarMenu("ISSMF - PORTAL", opcoes, scanner);
            if (opcao == 1){
                efetuarLogin();
            }
        }while (opcao != 0);
        System.out.printf("\n Até breve!");
    }

    /**
     * Lê as credenciais, deteta o tipo de utilizador pelo prefixo do e-mail
     * e redireciona para a View correspondente.
     */

    private void efetuarLogin(){
        System.out.printf("\n--- Login ---");
        System.out.printf(" E-mail: ");
        String email = scanner.nextLine().trim();

        System.out.printf(" Palavra-passe: ");
        String password = lerPassword();

        String prefixo = extrairPrefixo(email);
        if (prefixo == null){
            System.out.println(" [!] Formato de e-mail inválido. Use o formato xxxxx@issmf.pt");
            return;
        }

        try {
            if (prefixo.equals("gestor")){
                Gestor gestor = gestorController.autenticar(email,password);
                new GestorView(gestorController, estudanteController, docenteController, scanner).iniciar(gestor);

            }else if (prefixo.matches("[A-Za-z]{3}")){
                Docente docente = docenteController.autenticar(email, password);
                new DocenteView(docenteController, scanner).iniciar(docente);

            } else if (prefixo.matches("\\d+")) {
                Estudante estudante = estudanteController.autenticarEstudante(email, password);
                new EstudanteView(estudanteController, scanner).iniciar(estudante);

            } else {
                System.out.println("  [!] Tipo de utilizador não reconhecido.");
            }
        }catch (IllegalArgumentException e){
            System.out.println(" [!] " + e.getMessage());
        }
    }
    /**
     * Extrai o prefixo antes de "@issmf.pt".
     * @return O prefixo, ou null se o formato for inválido.
     */
    private String extrairPrefixo(String email) {
        if (email == null || !email.toLowerCase().endsWith("@issmf.pt")) {
            return null;
        }
        return email.substring(0, email.indexOf('@'));
    }

    /** Lê a password sem eco no terminal real; fallback para IDE. */
    private String lerPassword() {
        if (System.console() != null) {
            return new String(System.console().readPassword());
        }
        return scanner.nextLine().trim();
    }
}
