package View;

import Controller.JustificacaoController;
import Model.JustificacaoFalta;
import Model.TipoJustificacao;
import Utils.Utils;

import java.util.List;
import java.util.Scanner;

public class JustificacaoGestorView {

    private final JustificacaoController justificacaoController;
    private final Scanner scanner;

    public JustificacaoGestorView(JustificacaoController justificacaoController, Scanner scanner) {
        this.justificacaoController = justificacaoController;
        this.scanner = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Gerir Tipos de Justificação",
                "Ver Justificações Pendentes",
                "Ver Todas as Justificações"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("GESTÃO DE JUSTIFICAÇÕES", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1:
                        menuTipos();
                        break;
                    case 2:
                        verPendentes();
                        break;
                    case 3:
                        verTodas();
                        break;
                    case 0:
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    // ── Tipos ─────────────────────────────────────────────────────────────────

    private void menuTipos() {
        String[] opcoes = {"Criar Tipo", "Listar Tipos", "Remover Tipo"};
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("TIPOS DE JUSTIFICAÇÃO", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1:
                        criarTipo();
                        break;
                    case 2:
                        listarTipos();
                        break;
                    case 3:
                        removerTipo();
                        break;
                    case 0:
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void criarTipo() {
        Utils.tituloPagina("JUSTIFICAÇÕES", "Criar Tipo");
        String nome = Utils.lerCampo("  Nome (ex: Baixa médica): ", scanner);
        System.out.println("\n  Categoria:");
        System.out.println("  1. " + TipoJustificacao.CATEGORIA_SAUDE + "  (razões de saúde)");
        System.out.println("  2. " + TipoJustificacao.CATEGORIA_ESTATUTO + "  (estatuto de estudante)");
        int escolha = Utils.lerInteiro("  Selecione (1 ou 2): ", scanner);
        if (escolha < 1 || escolha > 2) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return;
        }
        String categoria = (escolha == 1) ? TipoJustificacao.CATEGORIA_SAUDE : TipoJustificacao.CATEGORIA_ESTATUTO;
        justificacaoController.criarTipo(nome, categoria);
        System.out.println("  [✓] Tipo '" + nome + "' criado.");
        Utils.pausar(scanner);
    }

    private void listarTipos() {
        Utils.tituloPagina("JUSTIFICAÇÕES", "Tipos de Justificação");
        List<TipoJustificacao> tipos = justificacaoController.listarTipos();
        if (tipos.isEmpty()) {
            System.out.println("  (sem tipos registados)");
        } else {
            for (TipoJustificacao t : tipos) System.out.println("  - " + t);
        }
        Utils.pausar(scanner);
    }

    private void removerTipo() {
        Utils.tituloPagina("JUSTIFICAÇÕES", "Remover Tipo");
        List<TipoJustificacao> tipos = justificacaoController.listarTipos();
        if (tipos.isEmpty()) {
            System.out.println("  (sem tipos registados)");
            Utils.pausar(scanner);
            return;
        }
        for (int i = 0; i < tipos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + tipos.get(i));
        }
        int escolha = Utils.lerInteiro("  Selecione (0 para voltar): ", scanner);
        if (escolha == 0) return;
        if (escolha < 1 || escolha > tipos.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return;
        }
        String nome = tipos.get(escolha - 1).getNome();
        if (!Utils.confirmar("Remover o tipo '" + nome + "'?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }
        justificacaoController.removerTipo(nome);
        System.out.println("  [✓] Tipo '" + nome + "' removido.");
        Utils.pausar(scanner);
    }

    // ── Justificações ─────────────────────────────────────────────────────────

    private void verPendentes() {
        while (true) {
            Utils.tituloPagina("JUSTIFICAÇÕES", "Pendentes");
            List<JustificacaoFalta> pendentes = justificacaoController.listarPendentes();
            if (pendentes.isEmpty()) {
                System.out.println("  (sem justificações pendentes)");
                Utils.pausar(scanner);
                return;
            }
            for (int i = 0; i < pendentes.size(); i++) {
                JustificacaoFalta j = pendentes.get(i);
                System.out.println("  " + (i + 1) + ". " + j.getNumMecanografico()
                        + "  |  " + j.getNomeUC()
                        + "  |  " + j.getDataAula() + " " + j.getHoraInicio()
                        + "  |  " + j.getNomeTipoJustificacao());
            }
            System.out.println();
            int escolha = Utils.lerInteiro("  Selecione para decidir (0 para voltar):", scanner);
            if (escolha == 0) return;
            if (escolha < 1 || escolha > pendentes.size()) {
                System.out.println("  [!] Opção inválida.");
                continue;
            }
            JustificacaoFalta j = pendentes.get(escolha - 1);
            System.out.println("\n  Justificação: " + j);
            System.out.println("  1. Aprovar   2. Rejeitar   0. Cancelar");
            int decisao = Utils.lerInteiro("  Decisão: ", scanner);
            if (decisao == 1) {
                justificacaoController.aprovarJustificacao(j);
                System.out.println("  [✓] Justificação aprovada.");
            } else if (decisao == 2) {
                justificacaoController.rejeitarJustificacao(j);
                System.out.println("  [✓] Justificação rejeitada.");
            }
        }
    }

    private void verTodas() {
        Utils.tituloPagina("JUSTIFICAÇÕES", "Todas as Justificações");
        List<JustificacaoFalta> todas = justificacaoController.listarTodas();
        if (todas.isEmpty()) {
            System.out.println("  (sem justificações registadas)");
        } else {
            System.out.printf("  %-12s %-20s %-12s %-18s %s%n",
                    "Nº Mecano.", "UC", "Data", "Tipo", "Estado");
            System.out.println("  " + "─".repeat(75));
            for (JustificacaoFalta j : todas) {
                System.out.printf("  %-12s %-20s %-12s %-18s %s%n",
                        j.getNumMecanografico(),
                        j.getNomeUC(),
                        j.getDataAula() + " " + j.getHoraInicio(),
                        j.getNomeTipoJustificacao(),
                        j.getEstado());
            }
        }
        Utils.pausar(scanner);
    }
}
