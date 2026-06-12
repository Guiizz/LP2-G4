package View;

import Controller.AnoLetivoController;
import Controller.CursoController;
import Controller.UnidadeCurricularController;
import Model.AnoLetivo;
import Model.Curso;
import Model.MomentoAvaliacao;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Gestão dos momentos de avaliação das UCs, por curso.
 * Cada UC pode ter até 3 momentos por curso e ano letivo,
 * com pesos distribuídos automaticamente (100 / 50-50 / 33-33-34).
 */
public class AvaliacaoView {

    private final UnidadeCurricularController unidadeCurricularController;
    private final CursoController             cursoController;
    private final AnoLetivoController         anoLetivoController;
    private final Scanner                     scanner;

    public AvaliacaoView(UnidadeCurricularController unidadeCurricularController,
                         CursoController cursoController,
                         AnoLetivoController anoLetivoController,
                         Scanner scanner) {
        this.unidadeCurricularController = unidadeCurricularController;
        this.cursoController             = cursoController;
        this.anoLetivoController         = anoLetivoController;
        this.scanner                     = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Registar momento de avaliação",
                "Listar momentos de avaliação",
                "Procurar momentos por UC",
                "Remover momento de avaliação"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("MOMENTOS DE AVALIAÇÃO", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: registar(); break;
                    case 2: listar(); break;
                    case 3: procurarPorUC(); break;
                    case 4: remover(); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    // ── Ações ─────────────────────────────────────────────────────────────────

    private void registar() {
        Utils.tituloPagina("Registar Momento de Avaliação");

        Curso curso = selecionarCurso();
        if (curso == null) return;

        UnidadeCurricular uc = selecionarUCDoCurso(curso);
        if (uc == null) return;

        String nome = Utils.lerCampo("  Nome do momento (ex: Frequência, Exame, Projeto): ", scanner);
        Date data = Utils.lerDataAvaliacao("  Data (DD/MM/AAAA): ", scanner);

        unidadeCurricularController.adicionarMomento(uc, nome, curso.getNomeCurso(), data);

        System.out.println("  [✓] Momento de avaliação registado com sucesso.");
        int anoLetivo = anoLetivoAberto();
        System.out.println("  Distribuição atual na UC '" + uc.getNome() + "' (" + curso.getNomeCurso() + "):");
        for (MomentoAvaliacao m : uc.getMomentosParaAno(anoLetivo, curso.getNomeCurso())) {
            System.out.printf("      %-25s %5.2f%%  %s%n", m.getNome(), m.getPeso(), m.getDataFormatada());
        }
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.limparEcra();
        Utils.tituloPagina("Momentos de Avaliação");
        boolean encontrou = false;
        for (UnidadeCurricular uc : unidadeCurricularController.listarUnidades()) {
            List<MomentoAvaliacao> momentos = uc.getMomentosAvaliacao();
            if (momentos == null || momentos.isEmpty()) continue;
            encontrou = true;
            System.out.println("\n  UC: " + uc.getNome() + " (Ano " + uc.getAnoCurricular() + ")");
            for (MomentoAvaliacao m : momentos) {
                System.out.printf("    - %-25s %5.2f%%  %-12s %s%n",
                        m.getNome(), m.getPeso(), m.getDataFormatada(),
                        m.getNomeCurso() != null ? m.getNomeCurso() : "(sem curso)");
            }
        }
        if (!encontrou) System.out.println("  (sem momentos de avaliação registados)");
        Utils.pausar(scanner);
    }

    private void procurarPorUC() {
        Utils.limparEcra();
        Utils.tituloPagina("Momentos por Unidade Curricular");

        UnidadeCurricular uc = selecionarUC();
        if (uc == null) return;

        List<MomentoAvaliacao> momentos = uc.getMomentosAvaliacao();
        if (momentos == null || momentos.isEmpty()) {
            System.out.println("  (sem momentos para esta UC)");
            Utils.pausar(scanner);
            return;
        }
        for (MomentoAvaliacao m : momentos) {
            System.out.println(m + "\n");
        }
        Utils.pausar(scanner);
    }

    private void remover() {
        Utils.limparEcra();
        Utils.tituloPagina("Remover Momento de Avaliação");

        Curso curso = selecionarCurso();
        if (curso == null) return;

        UnidadeCurricular uc = selecionarUCDoCurso(curso);
        if (uc == null) return;

        int anoLetivo = anoLetivoAberto();
        List<MomentoAvaliacao> momentosDoCurso = uc.getMomentosParaAno(anoLetivo, curso.getNomeCurso());
        if (momentosDoCurso.isEmpty()) {
            System.out.println("  [!] A UC '" + uc.getNome() + "' não tem momentos neste curso.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Momentos de '" + uc.getNome() + "' (" + curso.getNomeCurso() + "):");
        for (int i = 0; i < momentosDoCurso.size(); i++) {
            MomentoAvaliacao m = momentosDoCurso.get(i);
            System.out.printf("  %d. %-25s %5.2f%%  %s%n", i + 1, m.getNome(), m.getPeso(), m.getDataFormatada());
        }

        int escolha = Utils.lerInteiro("  Selecione o momento a remover (0 para voltar): ", scanner);
        if (escolha == 0) return;
        if (escolha < 1 || escolha > momentosDoCurso.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return;
        }

        MomentoAvaliacao alvo = momentosDoCurso.get(escolha - 1);
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
        System.out.println("  [✓] Momento removido com sucesso.");
        Utils.pausar(scanner);
    }

    // ── Seletores ─────────────────────────────────────────────────────────────

    private int anoLetivoAberto() {
        AnoLetivo aberto = anoLetivoController.consultarAnoAtual();
        return aberto != null ? aberto.getAno() : 0;
    }

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

    private Curso selecionarCurso() {
        ArrayList<Curso> cursos = cursoController.listarCursos();
        if (cursos.isEmpty()) {
            System.out.println("  [!] Não existem cursos registados.");
            Utils.pausar(scanner);
            return null;
        }
        System.out.println("  Cursos disponíveis:");
        for (int i = 0; i < cursos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + cursos.get(i).getNomeCurso());
        }
        int escolha = Utils.lerInteiro("  Selecione o curso (0 para voltar): ", scanner);
        if (escolha == 0) return null;
        if (escolha < 1 || escolha > cursos.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return null;
        }
        return cursos.get(escolha - 1);
    }

    private UnidadeCurricular selecionarUCDoCurso(Curso curso) {
        List<UnidadeCurricular> ucsDoCurso = new ArrayList<>(curso.getUnidades());
        if (ucsDoCurso.isEmpty()) {
            System.out.println("  [!] O curso '" + curso.getNomeCurso() + "' não tem UCs registadas.");
            Utils.pausar(scanner);
            return null;
        }
        ucsDoCurso.sort((a, b) -> Integer.compare(a.getAnoCurricular(), b.getAnoCurricular()));

        System.out.println("\n  UCs do curso " + curso.getNomeCurso() + ":");
        int anoAtual = 0;
        for (int i = 0; i < ucsDoCurso.size(); i++) {
            UnidadeCurricular uc = ucsDoCurso.get(i);
            if (uc.getAnoCurricular() != anoAtual) {
                anoAtual = uc.getAnoCurricular();
                System.out.println("  ── " + anoAtual + ".º ano ──");
            }
            System.out.println("  " + (i + 1) + ". " + uc.getNome());
        }
        int escolha = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
        if (escolha == 0) return null;
        if (escolha < 1 || escolha > ucsDoCurso.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return null;
        }
        return ucsDoCurso.get(escolha - 1);
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
