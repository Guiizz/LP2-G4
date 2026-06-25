package View;

import BLL.HorarioBLL;
import Controller.AnoLetivoController;
import Controller.CursoController;
import Controller.HorarioController;
import Controller.UnidadeCurricularController;
import Model.*;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HorarioView {

    private final HorarioController horarioController;
    private final CursoController cursoController;
    private final UnidadeCurricularController ucController;
    private final AnoLetivoController anoLetivoController;
    private final Scanner scanner;

    public HorarioView(HorarioController horarioController, CursoController cursoController, UnidadeCurricularController ucController, AnoLetivoController anoLetivoController, Scanner scanner) {
        this.horarioController = horarioController;
        this.cursoController = cursoController;
        this.ucController = ucController;
        this.anoLetivoController = anoLetivoController;
        this.scanner = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Ver Horário de um Curso/Ano",
                "Adicionar Bloco ao Horário",
                "Remover Bloco do Horário"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("GESTÃO DE HORÁRIOS", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: verHorario(); break;
                    case 2: adicionarBloco(); break;
                    case 3: removerBloco(); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    // ── Ações ─────────────────────────────────────────────────────────────────

    private void verHorario() {
        Utils.tituloPagina("GESTÃO DE HORÁRIOS", "Ver Horário");
        Curso curso = selecionarCurso();
        if (curso == null) return;
        int anoCurricular = selecionarAnoCurricular();
        if (anoCurricular == 0) return;
        int anoLetivo = obterAnoLetivo();
        Horario horario = horarioController.obterHorario(curso.getNomeCurso(), anoCurricular, anoLetivo);
        imprimirHorario(horario);
        Utils.pausar(scanner);
    }

    private void adicionarBloco() {
        Utils.tituloPagina("GESTÃO DE HORÁRIOS", "Adicionar Bloco ao Horário");

        Curso curso = selecionarCurso();
        if (curso == null) return;
        int anoCurricular = selecionarAnoCurricular();
        if (anoCurricular == 0) return;
        int anoLetivo = obterAnoLetivo();

        List<UnidadeCurricular> ucsAno = new ArrayList<>();
        for (UnidadeCurricular uc : ucController.listarUnidades()) {
            if (curso.getAnoCurricularDe(uc) == anoCurricular && curso.getUnidades().contains(uc)) {
                ucsAno.add(uc);
            }
        }
        if (ucsAno.isEmpty()) {
            System.out.println("  [!] Não existem UCs para o " + anoCurricular + ".º ano deste curso.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Unidades Curriculares:");
        for (int i = 0; i < ucsAno.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + ucsAno.get(i).getNome());
        }
        int escolhaUC;
        do {
            escolhaUC = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
            if (escolhaUC == 0) return;
            if (escolhaUC < 1 || escolhaUC > ucsAno.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + ucsAno.size() + ".");
        } while (escolhaUC < 1 || escolhaUC > ucsAno.size());
        String nomeUC = ucsAno.get(escolhaUC - 1).getNome();

        // Dia da semana
        String[] dias = horarioController.getDiasSemana();
        System.out.println("\n  Dia da semana:");
        for (int i = 0; i < dias.length; i++) System.out.println("  " + (i + 1) + ". " + dias[i]);
        int escolhaDia;
        do {
            escolhaDia = Utils.lerInteiro("  Selecione (0 para voltar): ", scanner);
            if (escolhaDia == 0) return;
            if (escolhaDia < 1 || escolhaDia > dias.length)
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + dias.length + ".");
        } while (escolhaDia < 1 || escolhaDia > dias.length);
        String diaSemana = dias[escolhaDia - 1];

        // Duração
        System.out.println("\n  Duração do bloco:");
        System.out.println("  1. 1 hora");
        System.out.println("  2. 2 horas");
        int escolhaDuracao;
        do {
            escolhaDuracao = Utils.lerInteiro("  Selecione (0 para voltar): ", scanner);
            if (escolhaDuracao == 0) return;
            if (escolhaDuracao < 1 || escolhaDuracao > 2)
                System.out.println("  [!] Opção inválida. Escolha 1 ou 2.");
        } while (escolhaDuracao < 1 || escolhaDuracao > 2);
        int duracao = (escolhaDuracao == 1) ? 60 : 120;

        // Hora de início
        String[] horas = horarioController.getHorasValidas(duracao);
        System.out.println("\n  Horas disponíveis para blocos de " + (duracao / 60) + "h:");
        for (int i = 0; i < horas.length; i++) System.out.println("  " + (i + 1) + ". " + horas[i]);
        int escolhaHora;
        do {
            escolhaHora = Utils.lerInteiro("  Selecione (0 para voltar): ", scanner);
            if (escolhaHora == 0) return;
            if (escolhaHora < 1 || escolhaHora > horas.length)
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + horas.length + ".");
        } while (escolhaHora < 1 || escolhaHora > horas.length);
        String horaInicio = horas[escolhaHora - 1];

        System.out.println("\n  ─── Resumo do bloco ───────────────────────");
        System.out.println("  Curso   : " + curso.getNomeCurso() + " (Ano " + anoCurricular + ")");
        System.out.println("  UC      : " + nomeUC);
        System.out.println("  Dia     : " + diaSemana);
        System.out.println("  Hora    : " + horaInicio + " (" + (duracao / 60) + "h)");
        System.out.println("  ───────────────────────────────────────────");

        if (!Utils.confirmar("Confirmar adição do bloco?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }

        BlocoHorario bloco = horarioController.adicionarBloco(
                curso.getNomeCurso(), anoCurricular, anoLetivo, diaSemana, horaInicio, duracao, nomeUC);
        System.out.println("  [✓] Bloco adicionado: " + bloco);
        Utils.pausar(scanner);
    }

    private void removerBloco() {
        Utils.tituloPagina("GESTÃO DE HORÁRIOS", "Remover Bloco do Horário");
        Curso curso = selecionarCurso();
        if (curso == null) return;
        int anoCurricular = selecionarAnoCurricular();
        if (anoCurricular == 0) return;
        int anoLetivo = obterAnoLetivo();

        Horario horario = horarioController.obterHorario(curso.getNomeCurso(), anoCurricular, anoLetivo);
        List<BlocoHorario> blocos = horario.getBlocos();
        if (blocos.isEmpty()) {
            System.out.println("  [!] Não existem blocos no horário.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Blocos no horário:");
        for (int i = 0; i < blocos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + blocos.get(i));
        }
        int escolha;
        do {
            escolha = Utils.lerInteiro("  Selecione o bloco a remover (0 para voltar): ", scanner);
            if (escolha == 0) return;
            if (escolha < 1 || escolha > blocos.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + blocos.size() + ".");
        } while (escolha < 1 || escolha > blocos.size());

        if (!Utils.confirmar("Remover o bloco '" + blocos.get(escolha - 1) + "'?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }
        horarioController.removerBloco(curso.getNomeCurso(), anoCurricular, anoLetivo, escolha - 1);
        System.out.println("  [✓] Bloco removido com sucesso.");
        Utils.pausar(scanner);
    }

    // ── Utilitários ───────────────────────────────────────────────────────────

    public void imprimirHorario(Horario horario) {
        System.out.println("\n  " + horario.getNomeCurso()
                + "  |  " + horario.getAnoCurricular() + ".º Ano"
                + "  |  Ano Letivo " + horario.getAnoLetivo() + "/" + (horario.getAnoLetivo() + 1));
        System.out.println("  " + "─".repeat(60));
        if (horario.getBlocos().isEmpty()) {
            System.out.println("  (sem blocos definidos)");
        } else {
            for (String dia : HorarioBLL.DIAS_SEMANA) {
                List<BlocoHorario> blocosDia = horario.getBlocosParaDia(dia);
                if (!blocosDia.isEmpty()) {
                    System.out.println("  " + dia + ":");
                    for (BlocoHorario b : blocosDia) {
                        String docente = "";
                        if (ucController != null) {
                            for (UnidadeCurricular uc : ucController.listarUnidades()) {
                                if (uc.getNome().equalsIgnoreCase(b.getNomeUC()) && uc.temDocenteResponsavel()) {
                                    docente = "  [" + uc.getDocenteResponsavel() + "]";
                                    break;
                                }
                            }
                        }
                        System.out.println("    " + b.getHoraInicio() + "–" + b.getHoraFim()
                                + "  " + b.getNomeUC() + "  (" + (b.getDuracao() / 60) + "h)" + docente);
                    }
                }
            }
        }
        System.out.println("  " + "─".repeat(60));
    }

    private Curso selecionarCurso() {
        List<Curso> cursos = cursoController.listarCursos();
        if (cursos.isEmpty()) {
            System.out.println("  [!] Não existem cursos registados.");
            Utils.pausar(scanner);
            return null;
        }
        System.out.println("\n  Cursos:");
        for (int i = 0; i < cursos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + cursos.get(i).getNomeCurso());
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

    private int selecionarAnoCurricular() {
        int opcao;
        do {
            opcao = Utils.lerInteiro("  Ano curricular (1, 2 ou 3 | 0 para voltar): ", scanner);
            if (opcao == 0) return 0;
            if (opcao < 1 || opcao > 3)
                System.out.println("  [!] Opção inválida. Escolha 1, 2 ou 3.");
        } while (opcao < 1 || opcao > 3);
        return opcao;
    }

    private int obterAnoLetivo() {
        AnoLetivo anoAtual = anoLetivoController.consultarAnoAtual();
        if (anoAtual != null) return anoAtual.getAno();
        return Utils.lerInteiro("  Ano letivo (ex: 2025): ", scanner);
    }
}
