package View;

import Controller.*;
import Model.Gestor;
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
                "Ano Letivo",
                "Gerir Departamentos",
                "Gerir Cursos",
                "Gerir Unidades Curriculares",
                "Gerir Docentes",
                "Gerir Estudantes",
                "Gerir Momentos de Avaliação",
                "Lançar Nota a Aluno",
                "Gerir Horários",
                "Gerir Justificações de Faltas",
                "Ver a minha Ficha",
                "Gerir Gestores"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ÁREA DO GESTOR [" + gestor.getEmail() + "]", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1:
                        new AnoLetivoView(anoLetivoController, estudanteController, scanner).iniciar();
                        break;
                    case 2:
                        new DepartamentoView(departamentoController, cursoController, scanner).iniciar();
                        break;
                    case 3:
                        new CursoView(cursoController, departamentoController, unidadeCurricularController, estudanteController, docenteController, scanner).iniciar();
                        break;
                    case 4:
                        new UnidadeCurricularView(unidadeCurricularController, docenteController, anoLetivoController, cursoController, scanner).iniciar();
                        break;
                    case 5:
                        new DocenteGestorView(docenteController, estudanteController, unidadeCurricularController, cursoController, scanner).iniciar();
                        break;
                    case 6:
                        new EstudanteGestorView(estudanteController, cursoController, inscricaoController, docenteController, scanner).iniciar();
                        break;
                    case 7:
                        new AvaliacaoView(unidadeCurricularController, cursoController, anoLetivoController, scanner).iniciar();
                        break;
                    case 8:
                        lancarNota();
                        break;
                    case 9:
                        new HorarioView(horarioController, cursoController, unidadeCurricularController, anoLetivoController, scanner).iniciar();
                        break;
                    case 10:
                        new JustificacaoGestorView(justificacaoController, scanner).iniciar();
                        break;
                    case 11:
                        Utils.limparEcra();
                        System.out.println("\n" + gestor);
                        Utils.pausar(scanner);
                        break;
                    case 12:
                        new GestorMenuView(gestorController, scanner).iniciar();
                        break;
                    case 0:
                        System.out.println("  A terminar sessão...");
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void lancarNota() {
        Utils.limparEcra();
        Utils.tituloPagina("Lançar Nota a Aluno");

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
                    + " (Ano " + uc.getAnoCurricular() + ")"
                    + " | " + (uc.isAtiva() ? "Ativa" : "Inativa"));
        }
        int escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
        if (escolhaUC == 0) return;
        if (escolhaUC < 1 || escolhaUC > todasUCs.size()) {
            System.out.println("  [!] Opção inválida. Selecione entre 1 e " + todasUCs.size() + ".");
            Utils.pausar(scanner);
            return;
        }
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
                    + " (" + momentos.get(i).getPeso() + "%)"
                    + (momentos.get(i).getNomeCurso() != null ? " — " + momentos.get(i).getNomeCurso() : "")
                    + (momentos.get(i).getData() != null ? " — " + momentos.get(i).getDataFormatada() : ""));
        }
        int escolhaMomento = Utils.lerInteiro("  Selecione o momento (0 para voltar): ", scanner);
        if (escolhaMomento == 0) return;
        if (escolhaMomento < 1 || escolhaMomento > momentos.size()) {
            System.out.println("  [!] Opção inválida. Selecione entre 1 e " + momentos.size() + ".");
            Utils.pausar(scanner);
            return;
        }
        Model.MomentoAvaliacao momentoEscolhido = momentos.get(escolhaMomento - 1);
        // O índice da nota na inscrição é a posição do momento dentro do grupo do curso
        int indiceMomento = ucEscolhida
                .getMomentosParaAno(anoLetivo, momentoEscolhido.getNomeCurso())
                .indexOf(momentoEscolhido);

        java.util.ArrayList<Model.Estudante> todosEstudantes = estudanteController.listarEstudante();
        java.util.ArrayList<Model.Estudante> alunosDaUC = new java.util.ArrayList<>();
        for (Model.Estudante e : todosEstudantes) {
            Model.Inscricao insc = estudanteController.obterInscricaoAtual(e);
            if (insc != null && insc.getCurso() != null
                    && insc.getCurso().getUnidades().contains(ucEscolhida)
                    && insc.getAnoDeCurso() == ucEscolhida.getAnoCurricular()
                    && momentoEscolhido.pertenceAoCurso(insc.getCurso().getNomeCurso())) {
                alunosDaUC.add(e);
            }
        }
        if (alunosDaUC.isEmpty()) {
            System.out.println("  [!] Não existem alunos inscritos no " + ucEscolhida.getAnoCurricular() + ".º ano desta UC.");
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

        int escolhaAluno = Utils.lerInteiro("  Selecione o aluno (0 para voltar): ", scanner);
        if (escolhaAluno == 0) return;
        if (escolhaAluno < 1 || escolhaAluno > alunosDaUC.size()) {
            System.out.println("  [!] Opção inválida. Selecione entre 1 e " + alunosDaUC.size() + ".");
            Utils.pausar(scanner);
            return;
        }
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