package View;

import Controller.AvaliacaoController;
import Controller.UnidadeCurricularController;
import Model.Avaliacao;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class AvaliacaoView {

    private final AvaliacaoController         avaliacaoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final Scanner                     scanner;

    public AvaliacaoView(AvaliacaoController avaliacaoController,
                         UnidadeCurricularController unidadeCurricularController,
                         Scanner scanner) {
        this.avaliacaoController = avaliacaoController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.scanner = scanner;
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
        System.out.println("\n--- Registar Avaliação ---");

        UnidadeCurricular uc = selecionarUC();
        if (uc == null) return;

        double peso = Utils.lerDouble("Peso (%): ", scanner);
        Date data = Utils.lerDataAvaliacao("Data (DD/MM/AAAA): ", scanner);
        double nota = Utils.lerDouble("Nota (0-20): ", scanner);

        List<UnidadeCurricular> ucs = new ArrayList<>();
        ucs.add(uc);

        Avaliacao a = avaliacaoController.registarAvaliacao(ucs, peso, data, nota);
        System.out.println("  [✓] Avaliação registada com sucesso.");
        System.out.println("  " + a);
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.limparEcra();
        System.out.println("\n--- Lista de Avaliações ---");
        ArrayList<Avaliacao> lista = avaliacaoController.listarAvaliacoes();
        if (lista.isEmpty()) { System.out.println("  (sem avaliações registadas)"); Utils.pausar(scanner); return; }
        for (Avaliacao a : lista) { System.out.println(a + "\n"); }
        Utils.pausar(scanner);
    }

    private void procurarPorUC() {
        Utils.limparEcra();
        System.out.println("\n--- Avaliações por Unidade Curricular ---");

        UnidadeCurricular uc = selecionarUC();
        if (uc == null) return;

        ArrayList<Avaliacao> avaliacoes = avaliacaoController.procurarPorUC(uc);
        if (avaliacoes.isEmpty()) { System.out.println("  (sem avaliações para esta UC)"); Utils.pausar(scanner); return; }
        for (Avaliacao a : avaliacoes) { System.out.println(a + "\n"); }
        Utils.pausar(scanner);
    }

    private void remover() {
        Utils.limparEcra();
        System.out.println("\n--- Remover Avaliação ---");
        ArrayList<Avaliacao> lista = avaliacaoController.listarAvaliacoes();
        if (lista.isEmpty()) { System.out.println("  (sem avaliações registadas)"); Utils.pausar(scanner); return; }
        for (Avaliacao a : lista) { System.out.println(a + "\n"); }

        Date data = Utils.lerDataAvaliacao("Data da avaliação a remover (DD/MM/AAAA): ", scanner);
        Avaliacao alvo = null;
        for (Avaliacao a : lista) {
            if (a.getData().equals(data)) { alvo = a; break; }
        }
        if (alvo == null) { System.out.println("  [!] Avaliação não encontrada."); Utils.pausar(scanner); return; }

        String confirmar = Utils.lerCampo("  Tem a certeza que deseja remover esta avaliação? (S/N): ", scanner);
        if (!confirmar.equalsIgnoreCase("S")) {
            System.out.println("  Operação cancelada.");
            Utils.pausar(scanner);
            return;
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
        System.out.println("  UCs disponíveis:");
        for (int i = 0; i < ucs.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + ucs.get(i).getNome()
                    + " (Ano " + ucs.get(i).getAnoCurricular() + ")");
        }
        int escolha = Utils.lerInteiro("Selecione a UC (número): ", scanner);
        if (escolha < 1 || escolha > ucs.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return null;
        }
        return ucs.get(escolha - 1);
    }
}