package View;

import Controller.CursoController;
import Controller.DepartamentoController;
import Model.Curso;
import Model.Departamento;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Scanner;

public class DepartamentoView {

    private final DepartamentoController departamentoController;
    private final CursoController        cursoController;
    private final Scanner                scanner;

    public DepartamentoView(DepartamentoController departamentoController,
                            CursoController cursoController,
                            Scanner scanner) {
        this.departamentoController = departamentoController;
        this.cursoController        = cursoController;
        this.scanner                = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Registar Departamento",
                "Listar Departamentos",
                "Procurar Departamento",
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
                    case 2: listar();   break;
                    case 3: procurar(); break;
                    case 4: atualizar(); break;
                    case 5: remover();  break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void registar() {
        Utils.tituloPagina("DEPARTAMENTOS", "Registar Departamento");
        String nome  = Utils.lerNome("Nome: ", scanner);
        String sigla = Utils.lerSigla("Sigla (3 Letras): ", scanner);

        Departamento d = departamentoController.registarDepartamento(nome, sigla);
        System.out.println("  [✓] Departamento '" + d.getNome() + "' registado com sucesso.");
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.tituloPagina("DEPARTAMENTOS", "Lista de Departamentos");
        ArrayList<Departamento> lista = departamentoController.listarDepartamentos();
        if (lista.isEmpty()) {
            System.out.println("  (sem departamentos registados)");
            Utils.pausar(scanner);
            return;
        }
        for (Departamento d : lista) {
            System.out.println("\n  " + d.getNome() + " (" + d.getSigla() + ")");
            mostrarCursosDoDepartamento(d);
        }
        System.out.println();
        Utils.pausar(scanner);
    }

    private void procurar() {
        Utils.tituloPagina("DEPARTAMENTOS", "Procurar Departamento");
        Departamento d = selecionarDepartamento();
        if (d == null) return;
        System.out.println("\n  " + d.getNome() + " (" + d.getSigla() + ")");
        mostrarCursosDoDepartamento(d);
        Utils.pausar(scanner);
    }

    private void atualizar() {
        Utils.tituloPagina("DEPARTAMENTOS", "Atualizar Departamento");
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
        Utils.tituloPagina("DEPARTAMENTOS", "Remover Departamento");
        Departamento d = selecionarDepartamento();
        if (d == null) return;
        if (!Utils.confirmar("Remover o departamento '" + d.getNome() + "'?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }
        departamentoController.removerDepartamento(d);
        System.out.println("  [✓] Departamento removido com sucesso.");
        Utils.pausar(scanner);
    }

    private void mostrarCursosDoDepartamento(Departamento d) {
        ArrayList<Curso> cursos = cursoController.listarCursosPorDepartamento(d);
        if (cursos.isEmpty()) {
            System.out.println("  Cursos: (sem cursos associados)");
        } else {
            System.out.println("  Cursos:");
            for (Curso c : cursos) {
                System.out.println("    - " + c.getNomeCurso() + " [" + c.getEstado() + "]");
            }
        }
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
            ArrayList<Curso> cursos = cursoController.listarCursosPorDepartamento(lista.get(i));
            System.out.println("  " + (i + 1) + ". " + lista.get(i).getNome()
                    + " (" + lista.get(i).getSigla() + ")"
                    + " — " + cursos.size() + " curso(s)");
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
