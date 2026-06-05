package View;

import Controller.CursoController;
import Controller.DepartamentoController;
import Controller.EstudanteController;
import Controller.UnidadeCurricularController;
import Model.Curso;
import Model.Departamento;
import Model.Estudante;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CursoView {

    private final CursoController cursoController;
    private final DepartamentoController departamentoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final EstudanteController estudanteController;
    private final Scanner scanner;

    public CursoView(CursoController cursoController, DepartamentoController departamentoController, UnidadeCurricularController unidadeCurricularController, EstudanteController estudanteController, Scanner scanner) {
        this.cursoController = cursoController;
        this.departamentoController = departamentoController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.estudanteController = estudanteController;
        this.scanner = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Registar Curso",
                "Listar Cursos",
                "Procurar Curso por Nome",
                "Configurar Curso",
                "Iniciar Curso",
                "Atualizar Nome de Curso",
                "Remover Curso"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("GESTÃO DE CURSOS", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: registar(); break;
                    case 2: listar();   break;
                    case 3: procurar(); break;
                    case 4: menuConfigurarCurso(); break;
                    case 5: iniciarCurso(); break;
                    case 6: atualizar(); break;
                    case 7: remover(); break;
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
            Utils.pausar(scanner); return;
        }
        System.out.println("  Departamentos disponíveis:");
        for (int i = 0; i < deptos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + deptos.get(i).getNome()
                    + " (" + deptos.get(i).getSigla() + ")");
        }
        int escolha = Utils.lerInteiro("Selecione o departamento (número): ", scanner);
        if (escolha < 1 || escolha > deptos.size()) {
            System.out.println("  [!] Opção inválida."); Utils.pausar(scanner); return;
        }
        Departamento depto = deptos.get(escolha - 1);

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
        System.out.println("\n--- Procurar Curso ---");
        Curso c = selecionarCurso("Cursos disponíveis");
        if (c == null) return;
        System.out.println("\n" + c);
        Utils.pausar(scanner);
    }

    private void atualizar() {
        System.out.println("\n--- Atualizar Nome de Curso ---");
        Curso c = selecionarCurso("Cursos disponíveis");
        if (c == null) return;
        String novoNome = Utils.lerCampo("Novo nome: ", scanner);
        cursoController.atualizarNomeCurso(c, novoNome);
        System.out.println("  [✓] Nome do curso atualizado com sucesso.");
        Utils.pausar(scanner);
    }

    private void remover() {
        System.out.println("\n--- Remover Curso ---");
        Curso c = selecionarCurso("Cursos disponíveis");
        if (c == null) return;

        String confirmar = Utils.lerCampo("  Tem a certeza que deseja remover o curso '" + c.getNomeCurso() + "'? (S/N): ", scanner);
        if (!confirmar.equalsIgnoreCase("S")) {
            System.out.println("  Operação cancelada.");
            Utils.pausar(scanner);
            return;
        }
        cursoController.removerCurso(c);
        System.out.println("  [✓] Curso removido com sucesso.");
        Utils.pausar(scanner);
    }

    private void adicionarUC(Curso c) {
        System.out.println("\n--- Adicionar UC ao Curso '" + c.getNomeCurso() + "' ---");

        System.out.println("\n  Vagas por ano no curso '" + c.getNomeCurso() + "':");
        for (int ano = 1; ano <= 3; ano++) {
            int vagas = cursoController.vagasUCsDisponiveis(c, ano);
            List<UnidadeCurricular> ucsAno = cursoController.listarUCsPorAno(c, ano);
            System.out.println("    Ano " + ano + ": " + ucsAno.size() + "/5 UCs  (" + vagas + " vaga(s))");
        }

        UnidadeCurricular uc = selecionarUC("UCs disponíveis");
        if (uc == null) return;

        cursoController.adicionarUnidadeCurricular(c, uc);
        System.out.println("  [✓] UC '" + uc.getNome() + "' adicionada ao curso '" + c.getNomeCurso() + "'.");
        Utils.pausar(scanner);
    }

    private void listarUCsPorAno(Curso c) {
        Utils.limparEcra();
        System.out.println("\n--- UCs do Curso '" + c.getNomeCurso() + "' por Ano ---");
        int ano = Utils.lerInteiro("Ano curricular (1, 2 ou 3): ", scanner);
        List<UnidadeCurricular> ucs = cursoController.listarUCsPorAno(c, ano);
        if (ucs.isEmpty()) { System.out.println("  (sem UCs para o ano " + ano + ")"); Utils.pausar(scanner); return; }
        for (UnidadeCurricular uc : ucs) { System.out.println("  - " + uc.getNome()); }
        Utils.pausar(scanner);
    }

    private void atualizarPropina(Curso c) {
        System.out.println("\n--- Atualizar Valor de Propina: " + c.getNomeCurso() + " ---");
        System.out.printf("  Propina atual: %.2f €%n", c.getValorPropina());
        System.out.println("  (Enter para cancelar)");
        double novoValor = Utils.lerDouble("Novo valor da propina (€): ", scanner);
        cursoController.atualizarValorPropina(c, novoValor);
        System.out.printf("  [✓] Propina de '%s' atualizada para %.2f €.%n", c.getNomeCurso(), c.getValorPropina());
        Utils.pausar(scanner);
    }

    private void iniciarCurso() {
        System.out.println("\n--- Iniciar Curso ---");
        Curso c = selecionarCurso("Cursos disponíveis (apenas PENDENTE)");
        if (c == null) return;

        List<Estudante> todosEstudantes = estudanteController.listarEstudante();
        int inscritos = cursoController.contarEstudantesInscritosNoCurso(c, todosEstudantes);

        System.out.println("\n  Curso    : " + c.getNomeCurso());
        System.out.println("  Estado   : " + c.getEstado());
        System.out.println("  UCs      : " + c.getUnidades().size());
        System.out.println("  Inscritos: " + inscritos);
        System.out.printf("  Propina  : %.2f €%n", c.getValorPropina());

        String confirmar = Utils.lerCampo("\n  Confirmar inicio do curso? (S/N): ", scanner);
        if (!confirmar.equalsIgnoreCase("S")) {
            System.out.println("  Operação cancelada.");
            Utils.pausar(scanner);
            return;
        }

        cursoController.iniciarCurso(c, todosEstudantes);

        // Contar UCs activas após a iniciação
        int ucsAtivas = 0;
        for (UnidadeCurricular uc : c.getUnidades()) {
            if (uc.isAtiva()) ucsAtivas++;
        }

        System.out.println("\n  [✓] Curso '" + c.getNomeCurso() + "' iniciado com sucesso.");
        System.out.println("  Estado actualizado para: ATIVO");
        System.out.println("  UCs activadas automaticamente: " + ucsAtivas + "/" + c.getUnidades().size());
        System.out.println("  Propinas actualizadas para " + inscritos + " aluno(s) inscrito(s).");
        Utils.pausar(scanner);
    }

    private void listarAlunosInscritos(Curso c) {
        Utils.limparEcra();
        System.out.println("\n--- Alunos Inscritos: " + c.getNomeCurso() + " ---");

        List<Estudante> todosEstudantes = estudanteController.listarEstudante();
        List<Estudante> inscritos = cursoController.listarEstudantesInscritos(c, todosEstudantes);

        System.out.println("\n  Curso: " + c.getNomeCurso() + " [" + c.getEstado() + "]");
        System.out.println("  " + "-".repeat(55));

        if (inscritos.isEmpty()) {
            System.out.println("  (sem alunos inscritos)");
        } else {
            System.out.printf("  %-12s %-25s %-6s%n", "Nº Mecano.", "Nome", "Ano");
            System.out.println("  " + "-".repeat(55));
            for (Estudante e : inscritos) {
                System.out.printf("  %-12s %-25s %-6d%n",
                        e.getNumMecanografico(), e.getNome(), e.getAnoAtual());
            }
            System.out.println("  " + "-".repeat(55));
            System.out.println("  Total: " + inscritos.size() + " aluno(s)");
        }

        Utils.pausar(scanner);
    }

    private void menuConfigurarCurso() {
        Curso c = selecionarCurso("Selecione o curso a configurar");
        if (c == null) return;

        String[] opcoesConfig = {
                "Adicionar UC ao Curso",
                "Listar UCs por Ano",
                "Atualizar Valor de Propina",
                "Listar Alunos Inscritos"
        };

        int opcao;
        do {
            Utils.limparEcra();
            System.out.println("\n  Curso: " + c.getNomeCurso() + " [" + c.getEstado() + "]");
            opcao = Utils.mostrarMenu("CONFIGURAR CURSO", opcoesConfig, scanner);
            try {
                switch (opcao) {
                    case 1: adicionarUC(c); break;
                    case 2: listarUCsPorAno(c); break;
                    case 3: atualizarPropina(c); break;
                    case 4: listarAlunosInscritos(c); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private Curso selecionarCurso(String titulo) {
        ArrayList<Curso> cursos = cursoController.listarCursos();
        if (cursos.isEmpty()) {
            System.out.println("  [!] Não existem cursos registados.");
            Utils.pausar(scanner);
            return null;
        }
        System.out.println("\n  " + titulo + ":");
        for (int i = 0; i < cursos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + cursos.get(i).getNomeCurso()
                    + " [" + cursos.get(i).getEstado() + "]");
        }
        int escolha = Utils.lerInteiro("Selecione o curso (número): ", scanner);
        if (escolha < 1 || escolha > cursos.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return null;
        }
        return cursos.get(escolha - 1);
    }

    private UnidadeCurricular selecionarUC(String titulo) {
        ArrayList<UnidadeCurricular> ucs = unidadeCurricularController.listarUnidades();
        if (ucs.isEmpty()) {
            System.out.println("  [!] Não existem UCs registadas.");
            Utils.pausar(scanner);
            return null;
        }
        System.out.println("\n  " + titulo + ":");
        for (int i = 0; i < ucs.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + ucs.get(i).getNome()
                    + " (Ano " + ucs.get(i).getAnoCurricular() + ")");
        }
        int escolha = Utils.lerInteiro("Selecione a UC (número): ", scanner);
        if (escolha < 1 || escolha > ucs.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return null;
        }
        return ucs.get(escolha - 1);
    }
}