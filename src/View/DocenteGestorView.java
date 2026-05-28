package View;

import Controller.DocenteController;
import Controller.EstudanteController;
import Model.Docente;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DocenteGestorView {

    private final DocenteController docenteController;
    private final EstudanteController estudanteController;
    private final Scanner scanner;

    public DocenteGestorView(DocenteController docenteController, EstudanteController estudanteController, Scanner scanner) {
        this.docenteController = docenteController;
        this.estudanteController = estudanteController;
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
        String nome = Utils.lerNome("Nome: ", scanner);
        LocalDate data = Utils.lerDataNascimento("Data de nascimento (AAAA-MM-DD): ", scanner);

        String nif;
        while (true) {
            nif = Utils.lerNif("NIF: ", scanner);
            if (!docenteController.nifJaExiste(nif) && !estudanteController.nifJaExiste(nif)) break;
            System.out.println("  [!] Este NIF já existe no sistema.");
        }

        String morada = Utils.lerMorada("Morada: ", scanner);
        // Gerar sigla automaticamente com base nas iniciais do nome
        java.util.List<String> siglasExistentes = new java.util.ArrayList<>();
        for (Docente doc : docenteController.listarDocentes()) {
            siglasExistentes.add(doc.getSigla().toLowerCase());
        }
        String siglaGerada = Utils.gerarSigla(nome, siglasExistentes);

        System.out.println("  Sigla sugerida: " + siglaGerada);

        Docente d = new Docente(nome, data, nif, morada, siglaGerada, new ArrayList<>());
        docenteController.registarDocente(d);

        System.out.println("  [✓] Docente registado com sucesso.");
        System.out.println("  Sigla: " + siglaGerada);
        System.out.println("  Email: " + siglaGerada + "@issmf.pt");
        System.out.println("  Password inicial: enviada por email para " + siglaGerada + "@issmf.pt");
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
        System.out.println("\n" + d.toStringDetalhado());
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