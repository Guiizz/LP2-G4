package View;

import Controller.GestorController;
import Model.Gestor;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import static Utils.Utils.lerPassword;

public class GestorMenuView {

    private final GestorController gestorController;
    private final Scanner scanner;

    public GestorMenuView(GestorController gestorController, Scanner scanner) {
        this.gestorController = gestorController;
        this.scanner = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Registar Gestor",
                "Listar Gestores",
                "Procurar Gestor por NIF",
                "Atualizar Gestor",
                "Remover Gestor"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("GESTÃO DE GESTORES", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: registar(); break;
                    case 2: listar(); break;
                    case 3: procurar(); break;
                    case 4: atualizar(); break;
                    case 5: remover(); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    // ── Ações ─────────────────────────────────────────────────────────────────

    private void registar() {
        Utils.tituloPagina("Registar Gestor");
        String nome = Utils.lerNome("Nome: ", scanner);
        LocalDate data = Utils.lerDataNascimento("Data de nascimento (AAAA-MM-DD): ", scanner);
        String nif = Utils.lerNif("NIF: ", scanner);
        String morada = Utils.lerMorada("Morada: ", scanner);
        String email = Utils.lerEmail("Email (@issmf.pt): ", scanner);
        System.out.print("Password: ");
        String password = lerPassword();

        gestorController.registarGestor(nome, data, nif, morada, email, password);
        System.out.println("  [✓] Gestor registado com sucesso.");
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.limparEcra();
        Utils.tituloPagina("Lista de Gestores");
        ArrayList<Gestor> gestores = gestorController.listarGestores();
        if (gestores.isEmpty()) { System.out.println("  (sem gestores registados)"); Utils.pausar(scanner); return; }
        for (Gestor g : gestores) { System.out.println(g + "\n"); }
        Utils.pausar(scanner);
    }

    private void procurar() {
        Utils.limparEcra();
        Utils.tituloPagina("Procurar Gestor");
        Gestor g = selecionarGestor();
        if (g == null) return;
        System.out.println("\n" + g.toStringDetalhado());
        Utils.pausar(scanner);
    }

    private void atualizar() {
        Utils.tituloPagina("Atualizar Gestor");
        Gestor g = selecionarGestor();
        if (g == null) return;

        System.out.println("  Dados atuais: " + g.getNome() + " | " + g.getMorada());
        String novoNome = Utils.lerCampo("Novo nome (Enter para manter): ", scanner);
        String novaMorada = Utils.lerCampo("Nova morada (Enter para manter): ", scanner);

        if (!novoNome.isEmpty()) g.setNome(novoNome);
        if (!novaMorada.isEmpty()) g.setMorada(novaMorada);

        gestorController.atualizarGestor(g);
        System.out.println("  [✓] Gestor atualizado com sucesso.");
        Utils.pausar(scanner);
    }

    private void remover() {
        Utils.tituloPagina("Remover Gestor");
        Gestor g = selecionarGestor();
        if (g == null) return;

        String confirmar = Utils.lerCampo("  Tem a certeza que deseja remover o gestor '" + g.getNome() + "'? (S/N): ", scanner);
        if (!confirmar.equalsIgnoreCase("S")) {
            System.out.println("  Operação cancelada.");
            Utils.pausar(scanner);
            return;
        }
        gestorController.removerGestor(g.getNif());
        System.out.println("  [✓] Gestor removido com sucesso.");
        Utils.pausar(scanner);
    }

    private Gestor selecionarGestor() {
        ArrayList<Gestor> lista = gestorController.listarGestores();
        if (lista.isEmpty()) {
            System.out.println("  [!] Não existem gestores registados.");
            Utils.pausar(scanner);
            return null;
        }
        System.out.println("\n  Gestores registados:");
        for (int i = 0; i < lista.size(); i++) {
            Gestor g = lista.get(i);
            System.out.println("  " + (i + 1) + ". " + g.getNome() + " (" + g.getNif() + ") — " + g.getEmail());
        }
        int escolha = Utils.lerInteiro("  Selecione (0 para voltar): ", scanner);
        if (escolha == 0) return null;
        if (escolha < 1 || escolha > lista.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return null;
        }
        return lista.get(escolha - 1);
    }

    private String lerPassword() {
        if (System.console() != null) {
            char[] chars = System.console().readPassword();
            return chars != null ? new String(chars) : "";
        }
        return scanner.nextLine().trim();
    }
}