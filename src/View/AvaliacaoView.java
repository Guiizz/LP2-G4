package View;

import Controller.AvaliacaoController;
import Controller.CursoController;
import Controller.UnidadeCurricularController;
import Model.Avaliacao;
import Model.Curso;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class AvaliacaoView {

    private final AvaliacaoController         avaliacaoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final CursoController             cursoController;
    private final Scanner                     scanner;

    public AvaliacaoView(AvaliacaoController avaliacaoController,
                         UnidadeCurricularController unidadeCurricularController,
                         CursoController cursoController,
                         Scanner scanner) {
        this.avaliacaoController         = avaliacaoController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.cursoController             = cursoController;
        this.scanner                     = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Registar Avaliação",
                "Listar Avaliações",
                "Procurar Avaliações por UC",
                "Remover Avaliação"
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
        Utils.tituloPagina("GESTÃO DE AVALIAÇÕES", "Registar Avaliação");

        UnidadeCurricular uc = selecionarUC();
        if (uc == null) return;

        List<Model.MomentoAvaliacao> momentos = uc.getMomentosAvaliacao();
        double peso;
        if (momentos == null || momentos.isEmpty()) {
            System.out.println("  [!] A UC não tem momentos de avaliação definidos. Introduza o peso manualmente.");
            do {
                peso = Utils.lerDouble("  Peso (%): ", scanner);
                if (peso < 0 || peso > 100)
                    System.out.println("  [!] O peso deve ser um valor entre 0 e 100.");
            } while (peso < 0 || peso > 100);
        } else {
            System.out.println("\n  Momentos de avaliação:");
            for (int i = 0; i < momentos.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + momentos.get(i).getNome()
                        + " (" + momentos.get(i).getPeso() + "%)");
            }
            int escolha;
            do {
                escolha = Utils.lerInteiro("  Selecione o momento (0 para voltar): ", scanner);
                if (escolha == 0) return;
                if (escolha < 1 || escolha > momentos.size())
                    System.out.println("  [!] Opção inválida. Escolha entre 1 e " + momentos.size() + ".");
            } while (escolha < 1 || escolha > momentos.size());
            peso = momentos.get(escolha - 1).getPeso();
            System.out.println("  Peso: " + peso + "%");
        }

        Date data = Utils.lerDataAvaliacao("  Data (DD/MM/AAAA): ", scanner);

        List<UnidadeCurricular> ucs = new ArrayList<>();
        ucs.add(uc);

        String nomeCurso = "";
        for (Curso c : cursoController.listarCursos()) {
            if (c.getUnidades().contains(uc)) { nomeCurso = c.getNomeCurso(); break; }
        }
        if (nomeCurso.isEmpty()) {
            System.out.println("  [!] A UC não está associada a nenhum curso.");
            Utils.pausar(scanner);
            return;
        }

        Avaliacao a = avaliacaoController.registarAvaliacao(ucs, nomeCurso, data, 0);
        System.out.println("  [✓] Momento de avaliação registado com sucesso.");
        System.out.println("  " + a);
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.tituloPagina("GESTÃO DE AVALIAÇÕES", "Lista de Avaliações");
        ArrayList<Avaliacao> lista = avaliacaoController.listarAvaliacoes();
        if (lista.isEmpty()) { System.out.println("  (sem avaliações registadas)"); Utils.pausar(scanner); return; }
        for (Avaliacao a : lista) { System.out.println(a + "\n"); }
        Utils.pausar(scanner);
    }

    private void procurarPorUC() {
        Utils.tituloPagina("GESTÃO DE AVALIAÇÕES", "Avaliações por UC");

        UnidadeCurricular uc = selecionarUC();
        if (uc == null) return;

        ArrayList<Avaliacao> avaliacoes = avaliacaoController.procurarPorUC(uc);
        if (avaliacoes.isEmpty()) { System.out.println("  (sem avaliações para esta UC)"); Utils.pausar(scanner); return; }
        for (Avaliacao a : avaliacoes) { System.out.println(a + "\n"); }
        Utils.pausar(scanner);
    }

    private void remover() {
        Utils.tituloPagina("GESTÃO DE AVALIAÇÕES", "Remover Avaliação");
        ArrayList<Avaliacao> lista = avaliacaoController.listarAvaliacoes();
        if (lista.isEmpty()) { System.out.println("  (sem avaliações registadas)"); Utils.pausar(scanner); return; }

        for (int i = 0; i < lista.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + lista.get(i));
        }

        int escolha;
        do {
            escolha = Utils.lerInteiro("  Selecione a avaliação a remover (0 para voltar): ", scanner);
            if (escolha == 0) return;
            if (escolha < 1 || escolha > lista.size())
                System.out.println("  [!] Opção inválida. Escolha entre 1 e " + lista.size() + ".");
        } while (escolha < 1 || escolha > lista.size());
        Avaliacao alvo = lista.get(escolha - 1);

        if (!Utils.confirmar("Remover a avaliação de " + alvo.getDataFormatada() + "?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }
        avaliacaoController.removerAvaliacao(alvo);
        System.out.println("  [✓] Avaliação removida com sucesso.");
        Utils.pausar(scanner);
    }

    private UnidadeCurricular selecionarUC() {
        ArrayList<UnidadeCurricular> ucs = unidadeCurricularController.listarUnidades();
        if (ucs.isEmpty()) {
            System.out.println("  [!] Não existem UCs registadas.");
            Utils.pausar(scanner);
            return null;
        }
        // Carregar todos os cursos uma única vez (evita 1 query por UC)
        ArrayList<Curso> todosCursos = cursoController.listarCursos();
        System.out.println("  UCs disponíveis:");
        for (int i = 0; i < ucs.size(); i++) {
            UnidadeCurricular uc = ucs.get(i);
            System.out.println("  " + (i + 1) + ". " + uc.getNome()
                    + " — " + cursosComUC(uc, todosCursos));
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

    private String cursosComUC(UnidadeCurricular uc, ArrayList<Curso> todosCursos) {
        StringBuilder sb = new StringBuilder();
        for (Curso c : todosCursos) {
            if (c.getUnidades().contains(uc)) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(c.getNomeCurso());
            }
        }
        return sb.length() > 0 ? sb.toString() : "(sem curso)";
    }
}