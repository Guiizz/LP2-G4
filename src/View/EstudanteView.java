package View;

import Controller.EstudanteController;
import Controller.HorarioController;
import Controller.InscricaoController;
import Controller.JustificacaoController;
import Controller.PresencaController;
import Controller.AnoLetivoController;
import Controller.CursoController;
import Model.*;
import Utils.PasswordUtils;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EstudanteView {

    private final EstudanteController estudanteController;
    private final InscricaoController inscricaoController;
    private final HorarioController horarioController;
    private final PresencaController presencaController;
    private final JustificacaoController justificacaoController;
    private final AnoLetivoController anoLetivoController;
    private final CursoController cursoController;
    private final Scanner scanner;

    public EstudanteView(EstudanteController estudanteController, InscricaoController inscricaoController, HorarioController horarioController, PresencaController presencaController, JustificacaoController justificacaoController, AnoLetivoController anoLetivoController, CursoController cursoController, Scanner scanner) {
        this.estudanteController = estudanteController;
        this.inscricaoController = inscricaoController;
        this.horarioController = horarioController;
        this.presencaController = presencaController;
        this.justificacaoController = justificacaoController;
        this.anoLetivoController = anoLetivoController;
        this.cursoController = cursoController;
        this.scanner = scanner;
    }

    public void iniciar(Estudante estudante) {
        String[] opcoes = {
                "A minha Área",
                "Presenças e Faltas",
                "A minha Conta"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ÁREA DO ESTUDANTE — " + estudante.getNome(), opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: menuMinhaArea(estudante); break;
                    case 2: menuPresencasEFaltas(estudante); break;
                    case 3: menuMinhaConta(estudante); break;
                    case 0: System.out.println("  A terminar sessão..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void menuMinhaArea(Estudante estudante) {
        String[] opcoes = {
                "Ver a minha Ficha",
                "Inscrições",
                "Avaliações",
                "Horário",
                "Propinas"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("A MINHA ÁREA", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: verFicha(estudante); break;
                    case 2: new InscricaoView(inscricaoController, scanner).iniciar(estudante); break;
                    case 3: verAvaliacoes(estudante); break;
                    case 4: verHorario(estudante); break;
                    case 5: verPropinas(estudante); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void menuPresencasEFaltas(Estudante estudante) {
        String[] opcoes = {
                "Marcar Presença",
                "Ver Assiduidade",
                "Justificar Falta",
                "Ver Justificações"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("PRESENÇAS E FALTAS", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: marcarPresenca(estudante); break;
                    case 2: verAssiduidade(estudante); break;
                    case 3: justificarFalta(estudante); break;
                    case 4: verJustificacoes(estudante); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void menuMinhaConta(Estudante estudante) {
        String[] opcoes = {
                "Atualizar Morada",
                "Alterar Password"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("A MINHA CONTA", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: atualizar(estudante); break;
                    case 2: alterarPassword(estudante); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void verFicha(Estudante estudante) {
        Utils.tituloPagina("A MINHA CONTA", "A minha Ficha");
        System.out.println(estudante.toStringDetalhado());
        Utils.pausar(scanner);
    }

    private void verAvaliacoes(Estudante estudante) {
        ArrayList<Inscricao> inscricoes = estudante.getInscricoes();
        if (inscricoes.isEmpty()) {
            Utils.tituloPagina("SITUAÇÃO ACADÉMICA", "As minhas Avaliações");
            System.out.println("  (sem inscrições registadas)");
            Utils.pausar(scanner);
            return;
        }

        // Agrupar as avaliações por UC (parallel lists, preservando a ordem)
        List<String> nomesUC = new ArrayList<>();
        List<String> contexto = new ArrayList<>();
        List<List<Avaliacao>> momentosPorUC = new ArrayList<>();

        for (Inscricao i : inscricoes) {
            if (i.getAvaliacoes() == null) continue;
            for (Avaliacao av : i.getAvaliacoes()) {
                String nomeUC = (av.getUc() != null && !av.getUc().isEmpty())
                        ? av.getUc().get(0).getNome() : "—";
                int idx = -1;
                for (int k = 0; k < nomesUC.size(); k++) {
                    if (nomesUC.get(k).equals(nomeUC)
                            && contexto.get(k).equals(String.valueOf(i.getAnoDeCurso()))) { idx = k; break; }
                }
                if (idx < 0) {
                    nomesUC.add(nomeUC);
                    contexto.add(String.valueOf(i.getAnoDeCurso()));
                    momentosPorUC.add(new ArrayList<>());
                    idx = nomesUC.size() - 1;
                }
                momentosPorUC.get(idx).add(av);
            }
        }

        do {
            Utils.tituloPagina("SITUAÇÃO ACADÉMICA", "As minhas Avaliações");

            if (nomesUC.isEmpty()) {
                System.out.println("  (sem avaliações registadas)");
            } else {
                System.out.printf("  %-4s %-25s %-6s %s%n", "#", "UC", "Ano", "Nota Final");
                System.out.println("  " + "─".repeat(55));
                for (int k = 0; k < nomesUC.size(); k++) {
                    System.out.printf("  %-4d %-25s %-6s %s%n",
                            k + 1, nomesUC.get(k), contexto.get(k) + "º", notaFinalUC(momentosPorUC.get(k)));
                }
                System.out.println("  " + "─".repeat(55));
            }

            // UCs em atraso (resumo)
            ArrayList<Avaliacao> ucsEmAtraso = estudante.getUCsEmAtraso();
            if (ucsEmAtraso != null && !ucsEmAtraso.isEmpty()) {
                System.out.println("\n  UCs em atraso: " + ucsEmAtraso.size());
            }

            if (nomesUC.isEmpty()) { Utils.pausar(scanner); return; }

            int escolha = Utils.lerInteiro("\n  Selecione uma UC para ver os momentos (0 para voltar): ", scanner);
            if (escolha == 0) return;
            if (escolha < 1 || escolha > nomesUC.size()) {
                System.out.println("  [!] Opção inválida."); Utils.pausar(scanner); continue;
            }
            mostrarMomentosUC(nomesUC.get(escolha - 1), momentosPorUC.get(escolha - 1));
        } while (true);
    }

    /** Calcula a nota final ponderada de uma UC a partir dos seus momentos. */
    private String notaFinalUC(List<Avaliacao> momentos) {
        double soma = 0, peso = 0;
        for (Avaliacao av : momentos) {
            if (av.isLancada()) {
                soma += av.getNota() * av.getPeso() / 100.0;
                peso += av.getPeso();
            }
        }
        if (peso <= 0) return "Pendente";
        double media = soma * 100.0 / peso;
        if (peso < 100) return String.format("%.1f (parcial)", media);
        return String.format("%.1f  %s", media, media >= 10 ? "✓" : "✗");
    }

    /** Detalhe dos momentos de uma UC. */
    private void mostrarMomentosUC(String nomeUC, List<Avaliacao> momentos) {
        Utils.tituloPagina("SITUAÇÃO ACADÉMICA", "Momentos — " + nomeUC);
        System.out.printf("  %-5s %-8s %-10s %s%n", "#", "Peso", "Nota", "Estado");
        System.out.println("  " + "─".repeat(40));
        for (int k = 0; k < momentos.size(); k++) {
            Avaliacao av = momentos.get(k);
            String estado = av.isLancada()
                    ? (av.isAprovado() ? "✓ Aprovado" : "✗ Reprovado")
                    : "Pendente";
            System.out.printf("  %-5d %-8s %-10s %s%n",
                    k + 1, String.format("%.0f%%", av.getPeso()), av.getNotaFormatada(), estado);
        }
        System.out.println("  " + "─".repeat(40));
        System.out.println("  Nota final: " + notaFinalUC(momentos) + "/20");
        Utils.pausar(scanner);
    }

    private void verAssiduidade(Estudante estudante) {
        Utils.tituloPagina("PRESENÇAS E FALTAS", "A minha Assiduidade");
        Inscricao insc = estudanteController.obterInscricaoAtual(estudante);
        if (insc == null) {
            System.out.println("  (sem inscrição ativa)");
            Utils.pausar(scanner);
            return;
        }
        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        if (anoAberto == null) {
            System.out.println("  [!] Não existe ano letivo aberto.");
            Utils.pausar(scanner);
            return;
        }
        int anoLetivo = anoAberto.getAno();
        String nomeCurso = insc.getCurso().getNomeCurso();

        System.out.println("  Curso: " + nomeCurso + " | " + insc.getAnoDeCurso() + ".º Ano | " + anoLetivo + "/" + (anoLetivo + 1));
        System.out.println();

        Horario horario = horarioController.obterHorario(nomeCurso, insc.getAnoDeCurso(), anoLetivo);
        if (horario == null) {
            System.out.println("  [!] Não existe horário definido para o seu curso/ano.");
            Utils.pausar(scanner);
            return;
        }
        List<String> ucsNoHorario = new ArrayList<>();
        for (BlocoHorario b : horario.getBlocos()) {
            if (!ucsNoHorario.contains(b.getNomeUC())) ucsNoHorario.add(b.getNomeUC());
        }
        if (ucsNoHorario.isEmpty()) {
            System.out.println("  [!] Não existe horário definido para o seu curso/ano.");
            Utils.pausar(scanner);
            return;
        }

        for (String nomeUC : ucsNoHorario) {
            List<RegistoAula> todasAulas = presencaController.listarAulasPorUC(nomeUC, nomeCurso, anoLetivo);
            List<RegistoAula> terminadas = new ArrayList<>();
            for (RegistoAula r : todasAulas) { if (r.isTerminada()) terminadas.add(r); }
            long total = terminadas.size();
            List<RegistoAula> faltas = presencaController.listarAulasSemPresencaEstudante(
                    estudante.getNumMecanografico(), nomeUC, nomeCurso, anoLetivo);
            int faltasCount = faltas.size();
            long presentes = total - faltasCount;
            String presenca = total > 0 ? presentes + "/" + total : "—";
            String estado = total == 0 ? "—" : (faltasCount == 0 ? "✓ Sem faltas" : "[!] " + faltasCount + " falta(s)");

            System.out.println("  " + "─".repeat(62));
            System.out.printf("  %-28s %-12s %-8s %s%n", nomeUC, presenca, faltasCount + " falta(s)", estado);

            if (!terminadas.isEmpty()) {
                java.util.Set<String> faltasChaves = new java.util.HashSet<>();
                for (RegistoAula f : faltas) faltasChaves.add(f.getData() + "|" + f.getHoraInicio());
                for (RegistoAula aula : terminadas) {
                    boolean faltou = faltasChaves.contains(aula.getData() + "|" + aula.getHoraInicio());
                    System.out.printf("    %-12s %-8s  %s%n",
                            aula.getData(), aula.getHoraInicio(),
                            faltou ? "✗ Falta" : "✓ Presente");
                }
            }
        }
        System.out.println("  " + "─".repeat(62));
        Utils.pausar(scanner);
    }

    private void atualizar(Estudante estudante) {
        Utils.tituloPagina("A MINHA CONTA", "Atualizar a minha Morada");
        System.out.println("  Nome, nº mecanográfico, email, NIF e data de nascimento não são editáveis.");
        System.out.println("  Morada atual: " + estudante.getMorada());

        String novaMorada = Utils.lerCampo("  Nova morada: ", scanner);
        estudanteController.atualizarMoradaPropria(estudante, novaMorada);

        System.out.println("  [✓] Morada atualizada com sucesso.");
        Utils.pausar(scanner);
    }

    private void verHorario(Estudante estudante) {
        Utils.tituloPagina("HORÁRIOS", "O meu Horário");
        Inscricao insc = estudanteController.obterInscricaoAtual(estudante);
        if (insc == null) {
            System.out.println("  (sem inscrição ativa)");
            Utils.pausar(scanner);
            return;
        }
        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : insc.getAnoLetivo();
        Horario horario = horarioController.obterHorario(
                insc.getCurso().getNomeCurso(), insc.getAnoDeCurso(), anoLetivo);
        if (horario == null) {
            System.out.println("  [!] Não existe horário definido para o seu curso/ano.");
            Utils.pausar(scanner);
            return;
        }
        new HorarioView(horarioController, cursoController, null, anoLetivoController, scanner)
                .imprimirHorario(horario);
        Utils.pausar(scanner);
    }

    private void marcarPresenca(Estudante estudante) {
        Utils.tituloPagina("PRESENÇAS E FALTAS", "Marcar a minha Presença");
        Inscricao insc = estudanteController.obterInscricaoAtual(estudante);
        if (insc == null) {
            System.out.println("  (sem inscrição ativa)");
            Utils.pausar(scanner);
            return;
        }
        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        if (anoAberto == null) {
            System.out.println("  [!] Não existe ano letivo aberto.");
            Utils.pausar(scanner);
            return;
        }
        int anoLetivo = anoAberto.getAno();
        String nomeCurso = insc.getCurso().getNomeCurso();

        Horario horario = horarioController.obterHorario(nomeCurso, insc.getAnoDeCurso(), anoLetivo);
        if (horario == null || horario.getBlocos().isEmpty()) {
            System.out.println("  [!] Não existe horário definido para o seu curso/ano.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Curso: " + nomeCurso + " | " + insc.getAnoDeCurso() + ".º Ano | " + anoLetivo + "/" + (anoLetivo + 1));

        // Mostrar UCs distintas no horário
        List<String> ucsNoHorario = new ArrayList<>();
        for (BlocoHorario b : horario.getBlocos()) {
            if (!ucsNoHorario.contains(b.getNomeUC())) ucsNoHorario.add(b.getNomeUC());
        }
        System.out.println("\n  Unidades Curriculares:");
        for (int i = 0; i < ucsNoHorario.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + ucsNoHorario.get(i));
        }
        int escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
        if (escolhaUC == 0) return;
        if (escolhaUC < 1 || escolhaUC > ucsNoHorario.size()) {
            System.out.println("  [!] Opção inválida. Selecione entre 1 e " + ucsNoHorario.size() + ".");
            Utils.pausar(scanner);
            return;
        }
        String nomeUC = ucsNoHorario.get(escolhaUC - 1);

        // Aulas ativas (iniciadas pelo docente) onde o estudante ainda não marcou presença
        List<RegistoAula> aulasPorMarcar = presencaController.listarAulasAtivasSemPresencaEstudante(estudante.getNumMecanografico(), nomeUC, nomeCurso, anoLetivo);
        if (aulasPorMarcar.isEmpty()) {
            System.out.println("  (nenhuma aula em curso para marcar presença — aguarde que o docente inicie a aula)");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Aulas com presença do docente registada:");
        for (int i = 0; i < aulasPorMarcar.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + aulasPorMarcar.get(i));
        }
        int escolhaAula = Utils.lerInteiro("  Selecione a aula (0 para voltar): ", scanner);
        if (escolhaAula == 0) return;
        if (escolhaAula < 1 || escolhaAula > aulasPorMarcar.size()) {
            System.out.println("  [!] Opção inválida. Selecione entre 1 e " + aulasPorMarcar.size() + ".");
            Utils.pausar(scanner);
            return;
        }
        RegistoAula aula = aulasPorMarcar.get(escolhaAula - 1);
        presencaController.marcarPresencaEstudante(estudante.getNumMecanografico(), nomeUC, nomeCurso, anoLetivo, aula.getData(), aula.getHoraInicio());
        System.out.println("  [✓] Presença registada para " + nomeUC + " em " + aula.getData() + " às " + aula.getHoraInicio() + ".");
        Utils.pausar(scanner);
    }

    private void justificarFalta(Estudante estudante) {
        Utils.tituloPagina("PRESENÇAS E FALTAS", "Justificar Falta");
        Inscricao insc = estudanteController.obterInscricaoAtual(estudante);
        if (insc == null) {
            System.out.println("  (sem inscrição ativa)");
            Utils.pausar(scanner);
            return;
        }
        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        if (anoAberto == null) {
            System.out.println("  [!] Não existe ano letivo aberto.");
            Utils.pausar(scanner);
            return;
        }
        int anoLetivo = anoAberto.getAno();
        String nomeCurso = insc.getCurso().getNomeCurso();
        Horario horario = horarioController.obterHorario(nomeCurso, insc.getAnoDeCurso(), anoLetivo);
        if (horario == null) {
            System.out.println("  [!] Não existe horário definido para o seu curso/ano.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Curso: " + nomeCurso + " | " + insc.getAnoDeCurso() + ".º Ano | " + anoLetivo + "/" + (anoLetivo + 1));

        // UCs no horário
        List<String> ucsNoHorario = new ArrayList<>();
        for (BlocoHorario b : horario.getBlocos()) {
            if (!ucsNoHorario.contains(b.getNomeUC())) ucsNoHorario.add(b.getNomeUC());
        }
        if (ucsNoHorario.isEmpty()) {
            System.out.println("  [!] Não existe horário definido.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Unidades Curriculares:");
        for (int i = 0; i < ucsNoHorario.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + ucsNoHorario.get(i));
        }
        int escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
        if (escolhaUC == 0) return;
        if (escolhaUC < 1 || escolhaUC > ucsNoHorario.size()) {
            System.out.println("  [!] Opção inválida. Selecione entre 1 e " + ucsNoHorario.size() + ".");
            Utils.pausar(scanner);
            return;
        }
        String nomeUC = ucsNoHorario.get(escolhaUC - 1);

        // Aulas marcadas pelo docente onde o estudante foi ausente
        List<RegistoAula> ausencias = presencaController.listarAulasSemPresencaEstudante(estudante.getNumMecanografico(), nomeUC, nomeCurso, anoLetivo);
        if (ausencias.isEmpty()) {
            System.out.println("  (não tem faltas por justificar nesta UC)");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Faltas por justificar:");
        for (int i = 0; i < ausencias.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + ausencias.get(i));
        }
        int escolhaAula = Utils.lerInteiro("  Selecione a falta (0 para voltar): ", scanner);
        if (escolhaAula == 0) return;
        if (escolhaAula < 1 || escolhaAula > ausencias.size()) {
            System.out.println("  [!] Opção inválida. Selecione entre 1 e " + ausencias.size() + ".");
            Utils.pausar(scanner);
            return;
        }
        RegistoAula aula = ausencias.get(escolhaAula - 1);

        // Tipo de justificação
        List<TipoJustificacao> tipos = justificacaoController.listarTipos();
        System.out.println("\n  Tipos de justificação:");
        for (int i = 0; i < tipos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + tipos.get(i));
        }
        System.out.println("  " + (tipos.size() + 1) + ". Outro");
        int totalOpcoes = tipos.size() + 1;
        int escolhaTipo = Utils.lerInteiro("  Selecione o tipo (0 para voltar): ", scanner);
        if (escolhaTipo == 0) return;
        if (escolhaTipo < 1 || escolhaTipo > totalOpcoes) {
            System.out.println("  [!] Opção inválida. Selecione entre 1 e " + totalOpcoes + ".");
            Utils.pausar(scanner);
            return;
        }
        String nomeTipo = (escolhaTipo == totalOpcoes) ? "Outro" : tipos.get(escolhaTipo - 1).getNome();

        justificacaoController.pedirJustificacao(estudante.getNumMecanografico(), nomeUC, nomeCurso, anoLetivo, aula.getData(), aula.getHoraInicio(), nomeTipo);
        System.out.println("  [✓] Pedido de justificação enviado ao gestor.");
        Utils.pausar(scanner);
    }

    private void verJustificacoes(Estudante estudante) {
        Utils.tituloPagina("PRESENÇAS E FALTAS", "As minhas Justificações");
        List<JustificacaoFalta> lista = justificacaoController
                .listarPorEstudante(estudante.getNumMecanografico());
        if (lista.isEmpty()) {
            System.out.println("  (sem pedidos de justificação)");
        } else {
            System.out.printf("  %-20s %-12s %-18s %s%n", "UC", "Data", "Tipo", "Estado");
            System.out.println("  " + "─".repeat(65));
            for (JustificacaoFalta j : lista) {
                System.out.printf("  %-20s %-12s %-18s %s%n",
                        j.getNomeUC(),
                        j.getDataAula() + " " + j.getHoraInicio(),
                        j.getNomeTipoJustificacao(),
                        j.getEstado());
            }
        }
        Utils.pausar(scanner);
    }

    private void alterarPassword(Estudante estudante) {
        Utils.tituloPagina("A MINHA CONTA", "Alterar Password");
        System.out.print("  Password atual: ");
        String atual = lerPasswordMascarada();
        System.out.print("  Nova password : ");
        String nova = lerPasswordMascarada();
        System.out.print("  Confirmar     : ");
        String confirmar = lerPasswordMascarada();

        if (!PasswordUtils.verificarPassword(atual, estudante.getPassword())) {
            System.out.println("  [!] A password atual está incorreta.");
            Utils.pausar(scanner);
            return;
        }
        if (!nova.equals(confirmar)) {
            System.out.println("  [!] As passwords não coincidem.");
            Utils.pausar(scanner);
            return;
        }
        estudanteController.alterarPassword(estudante, nova);
        System.out.println("  [✓] Password alterada com sucesso.");
        Utils.pausar(scanner);
    }

    /**
     * Lê uma password ocultando os caracteres (usa System.console se disponível).
     */
    private String lerPasswordMascarada() {
        if (System.console() != null) {
            char[] chars = System.console().readPassword();
            return chars != null ? new String(chars) : "";
        }
        return scanner.nextLine().trim();
    }

    private void verPropinas(Estudante estudante) {
        Utils.tituloPagina("SITUAÇÃO ACADÉMICA", "As minhas Propinas");

        Inscricao inscricaoAtual = estudanteController.obterInscricaoAtual(estudante);
        if (inscricaoAtual == null) {
            System.out.println("  (sem inscrição ativa)");
            Utils.pausar(scanner);
            return;
        }

        Propina propina = inscricaoAtual.getPropina();
        if (propina == null) {
            System.out.println("  (sem propina associada)");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Ano letivo: " + inscricaoAtual.getAnoLetivo()
                + "/" + (inscricaoAtual.getAnoLetivo() + 1));
        System.out.println("  " + propina);

        System.out.println("\n  Histórico de pagamentos:");
        if (propina.getHistoricoPagamentos().isEmpty()) {
            System.out.println("    (sem pagamentos registados)");
        } else {
            for (Pagamento p : propina.getHistoricoPagamentos()) {
                System.out.println("  " + p);
            }
        }

        if (!propina.isTotalmentePaga()) {
            System.out.println("\n  Saldo em dívida: "
                    + String.format("%.2f €", propina.getSaldoEmDebito()));

            // O pagamento só é permitido depois de o curso estar iniciado (ATIVO)
            boolean cursoAtivo = inscricaoAtual.getCurso() != null
                    && "ATIVO".equalsIgnoreCase(inscricaoAtual.getCurso().getEstado());

            if (!cursoAtivo) {
                System.out.println("  [!] O pagamento ficará disponível após o curso ser iniciado.");
            } else {
                if (Utils.confirmar("Deseja efetuar um pagamento?", scanner)) {
                    double valor;
                    do {
                        valor = Utils.lerDouble("  Valor a pagar (€): ", scanner);
                        if (valor <= 0)
                            System.out.println("  [!] O valor deve ser superior a 0 €.");
                    } while (valor <= 0);
                    estudanteController.pagarPropina(estudante, valor);
                    System.out.printf("  [✓] Pagamento de %.2f € registado.%n", valor);
                    if (propina.isTotalmentePaga())
                        System.out.println("  [✓] Propina totalmente liquidada!");
                    else
                        System.out.printf("  Saldo restante: %.2f €%n", propina.getSaldoEmDebito());
                }
            }
        } else {
            System.out.println("\n  [✓] Propina totalmente paga.");
        }

        Utils.pausar(scanner);
    }
}