package View;

import Controller.DocenteController;
import Model.Docente;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class DocenteGestorView {

    private final DocenteController docenteController;
    private final Scanner scanner;

    public DocenteGestorView(DocenteController docenteController, Scanner scanner) {
        this.docenteController = docenteController;
        this.scanner = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Registar Docente",
                "Listar Docentes",
                "Procurar Docente por Sigla",
                "Atualizar Docente",
                "Remover Docente"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("GESTÃO DE DOCENTES", opcoes, scanner);
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



    private void registar() {
        System.out.println("\n--- Registar Docente --- (0 para cancelar)");
        String nome = Utils.lerCampo("Nome: ", scanner);
        LocalDate data = Utils.lerData("Data de nascimento (AAAA-MM-DD): ", scanner);
        String nif = Utils.lerCampo("NIF: ", scanner);
        String morada = Utils.lerCampo("Morada: ", scanner);
        String sigla = Utils.lerCampo("Sigla: ", scanner);

        Docente d = new Docente(nome, data, nif, morada, sigla, new ArrayList<>());
        Docente resultado = docenteController.registarDocente(d);
        System.out.println("  [✓] Docente registado com sucesso.");
        System.out.println("  Sigla: " + resultado.getSigla());
        System.out.println("  E-mail: " + resultado.getEmail());
        System.out.println("  Password inicial: Issmf" + resultado.getSigla());
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.limparEcra();
        System.out.println("\n--- Lista de Docentes ---");
        ArrayList<Docente> lista = docenteController.listarDocentes();
        if (lista.isEmpty()) { System.out.println("  (sem docentes registados)"); Utils.pausar(scanner); return; }
        for (Docente d : lista) { System.out.println(d + "\n"); }
        Utils.pausar(scanner);
    }

    private void procurar() {
        Utils.limparEcra();
        System.out.print("\nSigla do docente: ");
        String sigla = scanner.nextLine().trim();
        Docente d = docenteController.procurarPorSigla(sigla);
        if (d == null) { System.out.println("  [!] Docente não encontrado."); Utils.pausar(scanner); return; }
        System.out.println("\n" + d);
        Utils.pausar(scanner);
    }

    private void atualizar() {
        System.out.println("\n--- Atualizar Docente --- (0 para cancelar)");
        System.out.print("Sigla do docente a atualizar: ");
        String sigla = scanner.nextLine().trim();
        Docente d = docenteController.procurarPorSigla(sigla);
        if (d == null) { System.out.println("  [!] Docente não encontrado."); Utils.pausar(scanner); return; }

        System.out.println("  Dados atuais: " + d.getNome() + " | " + d.getMorada());
        String novoNome = Utils.lerCampo("Novo nome (Enter para manter): ", scanner);
        String novaMorada = Utils.lerCampo("Nova morada (Enter para manter): ", scanner);

        if (!novoNome.isEmpty()) d.setNome(novoNome);
        if (!novaMorada.isEmpty()) d.setMorada(novaMorada);

        docenteController.atualizarDocente(d);
        System.out.println("  [✓] Docente atualizado com sucesso.");
        Utils.pausar(scanner);
    }

    private void remover() {
        System.out.print("\nSigla do docente a remover: ");
        String sigla = scanner.nextLine().trim();
        docenteController.removerDocente(sigla);
        System.out.println("  [✓] Docente removido com sucesso.");
        Utils.pausar(scanner);
    }
}