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
        System.out.println("\n--- Registar Avaliação --- (0 para cancelar)");

        ArrayList<UnidadeCurricular> todasUCs = unidadeCurricularController.listarUnidades();
        if (todasUCs.isEmpty()) {
            System.out.println("  [!] Não existem unidades curriculares registadas.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("  Unidades Curriculares disponíveis:");
        for (UnidadeCurricular uc : todasUCs) {
            System.out.println("    - " + uc.getNome() + " (Ano " + uc.getAnoCurricular() + ")");
        }

        System.out.print("Nome da UC: ");
        String nomeUC = scanner.nextLine().trim();
        UnidadeCurricular ucEscolhida = null;
        for (UnidadeCurricular uc : todasUCs) {
            if (uc.getNome().equalsIgnoreCase(nomeUC)) { ucEscolhida = uc; break; }
        }
        if (ucEscolhida == null) { System.out.println("  [!] UC não encontrada."); Utils.pausar(scanner); return; }

        double peso = Utils.lerDouble("Peso (%): ", scanner);
        Date data = Utils.lerDataAvaliacao("Data (DD/MM/AAAA): ", scanner);
        double nota = Utils.lerDouble("Nota (0-20): ", scanner);

        List<UnidadeCurricular> ucs = new ArrayList<>();
        ucs.add(ucEscolhida);

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
        ArrayList<UnidadeCurricular> todasUCs = unidadeCurricularController.listarUnidades();
        if (todasUCs.isEmpty()) { System.out.println("  [!] Não existem UCs registadas."); Utils.pausar(scanner); return; }

        for (UnidadeCurricular uc : todasUCs) { System.out.println("  - " + uc.getNome()); }

        System.out.print("Nome da UC: ");
        String nomeUC = scanner.nextLine().trim();
        UnidadeCurricular ucEscolhida = null;
        for (UnidadeCurricular uc : todasUCs) {
            if (uc.getNome().equalsIgnoreCase(nomeUC)) { ucEscolhida = uc; break; }
        }
        if (ucEscolhida == null) { System.out.println("  [!] UC não encontrada."); Utils.pausar(scanner); return; }

        ArrayList<Avaliacao> avaliacoes = avaliacaoController.procurarPorUC(ucEscolhida);
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

        avaliacaoController.removerAvaliacao(alvo);
        System.out.println("  [✓] Avaliação removida com sucesso.");
        Utils.pausar(scanner);
    }
}