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
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void registar() {
        Utils.tituloPagina("GESTÃO DE CURSOS", "Registar Curso");

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
        int escolha;
        do {
            escolha = Utils.lerInteiro("  Selecione o departamento (0 para voltar): ", scanner);
            if (escolha == 0) return;
            if (escolha < 1 || escolha > deptos.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + deptos.size() + ".");
        } while (escolha < 1 || escolha > deptos.size());
        Departamento depto = deptos.get(escolha - 1);

        String nomeCurso = Utils.lerCampo("Nome do curso: ", scanner);

        double valorPropina;
        do {
            valorPropina = Utils.lerDouble("Valor anual da propina (€): ", scanner);
            if (valorPropina < 0)
                System.out.println("  [!] O valor da propina não pode ser negativo.");
        } while (valorPropina < 0);

        Curso c = cursoController.registarCurso(nomeCurso, depto, valorPropina);
        System.out.println("  [✓] Curso '" + c.getNomeCurso() + "' registado com sucesso.");
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.tituloPagina("GESTÃO DE CURSOS", "Lista de Cursos");
        ArrayList<Curso> lista = cursoController.listarCursos();
        if (lista.isEmpty()) { System.out.println("  (sem cursos registados)"); Utils.pausar(scanner); return; }
        for (Curso c : lista) { System.out.println(c + "\n"); }
        Utils.pausar(scanner);
    }

    private void procurar() {
        Utils.tituloPagina("GESTÃO DE CURSOS", "Procurar Curso");
        Curso c = selecionarCurso("Cursos disponíveis");
        if (c == null) return;
        System.out.println("\n" + c);
        Utils.pausar(scanner);
    }

    private void atualizar() {
        Utils.tituloPagina("GESTÃO DE CURSOS", "Atualizar Nome de Curso");
        Curso c = selecionarCurso("Cursos disponíveis");
        if (c == null) return;
        String novoNome = Utils.lerCampo("Novo nome: ", scanner);
        cursoController.atualizarNomeCurso(c, novoNome);
        System.out.println("  [✓] Nome do curso atualizado com sucesso.");
        Utils.pausar(scanner);
    }

    private void remover() {
        Utils.tituloPagina("GESTÃO DE CURSOS", "Remover Curso");
        Curso c = selecionarCurso("Cursos disponíveis");
        if (c == null) return;
        if (!Utils.confirmar("Remover o curso '" + c.getNomeCurso() + "'?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }
        cursoController.removerCurso(c);
        System.out.println("  [✓] Curso removido com sucesso.");
        Utils.pausar(scanner);
    }

    private void adicionarUC(Curso c) {
        Utils.tituloPagina("GESTÃO DE CURSOS", "Adicionar UC — " + c.getNomeCurso());

        System.out.println("\n  Vagas por ano no curso '" + c.getNomeCurso() + "':");
        for (int ano = 1; ano <= 3; ano++) {
            int vagas = cursoController.vagasUCsDisponiveis(c, ano);
            List<UnidadeCurricular> ucsAno = cursoController.listarUCsPorAno(c, ano);
            System.out.println("    Ano " + ano + ": " + ucsAno.size() + "/5 UCs  (" + vagas + " vaga(s))");
        }

        UnidadeCurricular uc = selecionarUC("UCs disponíveis (excluindo já associadas)", c);
        if (uc == null) return;

        int ano;
        do {
            ano = Utils.lerInteiro("  Em que ano curricular fica esta UC? (1-3): ", scanner);
            if (ano < 1 || ano > 3) System.out.println("  [!] Escolha 1, 2 ou 3.");
        } while (ano < 1 || ano > 3);

        cursoController.adicionarUnidadeCurricular(c, uc, ano);
        System.out.println("  [✓] UC '" + uc.getNome() + "' adicionada ao ano " + ano + " do curso '" + c.getNomeCurso() + "'.");
        Utils.pausar(scanner);
    }

    private void listarUCsPorAno(Curso c) {
        Utils.tituloPagina("GESTÃO DE CURSOS", "UCs por Ano — " + c.getNomeCurso());
        System.out.println("  1. Ano 1");
        System.out.println("  2. Ano 2");
        System.out.println("  3. Ano 3");
        int ano;
        do {
            ano = Utils.lerInteiro("  Selecione o ano curricular (0 para voltar): ", scanner);
            if (ano == 0) return;
            if (ano < 1 || ano > 3) System.out.println("  [!] Opção inválida. Escolha 1, 2 ou 3.");
        } while (ano < 1 || ano > 3);
        List<UnidadeCurricular> ucs = cursoController.listarUCsPorAno(c, ano);
        if (ucs.isEmpty()) { System.out.println("  (sem UCs para o ano " + ano + ")"); Utils.pausar(scanner); return; }
        System.out.printf("  %-30s %-12s %s%n", "UC", "Estado", "Docente");
        System.out.println("  " + "─".repeat(60));
        for (UnidadeCurricular uc : ucs) {
            System.out.printf("  %-30s %-12s %s%n",
                    uc.getNome(),
                    uc.isAtiva() ? "ATIVA" : "INATIVA",
                    uc.getDocenteResponsavel() != null ? uc.getDocenteResponsavel() : "—");
        }
        Utils.pausar(scanner);
    }

    private void atualizarPropina(Curso c) {
        Utils.tituloPagina("GESTÃO DE CURSOS", "Atualizar Propina — " + c.getNomeCurso());
        System.out.printf("  Propina atual: %.2f €%n", c.getValorPropina());
        System.out.println("  (Enter para cancelar)");
        double novoValor;
        do {
            novoValor = Utils.lerDouble("Novo valor da propina (€): ", scanner);
            if (novoValor < 0)
                System.out.println("  [!] O valor da propina não pode ser negativo.");
        } while (novoValor < 0);
        cursoController.atualizarValorPropina(c, novoValor);
        System.out.printf("  [✓] Propina de '%s' atualizada para %.2f €.%n", c.getNomeCurso(), c.getValorPropina());
        Utils.pausar(scanner);
    }

    private void iniciarCurso() {
        Utils.tituloPagina("GESTÃO DE CURSOS", "Iniciar Curso");
        Curso c = selecionarCurso("Cursos disponíveis", "PENDENTE");
        if (c == null) return;

        List<Estudante> todosEstudantes = estudanteController.listarEstudante();
        int inscritos = cursoController.contarEstudantesInscritosNoCurso(c, todosEstudantes);

        System.out.println("\n  Curso    : " + c.getNomeCurso());
        System.out.println("  Estado   : " + c.getEstado());
        System.out.println("  UCs      : " + c.getUnidades().size());
        System.out.println("  Inscritos: " + inscritos);
        System.out.printf("  Propina  : %.2f €%n", c.getValorPropina());

        if (!Utils.confirmar("Confirmar início do curso '" + c.getNomeCurso() + "'?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }

        cursoController.iniciarCurso(c, todosEstudantes);

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
        Utils.tituloPagina("GESTÃO DE CURSOS", "Alunos Inscritos — " + c.getNomeCurso());

        List<Estudante> todosEstudantes = estudanteController.listarEstudante();
        List<Estudante> inscritos = cursoController.listarEstudantesInscritos(c, todosEstudantes);

        System.out.println("\n  Curso: " + c.getNomeCurso() + " [" + c.getEstado() + "]");
        System.out.println("  " + "─".repeat(55));

        if (inscritos.isEmpty()) {
            System.out.println("  (sem alunos inscritos)");
        } else {
            System.out.printf("  %-12s %-25s %-6s%n", "Nº Mecano.", "Nome", "Ano");
            System.out.println("  " + "─".repeat(55));
            for (Estudante e : inscritos) {
                System.out.printf("  %-12s %-25s %-6d%n",
                        e.getNumMecanografico(), e.getNome(), e.getAnoAtual());
            }
            System.out.println("  " + "─".repeat(55));
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
                "Listar Alunos Inscritos",
                "Remover UC do Curso"
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
                    case 5: removerUCdoCurso(c); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void removerUCdoCurso(Curso c) {
        Utils.tituloPagina("GESTÃO DE CURSOS", "Remover UC — " + c.getNomeCurso());
        List<UnidadeCurricular> ucs = c.getUnidades();
        if (ucs.isEmpty()) {
            System.out.println("  [!] O curso não tem UCs associadas.");
            Utils.pausar(scanner);
            return;
        }
        System.out.println("  UCs do curso:");
        for (int i = 0; i < ucs.size(); i++) {
            UnidadeCurricular u = ucs.get(i);
            System.out.println("  " + (i + 1) + ". " + u.getNome()
                    + " (Ano " + c.getAnoCurricularDe(u) + ")"
                    + (u.isAtiva() ? " [ATIVA]" : ""));
        }
        int escolha;
        do {
            escolha = Utils.lerInteiro("  Selecione a UC a remover (0 para voltar): ", scanner);
            if (escolha == 0) return;
            if (escolha < 1 || escolha > ucs.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + ucs.size() + ".");
        } while (escolha < 1 || escolha > ucs.size());
        UnidadeCurricular uc = ucs.get(escolha - 1);
        if (!Utils.confirmar("Remover a UC '" + uc.getNome() + "' do curso '" + c.getNomeCurso() + "'?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }
        cursoController.removerUnidadeCurricular(c, uc);
        System.out.println("  [✓] UC '" + uc.getNome() + "' removida do curso.");
        Utils.pausar(scanner);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Curso selecionarCurso(String titulo, String filtroEstado) {
        ArrayList<Curso> todos = cursoController.listarCursos();
        List<Curso> cursos = new ArrayList<>();
        for (Curso c : todos) {
            if (filtroEstado == null || filtroEstado.equalsIgnoreCase(c.getEstado())) cursos.add(c);
        }
        if (cursos.isEmpty()) {
            System.out.println("  [!] " + (filtroEstado != null
                    ? "Não existem cursos no estado " + filtroEstado + "."
                    : "Não existem cursos registados."));
            Utils.pausar(scanner);
            return null;
        }
        System.out.println("\n  " + titulo + ":");
        for (int i = 0; i < cursos.size(); i++) {
            Curso c = cursos.get(i);
            String dept = c.getDepartamento() != null ? c.getDepartamento().getNome() : "sem dept.";
            System.out.println("  " + (i + 1) + ". " + c.getNomeCurso() + " [" + c.getEstado() + "] — " + dept);
        }
        int escolha;
        do {
            escolha = Utils.lerInteiro("  Selecione o curso (0 para voltar): ", scanner);
            if (escolha == 0) return null;
            if (escolha < 1 || escolha > cursos.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + cursos.size() + ".");
        } while (escolha < 1 || escolha > cursos.size());
        return cursos.get(escolha - 1);
    }

    private Curso selecionarCurso(String titulo) {
        return selecionarCurso(titulo, null);
    }

    private UnidadeCurricular selecionarUC(String titulo, Curso excluirDoC) {
        ArrayList<UnidadeCurricular> todas = unidadeCurricularController.listarUnidades();
        List<UnidadeCurricular> ucs = new ArrayList<>();
        for (UnidadeCurricular u : todas) {
            if (excluirDoC != null && excluirDoC.getUnidades().contains(u)) continue;
            ucs.add(u);
        }
        if (ucs.isEmpty()) {
            System.out.println("  [!] Não existem UCs disponíveis para adicionar.");
            Utils.pausar(scanner);
            return null;
        }
        System.out.println("\n  " + titulo + ":");
        for (int i = 0; i < ucs.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + ucs.get(i).getNome());
        }
        int escolha;
        do {
            escolha = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
            if (escolha == 0) return null;
            if (escolha < 1 || escolha > ucs.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + ucs.size() + ".");
        } while (escolha < 1 || escolha > ucs.size());
        return ucs.get(escolha - 1);
    }

    private UnidadeCurricular selecionarUC(String titulo) {
        return selecionarUC(titulo, null);
    }
}
