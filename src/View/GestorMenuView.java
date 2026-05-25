package View;

import Controller.GestorController;
import Model.Gestor;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

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
        System.out.println("\n--- Registar Gestor --- (0 para cancelar)");
        String nome = Utils.lerNome("Nome: ", scanner);
        LocalDate data = Utils.lerDataNascimento("Data de nascimento (AAAA-MM-DD): ", scanner);
        String nif = Utils.lerNif("NIF: ", scanner);
        String morada = Utils.lerMorada("Morada: ", scanner);
        String email = Utils.lerEmail("Email (@issmf.pt): ", scanner);
        String password = Utils.lerPassword("Password: ", scanner);

        gestorController.registarGestor(nome, data, nif, morada, email, password);
        System.out.println("  [✓] Gestor registado com sucesso.");
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.limparEcra();
        System.out.println("\n--- Lista de Gestores ---");
        ArrayList<Gestor> gestores = gestorController.listarGestores();
        if (gestores.isEmpty()) { System.out.println("  (sem gestores registados)"); Utils.pausar(scanner); return; }
        for (Gestor g : gestores) { System.out.println(g + "\n"); }
        Utils.pausar(scanner);
    }

    private void procurar() {
        Utils.limparEcra();
        System.out.print("\nNIF do gestor: ");
        String nif = scanner.nextLine().trim();
        Gestor g = gestorController.procurarPorNif(nif);
        if (g == null) { System.out.println("  [!] Gestor não encontrado."); Utils.pausar(scanner); return; }
        System.out.println("\n" + g.toStringDetalhado());
        Utils.pausar(scanner);
    }

    private void atualizar() {
        System.out.println("\n--- Atualizar Gestor --- (0 para cancelar)");
        System.out.print("NIF do gestor a atualizar: ");
        String nif = scanner.nextLine().trim();
        Gestor g = gestorController.procurarPorNif(nif);
        if (g == null) { System.out.println("  [!] Gestor não encontrado."); Utils.pausar(scanner); return; }

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
        System.out.print("\nNIF do gestor a remover: ");
        String nif = scanner.nextLine().trim();
        gestorController.removerGestor(nif);
        System.out.println("  [✓] Gestor removido com sucesso.");
        Utils.pausar(scanner);
    }
}