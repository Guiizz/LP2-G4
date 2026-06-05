package View;

import Controller.DepartamentoController;
import Model.Departamento;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Scanner;

public class DepartamentoView {

    private final DepartamentoController departamentoController;
    private final Scanner scanner;

    public DepartamentoView(DepartamentoController departamentoController, Scanner scanner) {
        this.departamentoController = departamentoController;
        this.scanner = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Registar Departamento",
                "Listar Departamentos",
                "Procurar Departamento por Sigla",
                "Atualizar Departamento",
                "Remover Departamento"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("GESTÃO DE DEPARTAMENTOS", opcoes, scanner);
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
        System.out.println("\n--- Registar Departamento --- (0 para cancelar)");
        String nome  = Utils.lerNome("Nome: ", scanner);
        String sigla = Utils.lerSigla("Sigla (3 Letras): ", scanner);

        Departamento d = departamentoController.registarDepartamento(nome, sigla);
        System.out.println("  [✓] Departamento '" + d.getNome() + "' registado com sucesso.");
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.limparEcra();
        System.out.println("\n--- Lista de Departamentos ---");
        ArrayList<Departamento> lista = departamentoController.listarDepartamentos();
        if (lista.isEmpty()) { System.out.println("  (sem departamentos registados)"); Utils.pausar(scanner); return; }
        for (Departamento d : lista) { System.out.println(d + "\n"); }
        Utils.pausar(scanner);
    }

    private void procurar() {
        Utils.limparEcra();
        System.out.println("\n--- Procurar Departamento ---");
        Departamento d = selecionarDepartamento();
        if (d == null) return;
        System.out.println("\n" + d);
        Utils.pausar(scanner);
    }

    private void atualizar() {
        System.out.println("\n--- Atualizar Departamento ---");
        Departamento d = selecionarDepartamento();
        if (d == null) return;
        System.out.println("  Dados atuais: " + d.getNome() + " (" + d.getSigla() + ")");
        String novoNome = Utils.lerCampo("Novo nome (Enter para manter): ", scanner);
        if (novoNome.isEmpty()) { System.out.println("  Operação cancelada."); Utils.pausar(scanner); return; }
        departamentoController.atualizarDepartamento(d, novoNome);
        System.out.println("  [✓] Departamento atualizado com sucesso.");
        Utils.pausar(scanner);
    }

    private void remover() {
        System.out.println("\n--- Remover Departamento ---");
        Departamento d = selecionarDepartamento();
        if (d == null) return;
        String confirmar = Utils.lerCampo("  Tem a certeza que deseja remover '" + d.getNome() + "'? (S/N): ", scanner);
        if (!confirmar.equalsIgnoreCase("S")) {
            System.out.println("  Operação cancelada.");
            Utils.pausar(scanner);
            return;
        }
        departamentoController.removerDepartamento(d);
        System.out.println("  [✓] Departamento removido com sucesso.");
        Utils.pausar(scanner);
    }

    private Departamento selecionarDepartamento() {
        ArrayList<Departamento> lista = departamentoController.listarDepartamentos();
        if (lista.isEmpty()) {
            System.out.println("  [!] Não existem departamentos registados.");
            Utils.pausar(scanner);
            return null;
        }
        System.out.println("\n  Departamentos disponíveis:");
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + lista.get(i).getNome()
                    + " (" + lista.get(i).getSigla() + ")");
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
}