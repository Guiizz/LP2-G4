package View;

import Controller.AnoLetivoController;
import Controller.AvaliacaoController;
import Controller.CursoController;
import Controller.EstudanteController;
import Controller.UnidadeCurricularController;
import Model.AnoLetivo;
import Model.Avaliacao;
import Model.Curso;
import Model.Estudante;
import Model.Inscricao;
import Model.MomentoAvaliacao;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class AvaliacaoView {

    private final AvaliacaoController          avaliacaoController;
    private final UnidadeCurricularController  unidadeCurricularController;
    private final CursoController              cursoController;
    private final EstudanteController          estudanteController;
    private final AnoLetivoController          anoLetivoController;
    private final Scanner                      scanner;

    public AvaliacaoView(AvaliacaoController avaliacaoController,
                         UnidadeCurricularController unidadeCurricularController,
                         CursoController cursoController,
                         EstudanteController estudanteController,
                         AnoLetivoController anoLetivoController,
                         Scanner scanner) {
        this.avaliacaoController          = avaliacaoController;
        this.unidadeCurricularController  = unidadeCurricularController;
        this.cursoController              = cursoController;
        this.estudanteController          = estudanteController;
        this.anoLetivoController          = anoLetivoController;
        this.scanner                      = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Registar Avaliação",
                "Listar Avaliações",
                "Procurar Avaliações por UC",
                "Remover Avaliação",
                "Lançar Nota a Aluno"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("GESTÃO DE AVALIAÇÕES", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: registar(); break;
                    case 2: listar(); break;
                    case 3: procurarPorUC(); break;
                    case 4: remover(); break;
                    case 5: lancarNota(); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    // ── Ações ─────────────────────────────────────────────────────────────────

    private void registar() {
        Utils.tituloPagina("AVALIAÇÕES", "Registar Avaliação");

        UnidadeCurricular uc = selecionarUC();
        if (uc == null) return;

        List<Model.MomentoAvaliacao> momentos = uc.getMomentosAvaliacao();
        double peso;
        if (momentos == null || momentos.isEmpty()) {
            System.out.println("  [!] A UC não tem momentos de avaliação definidos. Introduza o peso manualmente.");
            peso = Utils.lerDouble("  Peso (%): ", scanner);
        } else {
            System.out.println("\n  Momentos de avaliação:");
            for (int i = 0; i < momentos.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + momentos.get(i).getNome()
                        + " (" + momentos.get(i).getPeso() + "%)");
            }
            int escolha = Utils.lerInteiro("  Selecione o momento (0 para voltar): ", scanner);
            if (escolha == 0) return;
            if (escolha < 1 || escolha > momentos.size()) {
                System.out.println("  [!] Opção inválida. Selecione entre 1 e " + momentos.size() + ".");
                Utils.pausar(scanner);
                return;
            }
            peso = momentos.get(escolha - 1).getPeso();
            System.out.println("  Peso: " + peso + "%");
        }

        Date data = Utils.lerDataAvaliacao("  Data (DD/MM/AAAA): ", scanner);

        List<UnidadeCurricular> ucs = new ArrayList<>();
        ucs.add(uc);

        Avaliacao a = avaliacaoController.registarAvaliacao(ucs, peso, data, 0);
        System.out.println("  [✓] Momento de avaliação registado com sucesso.");
        System.out.println("  " + a);
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.tituloPagina("AVALIAÇÕES", "Lista de Avaliações");
        ArrayList<Avaliacao> lista = avaliacaoController.listarAvaliacoes();
        if (lista.isEmpty()) { System.out.println("  (sem avaliações registadas)"); Utils.pausar(scanner); return; }
        for (Avaliacao a : lista) { System.out.println(a + "\n"); }
        Utils.pausar(scanner);
    }

    private void procurarPorUC() {
        Utils.tituloPagina("AVALIAÇÕES", "Avaliações por UC");

        UnidadeCurricular uc = selecionarUC();
        if (uc == null) return;

        ArrayList<Avaliacao> avaliacoes = avaliacaoController.procurarPorUC(uc);
        if (avaliacoes.isEmpty()) { System.out.println("  (sem avaliações para esta UC)"); Utils.pausar(scanner); return; }
        for (Avaliacao a : avaliacoes) { System.out.println(a + "\n"); }
        Utils.pausar(scanner);
    }

    private void remover() {
        Utils.tituloPagina("AVALIAÇÕES", "Remover Avaliação");
        ArrayList<Avaliacao> lista = avaliacaoController.listarAvaliacoes();
        if (lista.isEmpty()) { System.out.println("  (sem avaliações registadas)"); Utils.pausar(scanner); return; }

        for (int i = 0; i < lista.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + lista.get(i));
        }

        int escolha = Utils.lerInteiro("  Selecione a avaliação a remover (0 para voltar): ", scanner);
        if (escolha == 0) return;
        if (escolha < 1 || escolha > lista.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return;
        }
        Avaliacao alvo = lista.get(escolha - 1);

        if (!Utils.confirmar("Remover a avaliação de " + alvo.getDataFormatada() + "?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }
        avaliacaoController.removerAvaliacao(alvo);
        System.out.println("  [✓] Avaliação removida com sucesso.");
        Utils.pausar(scanner);
    }

    private void lancarNota() {
        Utils.tituloPagina("AVALIAÇÕES", "Lançar Nota a Aluno");

        ArrayList<UnidadeCurricular> todasUCs = unidadeCurricularController.listarUnidades();
        if (todasUCs.isEmpty()) {
            System.out.println("  [!] Não existem UCs registadas.");
            Utils.pausar(scanner);
            return;
        }

        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : 0;

        System.out.println("\n  Unidades Curriculares:");
        for (int i = 0; i < todasUCs.size(); i++) {
            UnidadeCurricular uc = todasUCs.get(i);
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
        UnidadeCurricular ucEscolhida = todasUCs.get(escolhaUC - 1);

        if (!ucEscolhida.isAtiva()) {
            System.out.println("  [!] A UC '" + ucEscolhida.getNome() + "' ainda não está ativa.");
            Utils.pausar(scanner);
            return;
        }

        List<MomentoAvaliacao> momentos = ucEscolhida.getMomentosParaAno(anoLetivo);
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
        int escolhaMomento = Utils.lerInteiro("  Selecione o momento (0 para voltar): ", scanner);
        if (escolhaMomento == 0) return;
        if (escolhaMomento < 1 || escolhaMomento > momentos.size()) {
            System.out.println("  [!] Opção inválida. Selecione entre 1 e " + momentos.size() + ".");
            Utils.pausar(scanner);
            return;
        }
        int indiceMomento = escolhaMomento - 1;
        MomentoAvaliacao momentoEscolhido = momentos.get(indiceMomento);

        ArrayList<Estudante> todosEstudantes = estudanteController.listarEstudante();
        ArrayList<Estudante> alunosDaUC = new ArrayList<>();
        for (Estudante e : todosEstudantes) {
            Inscricao insc = estudanteController.obterInscricaoAtual(e);
            if (insc != null && insc.getCurso() != null
                    && insc.getCurso().getUnidades().contains(ucEscolhida)
                    && insc.getAnoDeCurso() == ucEscolhida.getAnoCurricular()) {
                alunosDaUC.add(e);
            }
        }
        if (alunosDaUC.isEmpty()) {
            System.out.println("  [!] Não existem alunos inscritos no " + ucEscolhida.getAnoCurricular() + ".º ano desta UC.");
            Utils.pausar(scanner);
            return;
        }

        while (true) {
            Utils.tituloPagina("LANÇAR NOTA", ucEscolhida.getNome() + " — " + momentoEscolhido.getNome());
            System.out.println("  " + "─".repeat(55));
            System.out.printf("  %-5s %-25s %-12s %s%n", "Nº", "Nome", "Nº Mecano.", "Nota atual");
            System.out.println("  " + "─".repeat(55));
            for (int i = 0; i < alunosDaUC.size(); i++) {
                Estudante e = alunosDaUC.get(i);
                Inscricao insc = estudanteController.obterInscricaoAtual(e);
                String notaAtual = "Pendente";
                if (insc != null && insc.getAvaliacoes() != null && indiceMomento < insc.getAvaliacoes().size()) {
                    Avaliacao av = insc.getAvaliacoes().get(indiceMomento);
                    notaAtual = av.isLancada() ? String.format("%.1f", av.getNota()) : "Pendente";
                }
                System.out.printf("  %-5d %-25s %-12s %s%n", (i + 1), e.getNome(), e.getNumMecanografico(), notaAtual);
            }
            System.out.println("  " + "─".repeat(55));

            int escolhaAluno = Utils.lerInteiro("  Selecione o aluno (0 para voltar):", scanner);
            if (escolhaAluno == 0) return;
            if (escolhaAluno < 1 || escolhaAluno > alunosDaUC.size()) {
                System.out.println("  [!] Opção inválida.");
                continue;
            }
            Estudante alunoEscolhido = alunosDaUC.get(escolhaAluno - 1);

            Inscricao inscricaoAluno = estudanteController.obterInscricaoAtual(alunoEscolhido);
            if (inscricaoAluno != null && inscricaoAluno.getAvaliacoes() != null
                    && indiceMomento < inscricaoAluno.getAvaliacoes().size()) {
                Avaliacao avExistente = inscricaoAluno.getAvaliacoes().get(indiceMomento);
                if (avExistente != null && avExistente.isLancada()) {
                    System.out.println("  [!] Já existe uma nota lançada: " + String.format("%.1f", avExistente.getNota()) + "/20.");
                    if (!Utils.confirmar("Deseja substituir esta nota?", scanner)) continue;
                }
            }

            double nota = Utils.lerDouble("  Nota (0-20): ", scanner);
            estudanteController.lancarNotaMomento(alunoEscolhido, ucEscolhida, indiceMomento, nota);
            System.out.println("  [✓] Nota " + String.format("%.1f", nota) + " registada para " + alunoEscolhido.getNome() + ".");
        }
    }

    // ── Auxiliares ─────────────────────────────────────────────────────────────

    private UnidadeCurricular selecionarUC() {
        ArrayList<UnidadeCurricular> ucs = unidadeCurricularController.listarUnidades();
        if (ucs.isEmpty()) {
            System.out.println("  [!] Não existem UCs registadas.");
            Utils.pausar(scanner);
            return null;
        }
        System.out.println("  UCs disponíveis:");
        for (int i = 0; i < ucs.size(); i++) {
            UnidadeCurricular uc = ucs.get(i);
            System.out.println("  " + (i + 1) + ". " + uc.getNome()
                    + " (Ano " + uc.getAnoCurricular() + ")"
                    + " — " + cursosComUC(uc));
        }
        int escolha = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
        if (escolha == 0) return null;
        if (escolha < 1 || escolha > ucs.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return null;
        }
        return ucs.get(escolha - 1);
    }

    private String cursosComUC(UnidadeCurricular uc) {
        StringBuilder sb = new StringBuilder();
        for (Curso c : cursoController.listarCursos()) {
            if (c.getUnidades().contains(uc)) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(c.getNomeCurso());
            }
        }
        return sb.length() > 0 ? sb.toString() : "(sem curso)";
    }
}
