package View;

import Controller.AvaliacaoController;
import Controller.DocenteController;
import Controller.EstudanteController;
import Controller.UnidadeCurricularController;
import Model.*;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class DocenteView {

    private final DocenteController docenteController;
    private final EstudanteController estudanteController;
    private final AvaliacaoController avaliacaoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final Scanner scanner;

    public DocenteView(DocenteController docenteController, EstudanteController estudanteController, AvaliacaoController avaliacaoController, UnidadeCurricularController unidadeCurricularController, Scanner scanner) {
        this.docenteController = docenteController;
        this.estudanteController = estudanteController;
        this.avaliacaoController = avaliacaoController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.scanner = scanner;
    }

    public void iniciar(Docente docente) {
        String[] opcoes = {
                "Ver a minha Ficha",
                "Ver as minhas Unidades Curriculares",
                "Ver os meus Alunos",
                "Lançar Avaliação",
                "Atualizar os meus Dados"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ÁREA DO DOCENTE [" + docente.getEmail() + "]", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: verFicha(docente);   break;
                    case 2: verUCs(docente);     break;
                    case 3: verAlunos(docente);  break;
                    case 4: lancarAvaliacao(docente);   break;
                    case 5: atualizar(docente);  break;
                    case 0: System.out.println("  A terminar sessão..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void verFicha(Docente docente) {
        Utils.limparEcra();
        System.out.println("\n--- A minha Ficha ---");
        System.out.println(docente);
        Utils.pausar(scanner);
    }

    private void verUCs(Docente docente) {
        Utils.limparEcra();
        System.out.println("\n--- As minhas Unidades Curriculares ---");
        ArrayList<UnidadeCurricular> todasUCs = unidadeCurricularController.listarUnidades();
        boolean encontrou = false;
        for (UnidadeCurricular uc : todasUCs) {
            if (docente.getSigla().equalsIgnoreCase(uc.getDocenteResponsavel())) {
                System.out.println("  - " + uc.getNome() + " (Ano " + uc.getAnoCurricular() + ")");
                encontrou = true;
            }
        }
        if (!encontrou) System.out.println("  (sem unidades curriculares atribuídas)");
        Utils.pausar(scanner);
    }

    private void verAlunos(Docente docente) {
        Utils.limparEcra();
        System.out.println("\n--- Os meus Alunos ---");
        ArrayList<UnidadeCurricular> todasUCs        = unidadeCurricularController.listarUnidades();
        ArrayList<Estudante>         todosEstudantes = estudanteController.listarEstudante();

        boolean encontrouAluno = false;
        for (UnidadeCurricular uc : todasUCs) {
            if (docente.getSigla().equalsIgnoreCase(uc.getDocenteResponsavel())) {
                System.out.println("  UC: " + uc.getNome());
                for (Estudante e : todosEstudantes) {
                    Inscricao inscricao = estudanteController.obterInscricaoAtual(e);
                    if (inscricao != null && inscricao.getCurso() != null) {
                        if (inscricao.getCurso().getUnidades().contains(uc)) {
                            System.out.println("    - " + e.getNome() + " (" + e.getNumMecanografico() + ")");
                            encontrouAluno = true;
                        }
                    }
                }
            }
        }
        if (!encontrouAluno) System.out.println("  (sem alunos associados)");
        Utils.pausar(scanner);
    }

    private void lancarAvaliacao(Docente docente) {
        Utils.limparEcra();
        System.out.println("\n--- Lançar Nota por Aluno num Momento de Avaliação ---");

        ArrayList<UnidadeCurricular> todasUCs = unidadeCurricularController.listarUnidades();
        ArrayList<UnidadeCurricular> minhasUCs = new ArrayList<>();

        for (UnidadeCurricular uc : todasUCs) {
            if (docente.getSigla().equalsIgnoreCase(uc.getDocenteResponsavel())) {
                minhasUCs.add(uc);
            }
        }

        if (minhasUCs.isEmpty()) {
            System.out.println("  [!] Não tem nenhuma Unidade Curricular atribuída.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  As suas Unidades Curriculares:");
        for (int i = 0; i < minhasUCs.size(); i++) {
            UnidadeCurricular uc = minhasUCs.get(i);
            System.out.println("  " + (i + 1) + ". " + uc.getNome()
                    + " (Ano " + uc.getAnoCurricular() + ")"
                    + " | Momentos: " + uc.getMomentosAvaliacao().size() + "/3"
                    + " | Estado: " + (uc.isAtiva() ? "Ativa" : "Inativa"));
        }

        int escolhaUC = Utils.lerInteiro("\nSelecione a UC (número): ", scanner);
        if (escolhaUC < 1 || escolhaUC > minhasUCs.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return;
        }

        UnidadeCurricular ucEscolhida = minhasUCs.get(escolhaUC - 1);

        if (!ucEscolhida.isAtiva()) {
            System.out.println("  [!] A UC '" + ucEscolhida.getNome() + "' ainda não está ativa.");
            Utils.pausar(scanner);
            return;
        }

        List<MomentoAvaliacao> momentos = ucEscolhida.getMomentosAvaliacao();
        if (momentos == null || momentos.isEmpty()) {
            System.out.println("  [!] A UC '" + ucEscolhida.getNome() + "' não tem momentos de avaliação definidos.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Momentos de Avaliação da UC '" + ucEscolhida.getNome() + "':");
        for (int i = 0; i < momentos.size(); i++) {
            MomentoAvaliacao m = momentos.get(i);
            System.out.println("  " + (i + 1) + ". " + m.getNome() + " (Peso: " + m.getPeso() + "%)");
        }

        int escolhaMomento = Utils.lerInteiro("\nSelecione o momento (número): ", scanner);
        if (escolhaMomento < 1 || escolhaMomento > momentos.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return;
        }

        int indiceMomento = escolhaMomento - 1;
        MomentoAvaliacao momentoEscolhido = momentos.get(indiceMomento);

        ArrayList<Estudante> todosEstudantes = estudanteController.listarEstudante();
        ArrayList<Estudante> alunosDaUC = new ArrayList<>();

        for (Estudante e : todosEstudantes) {
            Inscricao inscricao = estudanteController.obterInscricaoAtual(e);

            if (inscricao != null
                    && inscricao.getCurso() != null
                    && inscricao.getAnoDeCurso() == ucEscolhida.getAnoCurricular()) {
                alunosDaUC.add(e);
            }
        }

        if (alunosDaUC.isEmpty()) {
            System.out.println("  [!] Não existem alunos inscritos no ano " + ucEscolhida.getAnoCurricular() + ".");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Alunos - Momento: " + momentoEscolhido.getNome());
        System.out.println("  " + "-".repeat(58));
        System.out.printf("  %-5s %-25s %-12s %s%n", "Nº", "Nome", "Nº Mecano.", "Nota atual");
        System.out.println("  " + "-".repeat(58));

        for (int i = 0; i < alunosDaUC.size(); i++) {
            Estudante e = alunosDaUC.get(i);
            Inscricao insc = estudanteController.obterInscricaoAtual(e);

            String notaAtual = "Pendente";
            if (insc != null
                    && insc.getAvaliacoes() != null
                    && indiceMomento < insc.getAvaliacoes().size()) {
                Avaliacao av = insc.getAvaliacoes().get(indiceMomento);
                notaAtual = av.isLancada() ? String.format("%.1f", av.getNota()) : "Pendente";
            }

            System.out.printf("  %-5d %-25s %-12s %s%n",
                    (i + 1), e.getNome(), e.getNumMecanografico(), notaAtual);
        }

        System.out.println("  " + "-".repeat(58));
        System.out.println("\n  (0 para voltar sem guardar)");

        int escolhaAluno = Utils.lerInteiro("Selecione o aluno (número): ", scanner);
        if (escolhaAluno == 0) return;

        if (escolhaAluno < 1 || escolhaAluno > alunosDaUC.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return;
        }

        Estudante alunoEscolhido = alunosDaUC.get(escolhaAluno - 1);

        Inscricao inscricaoAluno = estudanteController.obterInscricaoAtual(alunoEscolhido);
        if (inscricaoAluno != null
                && inscricaoAluno.getAvaliacoes() != null
                && indiceMomento < inscricaoAluno.getAvaliacoes().size()) {
            Avaliacao avExistente = inscricaoAluno.getAvaliacoes().get(indiceMomento);
            if (avExistente != null && avExistente.isLancada()) {
                System.out.println("  [!] Já existe uma nota lançada para este aluno neste momento.");
                System.out.print("  Deseja substituir? (S/N): ");
                String resposta = scanner.nextLine().trim();
                if (!resposta.equalsIgnoreCase("S")) {
                    Utils.pausar(scanner);
                    return;
                }
            }
        }

        double nota = Utils.lerDouble("Nota (0-20): ", scanner);

        estudanteController.lancarNotaMomento(
                alunoEscolhido,
                ucEscolhida,
                indiceMomento,
                nota
        );

        System.out.println("  [✓] Nota " + String.format("%.1f", nota)
                + " registada para " + alunoEscolhido.getNome()
                + " no momento '" + momentoEscolhido.getNome() + "'.");

        Utils.pausar(scanner);
    }

    private void atualizar(Docente docente) {
        System.out.println("\n--- Atualizar os meus Dados --- (0 para cancelar)");
        System.out.println("  Dados atuais: " + docente.getNome() + " | " + docente.getMorada());

        String novoNome   = Utils.lerCampo("Novo nome (Enter para manter): ", scanner);
        String novaMorada = Utils.lerCampo("Nova morada (Enter para manter): ", scanner);

        if (!novoNome.isEmpty())   docente.setNome(novoNome);
        if (!novaMorada.isEmpty()) docente.setMorada(novaMorada);

        docenteController.atualizarDocente(docente);
        System.out.println("  [✓] Dados atualizados com sucesso.");
        Utils.pausar(scanner);
    }
}