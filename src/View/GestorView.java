package View;

import Controller.*;
import Model.Gestor;
import Utils.PasswordUtils;
import Utils.Utils;

import java.util.Scanner;

public class GestorView {

    private final GestorController gestorController;
    private final EstudanteController estudanteController;
    private final DocenteController docenteController;
    private final DepartamentoController departamentoController;
    private final CursoController cursoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final AvaliacaoController avaliacaoController;
    private final InscricaoController inscricaoController;
    private final AnoLetivoController anoLetivoController;
    private final HorarioController horarioController;
    private final JustificacaoController justificacaoController;
    private final Scanner scanner;

    public GestorView(GestorController gestorController, EstudanteController estudanteController, DocenteController docenteController, DepartamentoController departamentoController, CursoController cursoController, UnidadeCurricularController unidadeCurricularController, AvaliacaoController avaliacaoController, InscricaoController inscricaoController, AnoLetivoController anoLetivoController, HorarioController horarioController, JustificacaoController justificacaoController, Scanner scanner) {
        this.gestorController = gestorController;
        this.estudanteController = estudanteController;
        this.docenteController = docenteController;
        this.departamentoController = departamentoController;
        this.cursoController = cursoController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.avaliacaoController = avaliacaoController;
        this.inscricaoController = inscricaoController;
        this.anoLetivoController = anoLetivoController;
        this.horarioController = horarioController;
        this.justificacaoController = justificacaoController;
        this.scanner = scanner;
    }

