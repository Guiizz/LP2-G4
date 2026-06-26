package View;

import Controller.AnoLetivoController;
import Controller.AvaliacaoController;
import Controller.CursoController;
import Controller.DocenteController;
import Controller.EstudanteController;
import Controller.HorarioController;
import Controller.PresencaController;
import Controller.UnidadeCurricularController;
import Model.*;
import Utils.PasswordUtils;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DocenteView {

    private final DocenteController docenteController;
    private final EstudanteController estudanteController;
    private final AvaliacaoController avaliacaoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final AnoLetivoController anoLetivoController;
    private final CursoController cursoController;
    private final HorarioController horarioController;
    private final PresencaController presencaController;
    private final Scanner scanner;

    public DocenteView(DocenteController docenteController,
                       EstudanteController estudanteController,
                       AvaliacaoController avaliacaoController,
                       UnidadeCurricularController unidadeCurricularController,
                       AnoLetivoController anoLetivoController,
                       CursoController cursoController,
                       HorarioController horarioController,
                       PresencaController presencaController,
                       Scanner scanner) {
        this.docenteController = docenteController;
        this.estudanteController = estudanteController;
        this.avaliacaoController = avaliacaoController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.anoLetivoController = anoLetivoController;
        this.cursoController = cursoController;
        this.horarioController = horarioController;
        this.presencaController = presencaController;
        this.scanner = scanner;
    }

    public void iniciar(Docente docente) {
        String[] opcoes = {
                "As minhas UCs",
                "Os meus Alunos",
                "Presenças",
                "A minha Conta"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ÁREA DO DOCENTE — " + docente.getNome(), opcoes, "Sair", scanner);
            try {
                switch (opcao) {
                    case 1: menuMinhasUCs(docente); break;
                    case 2: menuAlunos(docente); break;
                    case 3: menuPresencas(docente); break;
                    case 4: menuConta(docente); break;
                    case 0: System.out.println("  A terminar sessão..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    // ── Submenus ──────────────────────────────────────────────────────────────

    private void menuMinhasUCs(Docente docente) {
        String[] opcoes = {
                "Ver as minhas Unidades Curriculares",
                "Ver o meu Horário",
                "Gerir Momentos de Avaliação"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("AS MINHAS UCS", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: verUCs(docente); break;
                    case 2: verHorario(docente); break;
                    case 3: gerirMomentos(docente); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void menuAlunos(Docente docente) {
        String[] opcoes = {
                "Ver os meus Alunos",
                "Lançar Nota",
                "Ver Resultados da UC"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("OS MEUS ALUNOS", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: verAlunos(docente); break;
                    case 2: lancarAvaliacao(docente); break;
                    case 3: verResultados(docente); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void menuPresencas(Docente docente) {
        String[] opcoes = {
                "Iniciar Aula",
                "Ver Presenças dos Alunos",
                "Terminar Aula"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("PRESENÇAS", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: marcarPresenca(docente); break;
                    case 2: verPresencasAlunos(docente); break;
                    case 3: terminarAula(docente); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void menuConta(Docente docente) {
        String[] opcoes = {
                "Ver a minha Ficha",
                "Atualizar os meus Dados",
                "Alterar Password"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("A MINHA CONTA", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: verFicha(docente); break;
                    case 2: atualizar(docente); break;
                    case 3: alterarPassword(docente); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    // ── Ações — As minhas UCs ─────────────────────────────────────────────────

    private void verUCs(Docente docente) {
        Utils.tituloPagina("AS MINHAS UCS", "As minhas Unidades Curriculares");
        ArrayList<UnidadeCurricular> todasUCs = unidadeCurricularController.listarUnidades();
        List<Curso> todosCursos = cursoController.listarCursos();
        boolean encontrou = false;
        for (UnidadeCurricular uc : todasUCs) {
            if (docente.getSigla().equalsIgnoreCase(uc.getDocenteResponsavel())) {
                StringBuilder cursos = new StringBuilder();
                for (Curso c : todosCursos) {
                    if (c.getUnidades().contains(uc)) {
                        if (cursos.length() > 0) cursos.append(", ");
                        cursos.append(c.getNomeCurso());
                    }
                }
                String cursosStr = cursos.length() > 0 ? cursos.toString() : "sem curso";
                System.out.println("  - " + uc.getNome()
                        + " | " + (uc.isAtiva() ? "Ativa" : "Inativa")
                        + " | " + cursosStr);
                encontrou = true;
            }
        }
        if (!encontrou) System.out.println("  (sem unidades curriculares atribuídas)");
        Utils.pausar(scanner);
    }

    private void verHorario(Docente docente) {
        Utils.tituloPagina("AS MINHAS UCS", "O meu Horário");
        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : 0;

        List<String> nomesUCs = new ArrayList<>();
        for (UnidadeCurricular u : unidadeCurricularController.listarUnidades()) {
            if (docente.getSigla().equalsIgnoreCase(u.getDocenteResponsavel())) nomesUCs.add(u.getNome());
        }

        boolean encontrou = false;
        for (Curso c : cursoController.listarCursos()) {
            for (int ano = 1; ano <= 3; ano++) {
                Horario h = horarioController.obterHorario(c.getNomeCurso(), ano, anoLetivo);
                if (h == null) continue;
                List<BlocoHorario> blocos = new ArrayList<>();
                for (BlocoHorario b : h.getBlocos()) {
                    for (String nome : nomesUCs) {
                        if (nome.equalsIgnoreCase(b.getNomeUC())) { blocos.add(b); break; }
                    }
                }
                if (blocos.isEmpty()) continue;
                Horario horarioDocente = new Horario(c.getNomeCurso(), ano, anoLetivo);
                for (BlocoHorario b : blocos) horarioDocente.adicionarBloco(b);
                System.out.println("\n  " + c.getNomeCurso() + " — " + ano + ".º Ano:");
                new HorarioView(horarioController, cursoController, unidadeCurricularController, anoLetivoController, scanner).imprimirHorario(horarioDocente);
                encontrou = true;
            }
        }
        if (!encontrou) System.out.println("  (sem horário definido para as suas UCs)");
        Utils.pausar(scanner);
    }

    private void gerirMomentos(Docente docente) {
        Utils.tituloPagina("AS MINHAS UCS", "Gerir Momentos de Avaliação");

        ArrayList<UnidadeCurricular> minhasUCs = minhasUCs(docente);
        if (minhasUCs.isEmpty()) {
            System.out.println("  [!] Não tem UCs atribuídas.");
            Utils.pausar(scanner);
            return;
        }

        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : 0;
        if (anoLetivo == 0) {
            System.out.println("  [!] Não existe ano letivo aberto. Abra um ano letivo para gerir momentos.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  As suas UCs:");
        for (int i = 0; i < minhasUCs.size(); i++) {
            UnidadeCurricular uc = minhasUCs.get(i);
            List<MomentoAvaliacao> m = uc.getMomentosParaAno(anoLetivo);
            System.out.println("  " + (i + 1) + ". " + uc.getNome()
                    + " | " + m.size() + "/3 momentos"
                    + " | " + String.format("%.0f%%", uc.somaPesosParaAno(anoLetivo)));
        }
        int escolha;
        do {
            escolha = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
            if (escolha == 0) return;
            if (escolha < 1 || escolha > minhasUCs.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + minhasUCs.size() + ".");
        } while (escolha < 1 || escolha > minhasUCs.size());
        UnidadeCurricular uc = minhasUCs.get(escolha - 1);

        List<MomentoAvaliacao> momentos = uc.getMomentosParaAno(anoLetivo);
        System.out.println("\n  UC: " + uc.getNome()
                + " | Ano letivo: " + anoLetivo + "/" + (anoLetivo + 1)
                + " | " + momentos.size() + "/3 momentos"
                + " | Soma: " + String.format("%.1f%%", uc.somaPesosParaAno(anoLetivo)));
        if (!momentos.isEmpty()) {
            for (int i = 0; i < momentos.size(); i++) {
                System.out.printf("    %d. %s — %.1f%%%n", i + 1,
                        momentos.get(i).getNome(), momentos.get(i).getPeso());
            }
        }

        if (uc.isAtiva() && !momentos.isEmpty()) {
            System.out.println("\n  [!] A UC está ativa — os momentos deste ano letivo não podem ser alterados.");
            Utils.pausar(scanner);
            return;
        }

        String[] opcoesMomentos = { "Adicionar momento", "Remover momento" };
        int opcao = Utils.mostrarMenu("MOMENTOS DA UC '" + uc.getNome() + "'", opcoesMomentos, scanner);
        if (opcao == 1) adicionarMomento(uc, anoLetivo);
        else if (opcao == 2) removerMomento(uc, momentos);
    }

    private void adicionarMomento(UnidadeCurricular uc, int anoLetivo) {
        List<MomentoAvaliacao> momentos = uc.getMomentosParaAno(anoLetivo);
        if (momentos.size() >= 3) {
            System.out.println("  [!] Já tem 3 momentos para este ano. Remova um antes de adicionar.");
            Utils.pausar(scanner);
            return;
        }
        String nomeCurso = "";
        for (Curso c : cursoController.listarCursos()) {
            if (c.getUnidades().contains(uc)) { nomeCurso = c.getNomeCurso(); break; }
        }
        if (nomeCurso.isEmpty()) {
            System.out.println("  [!] A UC não está associada a nenhum curso.");
            Utils.pausar(scanner);
            return;
        }
        String nomeMomento = Utils.lerCampo("  Nome do momento (ex: Frequência, Exame, Projeto): ", scanner);
        java.util.Date data = Utils.lerDataAvaliacao("  Data do momento (DD/MM/AAAA): ", scanner);
        unidadeCurricularController.adicionarMomento(uc, nomeMomento, nomeCurso, data);
        List<MomentoAvaliacao> atualizados = uc.getMomentosParaAno(anoLetivo);
        System.out.println("  [✓] Momento '" + nomeMomento + "' adicionado. Distribuição atual:");
        for (MomentoAvaliacao m : atualizados) {
            System.out.printf("      %-30s %.0f%%%n", m.getNome(), m.getPeso());
        }
        Utils.pausar(scanner);
    }

    private void removerMomento(UnidadeCurricular uc, List<MomentoAvaliacao> momentos) {
        if (momentos.isEmpty()) {
            System.out.println("  [!] Não existem momentos para remover.");
            Utils.pausar(scanner);
            return;
        }
        System.out.println("\n  Escolha o momento a remover:");
        for (int i = 0; i < momentos.size(); i++) {
            System.out.printf("    %d. %s — %.1f%%%n", i + 1,
                    momentos.get(i).getNome(), momentos.get(i).getPeso());
        }
        int escolha;
        do {
            escolha = Utils.lerInteiro("  Selecione (0 para voltar): ", scanner);
            if (escolha == 0) { Utils.pausar(scanner); return; }
            if (escolha < 1 || escolha > momentos.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + momentos.size() + ".");
        } while (escolha < 1 || escolha > momentos.size());
        MomentoAvaliacao alvo = momentos.get(escolha - 1);
        int indiceReal = uc.getMomentosAvaliacao().indexOf(alvo);
        if (indiceReal < 0) {
            System.out.println("  [!] Não foi possível localizar o momento.");
            Utils.pausar(scanner);
            return;
        }
        if (!Utils.confirmar("Remover o momento '" + alvo.getNome() + "'?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }
        unidadeCurricularController.removerMomento(uc, indiceReal);
        System.out.println("  [✓] Momento '" + alvo.getNome() + "' removido.");
        Utils.pausar(scanner);
    }

    // ── Ações — Os meus Alunos ────────────────────────────────────────────────

    private void verAlunos(Docente docente) {
        Utils.tituloPagina("OS MEUS ALUNOS", "Os meus Alunos");
        ArrayList<UnidadeCurricular> todasUCs = unidadeCurricularController.listarUnidades();
        ArrayList<Estudante> todosEstudantes = estudanteController.listarEstudante();

        boolean encontrouAluno = false;
        int totalAlunos = 0;
        for (UnidadeCurricular uc : todasUCs) {
            if (docente.getSigla().equalsIgnoreCase(uc.getDocenteResponsavel())) {
                int countUC = 0;
                StringBuilder alunosUC = new StringBuilder();
                for (Estudante e : todosEstudantes) {
                    Inscricao inscricao = estudanteController.obterInscricaoAtual(e);
                    if (inscricao != null && inscricao.getCurso() != null
                            && inscricao.getCurso().getUnidades().contains(uc)) {
                        alunosUC.append("    - ").append(e.getNome())
                                .append(" (").append(e.getNumMecanografico()).append(")\n");
                        countUC++;
                        totalAlunos++;
                        encontrouAluno = true;
                    }
                }
                System.out.println("  UC: " + uc.getNome() + " (" + countUC + " aluno(s))");
                System.out.print(alunosUC);
            }
        }
        if (!encontrouAluno) {
            System.out.println("  (sem alunos associados)");
        } else {
            System.out.println("\n  Total de alunos: " + totalAlunos);
        }
        Utils.pausar(scanner);
    }

    private void lancarAvaliacao(Docente docente) {
        Utils.tituloPagina("OS MEUS ALUNOS", "Lançar Nota");

        ArrayList<UnidadeCurricular> minhasUCs = minhasUCs(docente);
        if (minhasUCs.isEmpty()) {
            System.out.println("  [!] Não tem nenhuma Unidade Curricular atribuída.");
            Utils.pausar(scanner);
            return;
        }

        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : 0;

        System.out.println("\n  As suas Unidades Curriculares:");
        for (int i = 0; i < minhasUCs.size(); i++) {
            UnidadeCurricular uc = minhasUCs.get(i);
            System.out.println("  " + (i + 1) + ". " + uc.getNome()
                    + " | Momentos: " + uc.getMomentosParaAno(anoLetivo).size() + "/3"
                    + " | Estado: " + (uc.isAtiva() ? "Ativa" : "Inativa"));
        }

        int escolhaUC;
        do {
            escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
            if (escolhaUC == 0) return;
            if (escolhaUC < 1 || escolhaUC > minhasUCs.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + minhasUCs.size() + ".");
        } while (escolhaUC < 1 || escolhaUC > minhasUCs.size());
        UnidadeCurricular ucEscolhida = minhasUCs.get(escolhaUC - 1);

        if (!ucEscolhida.isAtiva()) {
            System.out.println("  [!] A UC '" + ucEscolhida.getNome() + "' ainda não está ativa.");
            Utils.pausar(scanner);
            return;
        }

        List<MomentoAvaliacao> momentos = ucEscolhida.getMomentosParaAno(anoLetivo);
        if (momentos == null || momentos.isEmpty()) {
            System.out.println("  [!] A UC '" + ucEscolhida.getNome()
                    + "' não tem momentos de avaliação definidos"
                    + (anoLetivo > 0 ? " para o ano letivo " + anoLetivo + "/" + (anoLetivo + 1) : "") + ".");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Momentos de Avaliação da UC '" + ucEscolhida.getNome() + "':");
        for (int i = 0; i < momentos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + momentos.get(i).getNome()
                    + " (Peso: " + momentos.get(i).getPeso() + "%)");
        }

        int escolhaMomento;
        do {
            escolhaMomento = Utils.lerInteiro("  Selecione o momento (0 para voltar): ", scanner);
            if (escolhaMomento == 0) return;
            if (escolhaMomento < 1 || escolhaMomento > momentos.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + momentos.size() + ".");
        } while (escolhaMomento < 1 || escolhaMomento > momentos.size());
        int indiceMomento = escolhaMomento - 1;
        MomentoAvaliacao momentoEscolhido = momentos.get(indiceMomento);

        ArrayList<Estudante> todosEstudantes = estudanteController.listarEstudante();
        ArrayList<Estudante> alunosDaUC = new ArrayList<>();
        for (Estudante e : todosEstudantes) {
            Inscricao inscricao = estudanteController.obterInscricaoAtual(e);
            if (inscricao != null && inscricao.getCurso() != null
                    && inscricao.getCurso().getUnidades().contains(ucEscolhida)
                    && inscricao.getAnoDeCurso() == inscricao.getCurso().getAnoCurricularDe(ucEscolhida)) {
                alunosDaUC.add(e);
            }
        }
        if (alunosDaUC.isEmpty()) {
            System.out.println("  [!] Não existem alunos inscritos nesta UC.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Alunos - Momento: " + momentoEscolhido.getNome());
        System.out.println("  " + "─".repeat(58));
        System.out.printf("  %-5s %-25s %-12s %s%n", "Nº", "Nome", "Nº Mecano.", "Nota atual");
        System.out.println("  " + "─".repeat(58));
        for (int i = 0; i < alunosDaUC.size(); i++) {
            Estudante e = alunosDaUC.get(i);
            Inscricao insc = estudanteController.obterInscricaoAtual(e);
            String notaAtual = "Pendente";
            Avaliacao av = avaliacaoDaUC(insc, ucEscolhida, indiceMomento);
            if (av != null && av.isLancada()) notaAtual = String.format("%.1f", av.getNota());
            System.out.printf("  %-5d %-25s %-12s %s%n", (i + 1), e.getNome(), e.getNumMecanografico(), notaAtual);
        }
        System.out.println("  " + "─".repeat(58));

        int escolhaAluno;
        do {
            escolhaAluno = Utils.lerInteiro("  Selecione o aluno (0 para voltar): ", scanner);
            if (escolhaAluno == 0) return;
            if (escolhaAluno < 1 || escolhaAluno > alunosDaUC.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + alunosDaUC.size() + ".");
        } while (escolhaAluno < 1 || escolhaAluno > alunosDaUC.size());
        Estudante alunoEscolhido = alunosDaUC.get(escolhaAluno - 1);

        Inscricao inscricaoAluno = estudanteController.obterInscricaoAtual(alunoEscolhido);
        Avaliacao avExistente = avaliacaoDaUC(inscricaoAluno, ucEscolhida, indiceMomento);
        if (avExistente != null && avExistente.isLancada()) {
            System.out.println("  [!] Já existe uma nota lançada: " + String.format("%.1f", avExistente.getNota()) + "/20.");
            if (!Utils.confirmar("Deseja substituir esta nota?", scanner)) {
                Utils.pausar(scanner); return;
            }
        }

        double nota = Utils.lerDouble("  Nota (0-20): ", scanner);
        estudanteController.lancarNotaMomento(alunoEscolhido, ucEscolhida, indiceMomento, nota);
        System.out.println("  [✓] Nota " + String.format("%.1f", nota)
                + " registada para " + alunoEscolhido.getNome()
                + " no momento '" + momentoEscolhido.getNome() + "'.");
        Utils.pausar(scanner);
    }

    private void verResultados(Docente docente) {
        Utils.tituloPagina("OS MEUS ALUNOS", "Resultados da UC");

        ArrayList<UnidadeCurricular> minhasUCs = minhasUCs(docente);
        if (minhasUCs.isEmpty()) {
            System.out.println("  [!] Não tem nenhuma Unidade Curricular atribuída.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  As suas UCs:");
        for (int i = 0; i < minhasUCs.size(); i++) {
            UnidadeCurricular uc = minhasUCs.get(i);
            System.out.println("  " + (i + 1) + ". " + uc.getNome()
                    + " | " + (uc.isAtiva() ? "Ativa" : "Inativa"));
        }

        int escolhaUC;
        do {
            escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
            if (escolhaUC == 0) return;
            if (escolhaUC < 1 || escolhaUC > minhasUCs.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + minhasUCs.size() + ".");
        } while (escolhaUC < 1 || escolhaUC > minhasUCs.size());
        UnidadeCurricular ucEscolhida = minhasUCs.get(escolhaUC - 1);

        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : 0;
        List<MomentoAvaliacao> momentos = ucEscolhida.getMomentosParaAno(anoLetivo);

        System.out.println("\n  UC: " + ucEscolhida.getNome()
                + " | Ano letivo: " + (anoLetivo > 0 ? anoLetivo + "/" + (anoLetivo + 1) : "(sem ano aberto)"));

        if (momentos.isEmpty()) {
            System.out.println("  (sem momentos configurados para este ano letivo)");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  " + "─".repeat(80));
        System.out.printf("  %-12s %-22s", "Nº Mecano.", "Nome");
        for (MomentoAvaliacao m : momentos) {
            System.out.printf("  %-12s", m.getNome() + "(" + (int) m.getPeso() + "%)");
        }
        System.out.printf("  %s%n", "Final");
        System.out.println("  " + "─".repeat(80));

        ArrayList<Estudante> todosEstudantes = estudanteController.listarEstudante();
        boolean temAlunos = false;
        for (Estudante e : todosEstudantes) {
            Inscricao inscricao = estudanteController.obterInscricaoAtual(e);
            if (inscricao == null || inscricao.getCurso() == null) continue;
            if (!inscricao.getCurso().getUnidades().contains(ucEscolhida)) continue;
            if (inscricao.getAnoDeCurso() != inscricao.getCurso().getAnoCurricularDe(ucEscolhida)) continue;

            System.out.printf("  %-12s %-22s", e.getNumMecanografico(), e.getNome());
            double somaFinal = 0;
            double totalPeso = 0;
            for (int i = 0; i < momentos.size(); i++) {
                String nota = "Pendente";
                Avaliacao av = avaliacaoDaUC(inscricao, ucEscolhida, i);
                if (av != null && av.isLancada()) {
                    nota = String.format("%.1f", av.getNota());
                    somaFinal += av.getNota() * momentos.get(i).getPeso() / 100.0;
                    totalPeso += momentos.get(i).getPeso();
                }
                System.out.printf("  %-12s", nota);
            }
            String mediaFinal = totalPeso > 0
                    ? String.format("%.1f%s", somaFinal, totalPeso < 100 ? "*" : (somaFinal >= 10 ? " ✓" : " ✗"))
                    : "—";
            System.out.printf("  %s%n", mediaFinal);
            temAlunos = true;
        }
        if (!temAlunos) System.out.println("  (sem alunos inscritos neste ano curricular)");
        System.out.println("  " + "─".repeat(80));
        System.out.println("  * nota parcial (nem todos os momentos lançados)");
        Utils.pausar(scanner);
    }

    private void marcarPresenca(Docente docente) {
        Utils.tituloPagina("OS MEUS ALUNOS", "Marcar Presença em Aula");
        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        if (anoAberto == null) {
            System.out.println("  [!] Não existe um ano letivo aberto.");
            Utils.pausar(scanner);
            return;
        }
        int anoLetivo = anoAberto.getAno();

        ArrayList<UnidadeCurricular> minhasUCs = minhasUCs(docente);
        if (minhasUCs.isEmpty()) {
            System.out.println("  [!] Não tem UCs atribuídas.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  As suas UCs:");
        for (int i = 0; i < minhasUCs.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + minhasUCs.get(i).getNome());
        }
        int escolhaUC;
        do {
            escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
            if (escolhaUC == 0) return;
            if (escolhaUC < 1 || escolhaUC > minhasUCs.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + minhasUCs.size() + ".");
        } while (escolhaUC < 1 || escolhaUC > minhasUCs.size());
        UnidadeCurricular ucEscolhida = minhasUCs.get(escolhaUC - 1);

        String nomeCurso = null;
        Curso cursoEncontrado = null;
        for (Curso c : cursoController.listarCursos()) {
            if (c.getUnidades().contains(ucEscolhida)) { nomeCurso = c.getNomeCurso(); cursoEncontrado = c; break; }
        }
        if (nomeCurso == null) {
            System.out.println("  [!] A UC não está associada a nenhum curso.");
            Utils.pausar(scanner);
            return;
        }

        int anoUC = cursoEncontrado.getAnoCurricularDe(ucEscolhida);
        Horario horario = horarioController.obterHorario(nomeCurso, anoUC, anoLetivo);
        if (horario == null) {
            System.out.println("  [!] Não existe horário definido para este curso/ano.");
            Utils.pausar(scanner);
            return;
        }
        List<BlocoHorario> blocos = new ArrayList<>();
        for (BlocoHorario b : horario.getBlocos()) {
            if (b.getNomeUC().equalsIgnoreCase(ucEscolhida.getNome())) blocos.add(b);
        }
        if (blocos.isEmpty()) {
            System.out.println("  [!] Não existem blocos de horário para esta UC.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Blocos no horário:");
        for (int i = 0; i < blocos.size(); i++) System.out.println("  " + (i + 1) + ". " + blocos.get(i));
        int escolhaBloco;
        do {
            escolhaBloco = Utils.lerInteiro("  Selecione o bloco (0 para voltar): ", scanner);
            if (escolhaBloco == 0) return;
            if (escolhaBloco < 1 || escolhaBloco > blocos.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + blocos.size() + ".");
        } while (escolhaBloco < 1 || escolhaBloco > blocos.size());
        BlocoHorario blocoEscolhido = blocos.get(escolhaBloco - 1);

        LocalDate data = Utils.lerData("  Data da aula (AAAA-MM-DD): ", scanner);
        presencaController.marcarAulaDocente(docente.getSigla(), ucEscolhida.getNome(),
                nomeCurso, anoLetivo, data, blocoEscolhido.getHoraInicio());
        System.out.println("  [✓] Presença marcada para " + ucEscolhida.getNome()
                + " em " + data + " às " + blocoEscolhido.getHoraInicio() + ".");
        Utils.pausar(scanner);
    }

    private void verPresencasAlunos(Docente docente) {
        Utils.tituloPagina("OS MEUS ALUNOS", "Presenças dos Alunos");
        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : 0;

        ArrayList<UnidadeCurricular> minhasUCs = minhasUCs(docente);
        if (minhasUCs.isEmpty()) {
            System.out.println("  (sem UCs atribuídas)");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  As suas UCs:");
        for (int i = 0; i < minhasUCs.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + minhasUCs.get(i).getNome());
        }
        int escolhaUC;
        do {
            escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
            if (escolhaUC == 0) return;
            if (escolhaUC < 1 || escolhaUC > minhasUCs.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + minhasUCs.size() + ".");
        } while (escolhaUC < 1 || escolhaUC > minhasUCs.size());
        UnidadeCurricular ucEscolhida = minhasUCs.get(escolhaUC - 1);

        String nomeCurso = null;
        Curso cursoPresencas = null;
        for (Curso c : cursoController.listarCursos()) {
            if (c.getUnidades().contains(ucEscolhida)) { nomeCurso = c.getNomeCurso(); cursoPresencas = c; break; }
        }
        if (nomeCurso == null) {
            System.out.println("  [!] A UC não está associada a nenhum curso.");
            Utils.pausar(scanner);
            return;
        }

        List<RegistoAula> aulas = presencaController.listarAulasPorUC(ucEscolhida.getNome(), nomeCurso, anoLetivo);
        if (aulas.isEmpty()) {
            System.out.println("  (sem aulas marcadas para esta UC)");
            Utils.pausar(scanner);
            return;
        }

        List<Presenca> presencas = presencaController.listarPresencasPorUC(ucEscolhida.getNome(), nomeCurso, anoLetivo);
        ArrayList<Estudante> todosEstudantes = estudanteController.listarEstudante();

        System.out.println("\n  UC: " + ucEscolhida.getNome() + " — " + aulas.size() + " aulas marcadas");
        System.out.println("  " + "─".repeat(55));
        System.out.printf("  %-12s %-22s  Presenças%n", "Nº Mecano.", "Nome");
        System.out.println("  " + "─".repeat(55));
        for (Estudante e : todosEstudantes) {
            Inscricao insc = estudanteController.obterInscricaoAtual(e);
            if (insc == null || insc.getCurso() == null) continue;
            if (!insc.getCurso().getNomeCurso().equalsIgnoreCase(nomeCurso)) continue;
            if (insc.getAnoDeCurso() != cursoPresencas.getAnoCurricularDe(ucEscolhida)) continue;
            long presentes = 0;
            for (Presenca p : presencas) {
                if (p.getNumMecanografico().equals(e.getNumMecanografico()) && p.isPresente()) presentes++;
            }
            System.out.printf("  %-12s %-22s  %d/%d%n",
                    e.getNumMecanografico(), e.getNome(), presentes, aulas.size());
        }
        System.out.println("  " + "─".repeat(55));
        Utils.pausar(scanner);
    }

    private void terminarAula(Docente docente) {
        Utils.tituloPagina("OS MEUS ALUNOS", "Terminar Aula");
        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        if (anoAberto == null) {
            System.out.println("  [!] Não existe um ano letivo aberto.");
            Utils.pausar(scanner);
            return;
        }
        int anoLetivo = anoAberto.getAno();

        ArrayList<UnidadeCurricular> minhasUCs = minhasUCs(docente);
        if (minhasUCs.isEmpty()) {
            System.out.println("  [!] Não tem UCs atribuídas.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  As suas UCs:");
        for (int i = 0; i < minhasUCs.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + minhasUCs.get(i).getNome());
        }
        int escolhaUC;
        do {
            escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
            if (escolhaUC == 0) return;
            if (escolhaUC < 1 || escolhaUC > minhasUCs.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + minhasUCs.size() + ".");
        } while (escolhaUC < 1 || escolhaUC > minhasUCs.size());
        UnidadeCurricular ucEscolhida = minhasUCs.get(escolhaUC - 1);

        String nomeCurso = null;
        Curso cursoTerminar = null;
        for (Curso c : cursoController.listarCursos()) {
            if (c.getUnidades().contains(ucEscolhida)) { nomeCurso = c.getNomeCurso(); cursoTerminar = c; break; }
        }
        if (nomeCurso == null) {
            System.out.println("  [!] A UC não está associada a nenhum curso.");
            Utils.pausar(scanner);
            return;
        }

        List<RegistoAula> todasAulas = presencaController.listarAulasPorUC(ucEscolhida.getNome(), nomeCurso, anoLetivo);
        List<RegistoAula> aulasEmCurso = new ArrayList<>();
        for (RegistoAula r : todasAulas) {
            if (!r.isTerminada()) aulasEmCurso.add(r);
        }
        if (aulasEmCurso.isEmpty()) {
            System.out.println("  [!] Não há aulas em curso para esta UC.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Aulas em curso:");
        for (int i = 0; i < aulasEmCurso.size(); i++) {
            RegistoAula r = aulasEmCurso.get(i);
            System.out.println("  " + (i + 1) + ". " + r.getData() + " às " + r.getHoraInicio());
        }
        int escolhaAula;
        do {
            escolhaAula = Utils.lerInteiro("  Selecione a aula (0 para voltar): ", scanner);
            if (escolhaAula == 0) return;
            if (escolhaAula < 1 || escolhaAula > aulasEmCurso.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + aulasEmCurso.size() + ".");
        } while (escolhaAula < 1 || escolhaAula > aulasEmCurso.size());
        RegistoAula aulaEscolhida = aulasEmCurso.get(escolhaAula - 1);

        List<String> numMecanograficos = new ArrayList<>();
        for (Estudante e : estudanteController.listarEstudante()) {
            Inscricao insc = estudanteController.obterInscricaoAtual(e);
            if (insc == null || insc.getCurso() == null) continue;
            if (!insc.getCurso().getNomeCurso().equalsIgnoreCase(nomeCurso)) continue;
            if (insc.getAnoDeCurso() != cursoTerminar.getAnoCurricularDe(ucEscolhida)) continue;
            numMecanograficos.add(e.getNumMecanografico());
        }

        int faltas = presencaController.terminarAula(
                ucEscolhida.getNome(), nomeCurso, anoLetivo,
                aulaEscolhida.getData(), aulaEscolhida.getHoraInicio(),
                numMecanograficos);
        System.out.println("  [✓] Aula terminada. Faltas registadas: " + faltas + ".");
        Utils.pausar(scanner);
    }

    // ── Ações — A minha Conta ─────────────────────────────────────────────────

    private void verFicha(Docente docente) {
        Utils.tituloPagina("A MINHA CONTA", "A minha Ficha");
        System.out.println(docente.toStringDetalhado());
        Utils.pausar(scanner);
    }

    private void atualizar(Docente docente) {
        Utils.tituloPagina("A MINHA CONTA", "Atualizar os meus Dados");
        System.out.println("  Dados atuais: " + docente.getNome() + " | " + docente.getMorada());
        String novoNome = Utils.lerCampo("  Novo nome (Enter para manter): ", scanner);
        String novaMorada = Utils.lerCampo("  Nova morada (Enter para manter): ", scanner);
        if (!novoNome.isEmpty()) docente.setNome(novoNome);
        if (!novaMorada.isEmpty()) docente.setMorada(novaMorada);
        docenteController.atualizarDocente(docente);
        System.out.println("  [✓] Dados atualizados com sucesso.");
        Utils.pausar(scanner);
    }

    private void alterarPassword(Docente docente) {
        Utils.tituloPagina("A MINHA CONTA", "Alterar Password");
        System.out.print("  Password atual: ");
        String atual = lerPasswordMascarada();
        System.out.print("  Nova password : ");
        String nova = lerPasswordMascarada();
        System.out.print("  Confirmar     : ");
        String confirmar = lerPasswordMascarada();
        if (!PasswordUtils.verificarPassword(atual, docente.getPassword())) {
            System.out.println("  [!] A password atual está incorreta.");
            Utils.pausar(scanner);
            return;
        }
        if (!nova.equals(confirmar)) {
            System.out.println("  [!] As passwords não coincidem.");
            Utils.pausar(scanner);
            return;
        }
        docenteController.alterarPassword(docente, nova);
        System.out.println("  [✓] Password alterada com sucesso.");
        Utils.pausar(scanner);
    }

    // ── Utilitários ───────────────────────────────────────────────────────────

    private ArrayList<UnidadeCurricular> minhasUCs(Docente docente) {
        ArrayList<UnidadeCurricular> resultado = new ArrayList<>();
        for (UnidadeCurricular uc : unidadeCurricularController.listarUnidades()) {
            if (docente.getSigla().equalsIgnoreCase(uc.getDocenteResponsavel())) resultado.add(uc);
        }
        return resultado;
    }

    private Avaliacao avaliacaoDaUC(Inscricao insc, UnidadeCurricular uc, int indiceMomento) {
        if (insc == null || insc.getAvaliacoes() == null) return null;
        int count = 0;
        for (Avaliacao a : insc.getAvaliacoes()) {
            if (a != null && a.getUc() != null && a.getUc().contains(uc)) {
                if (count == indiceMomento) return a;
                count++;
            }
        }
        return null;
    }

    private String lerPasswordMascarada() {
        if (System.console() != null) {
            char[] chars = System.console().readPassword();
            return chars != null ? new String(chars) : "";
        }
        return scanner.nextLine().trim();
    }
}
