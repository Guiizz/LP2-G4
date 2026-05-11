package View;

import Controller.CursoController;
import Controller.EstudanteController;
import Controller.InscricaoController;
import Model.Curso;
import Model.Estudante;
import Model.Inscricao;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class EstudanteGestorView {

    private final EstudanteController estudanteController;
    private final CursoController cursoController;
    private final InscricaoController inscricaoController;
    private final Scanner scanner;

    public EstudanteGestorView(EstudanteController estudanteController, CursoController cursoController, InscricaoController inscricaoController, Scanner scanner) {
        this.estudanteController = estudanteController;
        this.cursoController = cursoController;
        this.inscricaoController = inscricaoController;
        this.scanner = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Registar Estudante",
                "Listar Estudantes",
                "Procurar Estudante por Nº Mecanográfico",
                "Atualizar Estudante",
                "Remover Estudante",
                "Inscrever Estudante em Curso",
                "Marcar Propina Atual como Paga",
                "Listar Estudantes com Propina em Dívida",
                "Registar Nota na Inscrição Atual"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("GESTÃO DE ESTUDANTES", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: registar(); break;
                    case 2: listar(); break;
                    case 3: procurar(); break;
                    case 4: atualizar(); break;
                    case 5: remover(); break;
                    case 6: inscrever(); break;
                    case 7: marcarPropinaPaga(); break;
                    case 8: listarPropinasEmDivida(); break;
                    case 9: registarNota(); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void registar() {
        System.out.println("\n--- Registar Estudante --- (0 para cancelar)");
        String nome = Utils.lerCampo("Nome: ", scanner);
        LocalDate data = Utils.lerData("Data de nascimento (AAAA-MM-DD): ", scanner);
        String nif = Utils.lerCampo("NIF: ", scanner);
        String morada = Utils.lerCampo("Morada: ", scanner);

        Estudante e = estudanteController.registarEstudante(nome, data, nif, morada);
        System.out.println("  [✓] Estudante registado com sucesso.");
        System.out.println("  Nº Mecanográfico : " + e.getNumMecanografico());
        System.out.println("  E-mail           : " + e.getEmail());
        System.out.println("  Password inicial : enviada por email");
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.limparEcra();
        System.out.println("\n--- Lista de Estudantes ---");
        ArrayList<Estudante> lista = estudanteController.listarEstudante();
        if (lista.isEmpty()) { System.out.println("  (sem estudantes registados)"); Utils.pausar(scanner); return; }
        for (Estudante e : lista) { System.out.println(e + "\n"); }
        Utils.pausar(scanner);
    }

    private void procurar() {
        Utils.limparEcra();
        System.out.print("\nNº Mecanográfico: ");
        String num = scanner.nextLine().trim();
        Estudante e = estudanteController.procurarPorNumMecanografico(num);
        System.out.println("\n" + e);
        Utils.pausar(scanner);
    }

    private void atualizar() {
        System.out.println("\n--- Atualizar Estudante --- (0 para cancelar)");
        System.out.print("Nº Mecanográfico do estudante a atualizar: ");
        String num = scanner.nextLine().trim();
        Estudante e = estudanteController.procurarPorNumMecanografico(num);

        System.out.println("  Dados atuais: " + e.getNome() + " | " + e.getMorada());
        System.out.println("  Email, NIF, data de nascimento e nº mecanográfico não são editáveis.");
        String novoNome = Utils.lerCampo("Novo nome (Enter para manter): ", scanner);
        String novaMorada = Utils.lerCampo("Nova morada (Enter para manter): ", scanner);

        String nomeAtualizar = novoNome.isEmpty() ? e.getNome() : novoNome;
        String moradaAtualizar = novaMorada.isEmpty() ? e.getMorada() : novaMorada;

        estudanteController.atualizarEstudante(e.getNumMecanografico(), nomeAtualizar, moradaAtualizar);
        System.out.println("  [✓] Estudante atualizado com sucesso.");
        Utils.pausar(scanner);
    }

    private void remover() {
        System.out.print("\nNº Mecanográfico do estudante a remover: ");
        String num = scanner.nextLine().trim();
        estudanteController.removerEstudante(num);
        System.out.println("  [✓] Estudante removido com sucesso.");
        Utils.pausar(scanner);
    }

    private void inscrever() {
        System.out.println("\n--- Inscrever Estudante em Curso ---");
        System.out.print("Nº Mecanográfico: ");
        Estudante e = estudanteController.procurarPorNumMecanografico(scanner.nextLine().trim());

        ArrayList<Curso> cursos = cursoController.listarCursos();
        if (cursos.isEmpty()) { System.out.println("  [!] Não existem cursos registados."); Utils.pausar(scanner); return; }

        System.out.println("  Cursos disponíveis:");
        for (Curso c : cursos) {
            System.out.println("    - " + c.getNomeCurso() + " | " + c.getDepartamento().getNome());
        }

        System.out.print("Nome do curso: ");
        Curso curso = cursoController.procurarPorNome(scanner.nextLine().trim());
        if (curso == null) { System.out.println("  [!] Curso não encontrado."); Utils.pausar(scanner); return; }

        Inscricao inscricao = inscricaoController.inscreverEstudante(e, curso, LocalDate.now().getYear());

        String propinaPaga = Utils.lerCampo("Propina inicial já está paga? (S/N): ", scanner);
        inscricao.setPropinaPaga(propinaPaga.equalsIgnoreCase("S"));

        estudanteController.guardarEstadoEstudante(e);

        System.out.println("  [✓] Estudante inscrito em '" + curso.getNomeCurso() + "' com sucesso.");
        Utils.pausar(scanner);
    }

    private void marcarPropinaPaga() {
        System.out.println("\n--- Marcar Propina Atual como Paga ---");
        System.out.print("Nº Mecanográfico: ");
        String num = scanner.nextLine().trim();

        estudanteController.marcarPropinaAtualComoPaga(num);

        System.out.println("  [✓] Propina atual marcada como paga.");
        Utils.pausar(scanner);
    }

    private void listarPropinasEmDivida() {
        Utils.limparEcra();
        System.out.println("\n--- Estudantes com Propina em Dívida ---");

        ArrayList<Estudante> estudantes = estudanteController.listarComPropinaEmDivida();

        if (estudantes.isEmpty()) {
            System.out.println("  (sem dívidas registadas)");
            Utils.pausar(scanner);
            return;
        }

        for (Estudante e : estudantes) {
            Inscricao inscricaoAtual = estudanteController.obterInscricaoAtual(e);
            String curso = inscricaoAtual != null && inscricaoAtual.getCurso() != null
                    ? inscricaoAtual.getCurso().getNomeCurso()
                    : "sem curso";
            System.out.println("  - " + e.getNome() + " (" + e.getNumMecanografico() + ") | " + curso);
        }

        Utils.pausar(scanner);
    }

    private void registarNota() {
        System.out.println("\n--- Registar Nota na Inscrição Atual ---");
        System.out.print("Nº Mecanográfico: ");
        String num = scanner.nextLine().trim();
        double nota = Utils.lerDouble("Nota (0-20): ", scanner);

        estudanteController.registarNotaNaInscricaoAtual(num, nota);

        System.out.println("  [✓] Nota registada na inscrição atual do estudante.");
        Utils.pausar(scanner);
    }
}