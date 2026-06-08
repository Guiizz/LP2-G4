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
                "Ver a minha Ficha",
                "Ver as minhas Unidades Curriculares",
                "Ver os meus Alunos",
                "Lançar Nota",
                "Ver Resultados da UC",
                "Ver o meu Horário",
                "Marcar Presença em Aula",
                "Ver Presenças dos Alunos",
                "Atualizar os meus Dados",
                "Alterar Password"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ÁREA DO DOCENTE — " + docente.getNome(), opcoes, scanner);
            try {
                switch (opcao) {
                    case 1:
                        verFicha(docente);
                        break;
                    case 2:
                        verUCs(docente);
                        break;
                    case 3:
                        verAlunos(docente);
                        break;
                    case 4:
                        lancarAvaliacao(docente);
                        break;
                    case 5:
                        verResultados(docente);
                        break;
                    case 6:
                        verHorario(docente);
                        break;
                    case 7:
                        marcarPresenca(docente);
                        break;
                    case 8:
                        verPresencasAlunos(docente);
                        break;
                    case 9:
                        atualizar(docente);
                        break;
                    case 10:
                        alterarPassword(docente);
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

    private void verFicha(Docente docente) {
        Utils.limparEcra();
        Utils.tituloPagina("A minha Ficha");
        System.out.println(docente.toStringDetalhado());
        Utils.pausar(scanner);
    }

    private void verUCs(Docente docente) {
        Utils.limparEcra();
        Utils.tituloPagina("As minhas Unidades Curriculares");
        ArrayList<UnidadeCurricular> todasUCs = unidadeCurricularController.listarUnidades();
        boolean encontrou = false;
        for (UnidadeCurricular uc : todasUCs) {
            if (docente.getSigla().equalsIgnoreCase(uc.getDocenteResponsavel())) {
                StringBuilder cursos = new StringBuilder();
                for (Model.Curso c : cursoController.listarCursos()) {
                    if (c.getUnidades().contains(uc)) {
                        if (cursos.length() > 0) cursos.append(", ");
                        cursos.append(c.getNomeCurso());
                    }
                }
                String cursosStr = cursos.length() > 0 ? cursos.toString() : "sem curso";
                System.out.println("  - " + uc.getNome()
                        + " (Ano " + uc.getAnoCurricular() + ")"
                        + " | " + (uc.isAtiva() ? "Ativa" : "Inativa")
                        + " | " + cursosStr);
                encontrou = true;
            }
        }
        if (!encontrou) System.out.println("  (sem unidades curriculares atribuídas)");
        Utils.pausar(scanner);
    }

    private void verAlunos(Docente docente) {
        Utils.limparEcra();
        Utils.tituloPagina("Os meus Alunos");
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
                    if (inscricao != null && inscricao.getCurso() != null) {
                        if (inscricao.getCurso().getUnidades().contains(uc)) {
                            alunosUC.append("    - ").append(e.getNome())
                                    .append(" (").append(e.getNumMecanografico()).append(")\n");
                            countUC++;
                            totalAlunos++;
                            encontrouAluno = true;
                        }
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
        Utils.limparEcra();
        Utils.tituloPagina("Lançar Nota por Aluno");

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

        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : 0;

        System.out.println("\n  As suas Unidades Curriculares:");
        for (int i = 0; i < minhasUCs.size(); i++) {
            UnidadeCurricular uc = minhasUCs.get(i);
            System.out.println("  " + (i + 1) + ". " + uc.getNome()
                    + " (Ano " + uc.getAnoCurricular() + ")"
                    + " | Momentos: " + uc.getMomentosParaAno(anoLetivo).size() + "/3"
                    + " | Estado: " + (uc.isAtiva() ? "Ativa" : "Inativa"));
        }

        int escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
        if (escolhaUC == 0) return;
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
            MomentoAvaliacao m = momentos.get(i);
            System.out.println("  " + (i + 1) + ". " + m.getNome() + " (Peso: " + m.getPeso() + "%)");
        }

        int escolhaMomento = Utils.lerInteiro("  Selecione o momento (0 para voltar): ", scanner);
        if (escolhaMomento == 0) return;
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
                    && inscricao.getCurso().getUnidades().contains(ucEscolhida)
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
        System.out.println("  " + "─".repeat(58));
        System.out.printf("  %-5s %-25s %-12s %s%n", "Nº", "Nome", "Nº Mecano.", "Nota atual");
        System.out.println("  " + "─".repeat(58));

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

        System.out.println("  " + "─".repeat(58));
        System.out.println("\n  (0 para voltar sem guardar)");

        int escolhaAluno = Utils.lerInteiro("  Selecione o aluno (número): ", scanner);
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
                System.out.println("  [!] Já existe uma nota lançada: " + String.format("%.1f", avExistente.getNota()) + "/20.");
                if (!Utils.confirmar("Deseja substituir esta nota?", scanner)) {
                    Utils.pausar(scanner);
                    return;
                }
            }
        }

        double nota = Utils.lerDouble("  Nota (0-20): ", scanner);

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

    private void verResultados(Docente docente) {
        Utils.limparEcra();
        Utils.tituloPagina("Resultados da UC");

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

        System.out.println("\n  As suas UCs:");
        for (int i = 0; i < minhasUCs.size(); i++) {
            UnidadeCurricular uc = minhasUCs.get(i);
            System.out.println("  " + (i + 1) + ". " + uc.getNome()
                    + " (Ano " + uc.getAnoCurricular() + ")"
                    + " | " + (uc.isAtiva() ? "Ativa" : "Inativa"));
        }

        int escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
        if (escolhaUC == 0) return;
        if (escolhaUC < 1 || escolhaUC > minhasUCs.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return;
        }

        UnidadeCurricular ucEscolhida = minhasUCs.get(escolhaUC - 1);
        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : 0;
        List<MomentoAvaliacao> momentos = ucEscolhida.getMomentosParaAno(anoLetivo);

        System.out.println("\n  UC: " + ucEscolhida.getNome()
                + " | Ano curricular: " + ucEscolhida.getAnoCurricular()
                + " | Ano letivo: " + (anoLetivo > 0 ? anoLetivo + "/" + (anoLetivo + 1) : "(sem ano aberto)"));

        if (momentos.isEmpty()) {
            System.out.println("  (sem momentos configurados para este ano letivo)");
            Utils.pausar(scanner);
            return;
        }

        // Cabeçalho dinâmico com os momentos + coluna Final
        System.out.println("\n  " + "─".repeat(80));
        System.out.printf("  %-12s %-22s", "Nº Mecano.", "Nome");
        for (MomentoAvaliacao m : momentos) {
            System.out.printf("  %-12s", m.getNome() + "(" + (int)m.getPeso() + "%)");
        }
        System.out.printf("  %s%n", "Final");
        System.out.println("  " + "─".repeat(80));

        ArrayList<Estudante> todosEstudantes = estudanteController.listarEstudante();
        boolean temAlunos = false;

        for (Estudante e : todosEstudantes) {
            Inscricao inscricao = estudanteController.obterInscricaoAtual(e);
            if (inscricao == null || inscricao.getCurso() == null) continue;
            if (!inscricao.getCurso().getUnidades().contains(ucEscolhida)) continue;
            if (inscricao.getAnoDeCurso() != ucEscolhida.getAnoCurricular()) continue;

            System.out.printf("  %-12s %-22s", e.getNumMecanografico(), e.getNome());
            double somaFinal = 0;
            double totalPeso = 0;
            for (int i = 0; i < momentos.size(); i++) {
                String nota = "Pendente";
                if (inscricao.getAvaliacoes() != null && i < inscricao.getAvaliacoes().size()) {
                    Avaliacao av = inscricao.getAvaliacoes().get(i);
                    if (av != null && av.isLancada()) {
                        nota = String.format("%.1f", av.getNota());
                        somaFinal += av.getNota() * momentos.get(i).getPeso() / 100.0;
                        totalPeso += momentos.get(i).getPeso();
                    }
                }
                System.out.printf("  %-12s", nota);
            }
            String mediaFinal = totalPeso > 0
                    ? String.format("%.1f%s", somaFinal, totalPeso < 100 ? "*" : (somaFinal >= 10 ? " ✓" : " ✗"))
                    : "—";
            System.out.printf("  %s%n", mediaFinal);
            temAlunos = true;
        }

        if (!temAlunos) {
            System.out.println("  (sem alunos inscritos neste ano curricular)");
        }
        System.out.println("  " + "─".repeat(80));
        System.out.println("  * nota parcial (nem todos os momentos lançados)");
        Utils.pausar(scanner);
    }

    private void verHorario(Docente docente) {
        Utils.limparEcra();
        Utils.tituloPagina("O meu Horário");
        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : 0;

        // Nomes das UCs do docente
        List<String> nomesucs = new ArrayList<>();
        for (UnidadeCurricular u : unidadeCurricularController.listarUnidades()) {
            if (docente.getSigla().equalsIgnoreCase(u.getDocenteResponsavel())) nomesucs.add(u.getNome());
        }

        boolean encontrou = false;
        for (Curso c : cursoController.listarCursos()) {
            for (int ano = 1; ano <= 3; ano++) {
                Horario h = horarioController.obterHorario(c.getNomeCurso(), ano, anoLetivo);
                // Filtrar apenas os blocos das UCs do docente
                List<BlocoHorario> blocos = new ArrayList<>();
                for (BlocoHorario b : h.getBlocos()) {
                    for (String nome : nomesucs) {
                        if (nome.equalsIgnoreCase(b.getNomeUC())) { blocos.add(b); break; }
                    }
                }
                if (blocos.isEmpty()) continue;

                // Criar horário temporário só com os blocos do docente
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

    private void marcarPresenca(Docente docente) {
        Utils.limparEcra();
        Utils.tituloPagina("Marcar Presença em Aula");
        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        if (anoAberto == null) {
            System.out.println("  [!] Não existe um ano letivo aberto.");
            Utils.pausar(scanner);
            return;
        }
        int anoLetivo = anoAberto.getAno();

        ArrayList<UnidadeCurricular> minhasUCs = new ArrayList<>();
        for (UnidadeCurricular uc : unidadeCurricularController.listarUnidades()) {
            if (docente.getSigla().equalsIgnoreCase(uc.getDocenteResponsavel())) minhasUCs.add(uc);
        }
        if (minhasUCs.isEmpty()) {
            System.out.println("  [!] Não tem UCs atribuídas.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  As suas UCs:");
        for (int i = 0; i < minhasUCs.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + minhasUCs.get(i).getNome());
        }
        int escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
        if (escolhaUC == 0) return;
        if (escolhaUC < 1 || escolhaUC > minhasUCs.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return;
        }
        UnidadeCurricular ucEscolhida = minhasUCs.get(escolhaUC - 1);

        // Descobrir o curso da UC
        String nomeCurso = null;
        for (Curso c : cursoController.listarCursos()) {
            if (c.getUnidades().contains(ucEscolhida)) {
                nomeCurso = c.getNomeCurso();
                break;
            }
        }
        if (nomeCurso == null) {
            System.out.println("  [!] A UC não está associada a nenhum curso.");
            Utils.pausar(scanner);
            return;
        }

        // Blocos do horário para esta UC
        Horario horario = horarioController.obterHorario(nomeCurso, ucEscolhida.getAnoCurricular(), anoLetivo);
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
        int escolhaBloco = Utils.lerInteiro("  Selecione o bloco (0 para voltar): ", scanner);
        if (escolhaBloco == 0) return;
        if (escolhaBloco < 1 || escolhaBloco > blocos.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return;
        }
        BlocoHorario blocoEscolhido = blocos.get(escolhaBloco - 1);

        LocalDate data = Utils.lerData("  Data da aula (AAAA-MM-DD): ", scanner);
        presencaController.marcarAulaDocente(docente.getSigla(), ucEscolhida.getNome(),
                nomeCurso, anoLetivo, data, blocoEscolhido.getHoraInicio());
        System.out.println("  [✓] Presença marcada para " + ucEscolhida.getNome()
                + " em " + data + " às " + blocoEscolhido.getHoraInicio() + ".");
        Utils.pausar(scanner);
    }

    private void verPresencasAlunos(Docente docente) {
        Utils.limparEcra();
        Utils.tituloPagina("Presenças dos Alunos");
        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : 0;

        ArrayList<UnidadeCurricular> minhasUCs = new ArrayList<>();
        for (UnidadeCurricular uc : unidadeCurricularController.listarUnidades()) {
            if (docente.getSigla().equalsIgnoreCase(uc.getDocenteResponsavel())) minhasUCs.add(uc);
        }
        if (minhasUCs.isEmpty()) {
            System.out.println("  (sem UCs atribuídas)");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  As suas UCs:");
        for (int i = 0; i < minhasUCs.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + minhasUCs.get(i).getNome());
        }
        int escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
        if (escolhaUC == 0) return;
        if (escolhaUC < 1 || escolhaUC > minhasUCs.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return;
        }
        UnidadeCurricular ucEscolhida = minhasUCs.get(escolhaUC - 1);

        String nomeCurso = null;
        for (Curso c : cursoController.listarCursos()) {
            if (c.getUnidades().contains(ucEscolhida)) {
                nomeCurso = c.getNomeCurso();
                break;
            }
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
            if (insc.getAnoDeCurso() != ucEscolhida.getAnoCurricular()) continue;

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

    private void alterarPassword(Docente docente) {
        Utils.limparEcra();
        Utils.tituloPagina("Alterar Password");
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

    private String lerPasswordMascarada() {
        if (System.console() != null) {
            char[] chars = System.console().readPassword();
            return chars != null ? new String(chars) : "";
        }
        return scanner.nextLine().trim();
    }

    private void atualizar(Docente docente) {
        Utils.limparEcra();
        Utils.tituloPagina("Atualizar os meus Dados");
        System.out.println("  Dados atuais: " + docente.getNome() + " | " + docente.getMorada());

        String novoNome = Utils.lerCampo("  Novo nome (Enter para manter): ", scanner);
        String novaMorada = Utils.lerCampo("  Nova morada (Enter para manter): ", scanner);

        if (!novoNome.isEmpty()) docente.setNome(novoNome);
        if (!novaMorada.isEmpty()) docente.setMorada(novaMorada);

        docenteController.atualizarDocente(docente);
        System.out.println("  [✓] Dados atualizados com sucesso.");
        Utils.pausar(scanner);
    }
}