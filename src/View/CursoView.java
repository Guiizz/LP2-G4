package View;

import Controller.CursoController;
import Controller.DepartamentoController;
import Controller.UnidadeCurricularController;
import Model.Curso;
import Model.Departamento;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CursoView {

    private final CursoController cursoController;
    private final DepartamentoController departamentoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final Scanner scanner;

    public CursoView(CursoController cursoController, DepartamentoController departamentoController, UnidadeCurricularController unidadeCurricularController, Scanner scanner) {
        this.cursoController = cursoController;
        this.departamentoController = departamentoController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.scanner = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Registar Curso",
                "Listar Cursos",
                "Procurar Curso por Nome",
                "Atualizar Nome de Curso",
                "Remover Curso",
                "Adicionar UC a Curso",
                "Listar UCs de um Curso por Ano",
                "Atualizar Valor de Propina"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("GESTÃO DE CURSOS", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: registar(); break;
                    case 2: listar();  break;
                    case 3: procurar(); break;
                    case 4: atualizar(); break;
                    case 5: remover(); break;
                    case 6: adicionarUC(); break;
                    case 7: listarUCsPorAno(); break;
                    case 8: atualizarPropina(); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void registar() {
        System.out.println("\n--- Registar Curso --- (0 para cancelar)");

        ArrayList<Departamento> deptos = departamentoController.listarDepartamentos();
        if (deptos.isEmpty()) {
            System.out.println("  [!] Não existem departamentos registados. Registe um primeiro.");
            Utils.pausar(scanner);
            return;
        }
        System.out.println("  Departamentos disponíveis:");
        for (Departamento d : deptos) {
            System.out.println("    - " + d.getNome() + " (" + d.getSigla() + ")");
        }

        String siglaDepto = Utils.lerCampo("Sigla do departamento: ", scanner);
        Departamento depto = departamentoController.procurarDepartamento(siglaDepto);
        if (depto == null) { System.out.println("  [!] Departamento não encontrado."); Utils.pausar(scanner); return; }

        String nomeCurso = Utils.lerCampo("Nome do curso: ", scanner);
        double valorPropina = Utils.lerDouble("Valor anual da propina (€): ", scanner);
        Curso c = cursoController.registarCurso(nomeCurso, depto, valorPropina);
        System.out.println("  [✓] Curso '" + c.getNomeCurso() + "' registado com sucesso.");
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.limparEcra();
        System.out.println("\n--- Lista de Cursos ---");
        ArrayList<Curso> lista = cursoController.listarCursos();
        if (lista.isEmpty()) { System.out.println("  (sem cursos registados)"); Utils.pausar(scanner); return; }
        for (Curso c : lista) { System.out.println(c + "\n"); }
        Utils.pausar(scanner);
    }

    private void procurar() {
        Utils.limparEcra();
        String nome = Utils.lerCampo("Nome do curso: ", scanner);
        Curso c = cursoController.procurarPorNome(nome);
        if (c == null) { System.out.println("  [!] Curso não encontrado."); Utils.pausar(scanner); return; }
        System.out.println("\n" + c);
        Utils.pausar(scanner);
    }

    private void atualizar() {
        System.out.println("\n--- Atualizar Nome de Curso --- (0 para cancelar)");
        String nomeAtual = Utils.lerCampo("Nome atual do curso: ", scanner);
        Curso c = cursoController.procurarPorNome(nomeAtual);
        if (c == null) { System.out.println("  [!] Curso não encontrado."); Utils.pausar(scanner); return; }

        String novoNome = Utils.lerCampo("Novo nome: ", scanner);
        cursoController.atualizarNomeCurso(c, novoNome);
        System.out.println("  [✓] Nome do curso atualizado com sucesso.");
        Utils.pausar(scanner);
    }

    private void remover() {
        System.out.println("\n--- Remover Curso ---");
        String nome = Utils.lerCampo("Nome do curso a remover: ", scanner);
        Curso c = cursoController.procurarPorNome(nome);
        if (c == null) { System.out.println("  [!] Curso não encontrado."); Utils.pausar(scanner); return; }


        cursoController.removerCurso(c);
        System.out.println("  [✓] Curso removido com sucesso.");
        Utils.pausar(scanner);
    }

    private void adicionarUC() {
        System.out.println("\n--- Adicionar UC a Curso ---");
        System.out.print("Nome do curso: ");
        Curso c = cursoController.procurarPorNome(scanner.nextLine().trim());
        if (c == null) { System.out.println("  [!] Curso não encontrado."); Utils.pausar(scanner); return; }

        System.out.println("\n  Vagas de UCs disponíveis por ano no curso '" + c.getNomeCurso() + "':");
        for (int ano = 1; ano <= 3; ano++) {
            int vagas = cursoController.vagasUCsDisponiveis(c, ano);
            List<UnidadeCurricular> ucsAno = cursoController.listarUCsPorAno(c, ano);
            System.out.println("    Ano " + ano + ": " + ucsAno.size() + "/5 UCs  (" + vagas + " vaga(s) disponível(is))");
        }

        ArrayList<UnidadeCurricular> todasUCs = unidadeCurricularController.listarUnidades();
        if (todasUCs.isEmpty()) { System.out.println("  [!] Não existem UCs registadas."); Utils.pausar(scanner); return; }

        System.out.println("  UCs disponíveis:");
        for (UnidadeCurricular uc : todasUCs) {
            System.out.println("    - " + uc.getNome() + " (Ano " + uc.getAnoCurricular() + ")");
        }

        String nomeUC = Utils.lerCampo("Nome da UC a adicionar: ", scanner);
        UnidadeCurricular ucEscolhida = null;
        for (UnidadeCurricular uc : todasUCs) {
            if (uc.getNome().equalsIgnoreCase(nomeUC)) { ucEscolhida = uc; break; }
        }
        if (ucEscolhida == null) { System.out.println("  [!] UC não encontrada."); Utils.pausar(scanner); return; }

        cursoController.adicionarUnidadeCurricular(c, ucEscolhida);
        System.out.println("  [✓] UC '" + ucEscolhida.getNome() + "' adicionada ao curso '" + c.getNomeCurso() + "'.");
        Utils.pausar(scanner);
    }

    private void listarUCsPorAno() {
        Utils.limparEcra();
        System.out.println("\n--- Listar UCs de um Curso por Ano ---");
        System.out.print("Nome do curso: ");
        Curso c = cursoController.procurarPorNome(scanner.nextLine().trim());
        if (c == null) { System.out.println("  [!] Curso não encontrado."); Utils.pausar(scanner); return; }

        int ano = Utils.lerInteiro("Ano curricular (1, 2 ou 3): ", scanner);
        List<UnidadeCurricular> ucs = cursoController.listarUCsPorAno(c, ano);
        if (ucs.isEmpty()) { System.out.println("  (sem UCs para o ano " + ano + ")"); Utils.pausar(scanner); return; }
        for (UnidadeCurricular uc : ucs) { System.out.println("  - " + uc.getNome()); }
        Utils.pausar(scanner);
    }

    private void atualizarPropina() {
        System.out.println("\n--- Atualizar Valor de Propina --- (0 para cancelar)");
        String nomeCurso = Utils.lerCampo("Nome do curso: ", scanner);
        Curso c = cursoController.procurarPorNome(nomeCurso);
        if (c == null) {
            System.out.println("  [!] Curso não encontrado.");
            Utils.pausar(scanner);
            return;
        }
        System.out.printf("  Propina atual: %.2f €%n", c.getValorPropina());
        double novoValor = Utils.lerDouble("Novo valor da propina (€): ", scanner);
        cursoController.atualizarValorPropina(c, novoValor);
        System.out.printf("  [✓] Propina do curso '%s' atualizada para %.2f €.%n",
                c.getNomeCurso(), c.getValorPropina());
        Utils.pausar(scanner);
    }
}