    public void iniciar(Gestor gestor) {
        String[] opcoes = {
                "Académico",
                "Pessoas",
                "Avaliações",
                "A minha Conta"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ÁREA DO GESTOR [" + gestor.getEmail() + "]", opcoes, "Sair", scanner);
            try {
                switch (opcao) {
                    case 1: menuAcademico(); break;
                    case 2: menuPessoas(); break;
                    case 3: menuAvaliacoes(); break;
                    case 4: menuConta(gestor); break;
                    case 0: System.out.println("  A terminar sessão..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    // ── Submenus ──────────────────────────────────────────────────────────────

    private void menuAcademico() {
        String[] opcoes = {
                "Ano Letivo",
                "Departamentos",
                "Cursos",
                "Unidades Curriculares",
                "Horários",
                "Momentos de Avaliação"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ACADÉMICO", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: new AnoLetivoView(anoLetivoController, estudanteController, scanner).iniciar(); break;
                    case 2: new DepartamentoView(departamentoController, cursoController, scanner).iniciar(); break;
                    case 3: new CursoView(cursoController, departamentoController, unidadeCurricularController, estudanteController, scanner).iniciar(); break;
                    case 4: new UnidadeCurricularView(unidadeCurricularController, docenteController, anoLetivoController, cursoController, scanner).iniciar(); break;
                    case 5: new HorarioView(horarioController, cursoController, unidadeCurricularController, anoLetivoController, scanner).iniciar(); break;
                    case 6: new AvaliacaoView(avaliacaoController, unidadeCurricularController, cursoController, scanner).iniciar(); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void menuPessoas() {
        String[] opcoes = {
                "Gestores",
                "Docentes",
                "Estudantes"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("PESSOAS", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: new GestorMenuView(gestorController, scanner).iniciar(); break;
                    case 2: new DocenteGestorView(docenteController, estudanteController, unidadeCurricularController, cursoController, scanner).iniciar(); break;
                    case 3: new EstudanteGestorView(estudanteController, cursoController, inscricaoController, docenteController, scanner).iniciar(); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void menuAvaliacoes() {
        String[] opcoes = {
                "Lançar Nota a Aluno",
                "Justificações de Faltas"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("AVALIAÇÕES", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: lancarNota(); break;
                    case 2: new JustificacaoGestorView(justificacaoController, scanner).iniciar(); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void menuConta(Gestor gestor) {
        String[] opcoes = {
                "Ver a minha Ficha",
                "Alterar Password"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("A MINHA CONTA", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: verFicha(gestor); break;
                    case 2: alterarPassword(gestor); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    // ── Ações ─────────────────────────────────────────────────────────────────

    private void verFicha(Gestor gestor) {
        Utils.tituloPagina("A MINHA CONTA", "A minha Ficha");
        System.out.println("\n" + gestor);
        Utils.pausar(scanner);
    }

    private void alterarPassword(Gestor gestor) {
        Utils.tituloPagina("A MINHA CONTA", "Alterar Password");
        System.out.print("  Password atual: ");
        String atual = lerPasswordMascarada();
        System.out.print("  Nova password : ");
        String nova = lerPasswordMascarada();
        System.out.print("  Confirmar     : ");
        String confirmar = lerPasswordMascarada();

        if (!PasswordUtils.verificarPassword(atual, gestor.getPassword())) {
            System.out.println("  [!] A password atual está incorreta.");
            Utils.pausar(scanner);
            return;
        }
        if (!nova.equals(confirmar)) {
            System.out.println("  [!] As passwords não coincidem.");
            Utils.pausar(scanner);
            return;
        }
        gestorController.alterarPassword(gestor, nova);
        System.out.println("  [✓] Password alterada com sucesso.");
        Utils.pausar(scanner);
    }

    private String lerPasswordMascarada() {
        if (System.console() != null) {
            char[] chars = System.console().readPassword();
            return chars != null ? new String(chars) : "";
        }
        return scanner.nextLine().trim();
    }

    private void lancarNota() {
        Utils.tituloPagina("AVALIAÇÕES", "Lançar Nota a Aluno");

        java.util.ArrayList<Model.UnidadeCurricular> todasUCs = unidadeCurricularController.listarUnidades();
        if (todasUCs.isEmpty()) {
            System.out.println("  [!] Não existem UCs registadas.");
            Utils.pausar(scanner);
            return;
        }

        Model.AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : 0;

        System.out.println("\n  Unidades Curriculares:");
        for (int i = 0; i < todasUCs.size(); i++) {
            Model.UnidadeCurricular uc = todasUCs.get(i);
            System.out.println("  " + (i + 1) + ". " + uc.getNome()
                    + " | " + (uc.isAtiva() ? "Ativa" : "Inativa"));
        }
        int escolhaUC;
        do {
            escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
            if (escolhaUC == 0) return;
            if (escolhaUC < 1 || escolhaUC > todasUCs.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + todasUCs.size() + ".");
        } while (escolhaUC < 1 || escolhaUC > todasUCs.size());
        Model.UnidadeCurricular ucEscolhida = todasUCs.get(escolhaUC - 1);

        if (!ucEscolhida.isAtiva()) {
            System.out.println("  [!] A UC '" + ucEscolhida.getNome() + "' ainda não está ativa.");
            Utils.pausar(scanner);
            return;
        }

        java.util.List<Model.MomentoAvaliacao> momentos = ucEscolhida.getMomentosParaAno(anoLetivo);
        if (momentos == null || momentos.isEmpty()) {
            System.out.println("  [!] A UC não tem momentos de avaliação definidos.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Momentos de Avaliação:");
        for (int i = 0; i < momentos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + momentos.get(i).getNome()
                    + " (" + momentos.get(i).getPeso() + "%)");
        }
        int escolhaMomento;
        do {
            escolhaMomento = Utils.lerInteiro("  Selecione o momento (0 para voltar): ", scanner);
            if (escolhaMomento == 0) return;
            if (escolhaMomento < 1 || escolhaMomento > momentos.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + momentos.size() + ".");
        } while (escolhaMomento < 1 || escolhaMomento > momentos.size());
        int indiceMomento = escolhaMomento - 1;
        Model.MomentoAvaliacao momentoEscolhido = momentos.get(indiceMomento);

        java.util.ArrayList<Model.Estudante> todosEstudantes = estudanteController.listarEstudante();
        java.util.ArrayList<Model.Estudante> alunosDaUC = new java.util.ArrayList<>();
        for (Model.Estudante e : todosEstudantes) {
            Model.Inscricao insc = estudanteController.obterInscricaoAtual(e);
            if (insc != null && insc.getCurso() != null
                    && insc.getCurso().getUnidades().contains(ucEscolhida)
                    && insc.getAnoDeCurso() == insc.getCurso().getAnoCurricularDe(ucEscolhida)) {
                alunosDaUC.add(e);
            }
        }
        if (alunosDaUC.isEmpty()) {
            System.out.println("  [!] Não existem alunos inscritos nesta UC.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Alunos — " + momentoEscolhido.getNome());
        System.out.println("  " + "─".repeat(55));
        System.out.printf("  %-5s %-25s %-12s %s%n", "Nº", "Nome", "Nº Mecano.", "Nota atual");
        System.out.println("  " + "─".repeat(55));
        for (int i = 0; i < alunosDaUC.size(); i++) {
            Model.Estudante e = alunosDaUC.get(i);
            Model.Inscricao insc = estudanteController.obterInscricaoAtual(e);
            String notaAtual = "Pendente";
            if (insc != null && insc.getAvaliacoes() != null && indiceMomento < insc.getAvaliacoes().size()) {
                Model.Avaliacao av = insc.getAvaliacoes().get(indiceMomento);
                notaAtual = av.isLancada() ? String.format("%.1f", av.getNota()) : "Pendente";
            }
            System.out.printf("  %-5d %-25s %-12s %s%n", (i + 1), e.getNome(), e.getNumMecanografico(), notaAtual);
        }
        System.out.println("  " + "─".repeat(55));

        int escolhaAluno;
        do {
            escolhaAluno = Utils.lerInteiro("  Selecione o aluno (0 para voltar): ", scanner);
            if (escolhaAluno == 0) return;
            if (escolhaAluno < 1 || escolhaAluno > alunosDaUC.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + alunosDaUC.size() + ".");
        } while (escolhaAluno < 1 || escolhaAluno > alunosDaUC.size());
        Model.Estudante alunoEscolhido = alunosDaUC.get(escolhaAluno - 1);

        Model.Inscricao inscricaoAluno = estudanteController.obterInscricaoAtual(alunoEscolhido);
        if (inscricaoAluno != null && inscricaoAluno.getAvaliacoes() != null
                && indiceMomento < inscricaoAluno.getAvaliacoes().size()) {
            Model.Avaliacao avExistente = inscricaoAluno.getAvaliacoes().get(indiceMomento);
            if (avExistente != null && avExistente.isLancada()) {
                System.out.println("  [!] Já existe uma nota lançada: " + String.format("%.1f", avExistente.getNota()) + "/20.");
                if (!Utils.confirmar("Deseja substituir esta nota?", scanner)) {
                    Utils.pausar(scanner);
                    return;
                }
            }
        }

        double nota = Utils.lerDouble("  Nota (0-20): ", scanner);
        estudanteController.lancarNotaMomento(alunoEscolhido, ucEscolhida, indiceMomento, nota);
        System.out.println("  [✓] Nota " + String.format("%.1f", nota)
                + " registada para " + alunoEscolhido.getNome()
                + " no momento '" + momentoEscolhido.getNome() + "'.");
        Utils.pausar(scanner);
    }
}
