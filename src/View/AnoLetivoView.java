package View;

import Controller.AnoLetivoController;
import Controller.EstudanteController;
import Model.AnoLetivo;
import Model.Estudante;
import Model.RelatorioFechoAnoLetivo;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AnoLetivoView {
    private final AnoLetivoController anoLetivoController;
    private final EstudanteController estudanteController;
    private final Scanner scanner;

    public AnoLetivoView(AnoLetivoController anoLetivoController, EstudanteController estudanteController, Scanner scanner) {
        this.anoLetivoController = anoLetivoController;
        this.estudanteController = estudanteController;
        this.scanner = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Consultar ano letivo atual",
                "Listar todos os anos letivos",
                "Abrir novo ano letivo",
                "Fechar ano letivo e avançar estudantes aprovados",
                "Remover ano letivo"
        };

        int opcao;

        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ANO LETIVO", opcoes, scanner);

            try {
                switch (opcao) {
                    case 1: consultarAtual(); break;
                    case 2: listarTodos(); break;
                    case 3: abrirNovoAno(); break;
                    case 4: fecharAnoLetivo(); break;
                    case 5: removerAnoLetivo(); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void listarTodos() {
        Utils.limparEcra();
        System.out.println("\n--- Histórico de Anos Letivos ---");
        List<AnoLetivo> anos = anoLetivoController.listarTodos();
        if (anos.isEmpty()) {
            System.out.println("  (nenhum ano letivo registado)");
            Utils.pausar(scanner);
            return;
        }
        System.out.println("  " + "-".repeat(45));
        System.out.printf("  %-12s %-10s %-12s %-12s%n", "Ano Letivo", "Estado", "Abertura", "Fecho");
        System.out.println("  " + "-".repeat(45));
        for (AnoLetivo a : anos) {
            System.out.printf("  %-12s %-10s %-12s %-12s%n",
                    a.getDesignacao(),
                    a.getEstado(),
                    a.getDataAbertura(),
                    a.getDataFecho() != null ? a.getDataFecho().toString() : "-");
        }
        System.out.println("  " + "-".repeat(45));
        Utils.pausar(scanner);
    }

    private void removerAnoLetivo() {
        System.out.println("\n--- Remover Ano Letivo ---");
        List<AnoLetivo> anos = anoLetivoController.listarTodos();
        if (anos.isEmpty()) {
            System.out.println("  (nenhum ano letivo registado)");
            Utils.pausar(scanner);
            return;
        }
        System.out.println("  Anos letivos registados:");
        for (AnoLetivo a : anos) {
            System.out.println("  - " + a.getDesignacao() + " [" + a.getEstado() + "]");
        }
        int ano = Utils.lerInteiro("\n  Ano de início a remover (ex: 2026): ", scanner);
        String confirmar = Utils.lerCampo(
                "  Tem a certeza que deseja remover o ano letivo " + ano + "/" + (ano + 1) + "? (S/N): ",
                scanner);
        if (!confirmar.equalsIgnoreCase("S")) {
            System.out.println("  Operação cancelada.");
            Utils.pausar(scanner);
            return;
        }
        anoLetivoController.removerAnoLetivo(ano);
        System.out.println("  [✓] Ano letivo " + ano + "/" + (ano + 1) + " removido com sucesso.");
        Utils.pausar(scanner);
    }

    private void consultarAtual() {
        Utils.limparEcra();
        System.out.println("\n--- Ano Letivo Atual ---");

        AnoLetivo atual = anoLetivoController.consultarAnoAtual();

        if (atual == null) {
            System.out.println("  [!] Não existe ano letivo ativo.");

            AnoLetivo maisRecente = anoLetivoController.consultarMaisRecente();
            if (maisRecente != null) {
                System.out.println("\n  Último ano letivo registado:");
                System.out.println(maisRecente);
            }

            Utils.pausar(scanner);
            return;
        }

        System.out.println(atual);
        Utils.pausar(scanner);
    }

    private void abrirNovoAno() {
        System.out.println("\n--- Abrir Novo Ano Letivo ---");

        int anoSugerido = LocalDate.now().getYear();
        int ano = Utils.lerInteiro("Ano de início (ex.: " + anoSugerido + "): ", scanner);

        AnoLetivo novo = anoLetivoController.abrirAnoLetivo(ano);

        System.out.println("  [✓] Ano letivo " + novo.getDesignacao() + " aberto com sucesso.");
        Utils.pausar(scanner);
    }

    private void fecharAnoLetivo() {
        System.out.println("\n--- Fechar Ano Letivo ---");

        AnoLetivo atual = anoLetivoController.consultarAnoAtual();

        if (atual == null) {
            System.out.println("  [!] Não existe ano letivo ativo para fechar.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("  Ano letivo ativo: " + atual.getDesignacao());
        System.out.println("  Esta operação fecha o ano letivo, avança estudantes aprovados e marca concluídos do 3.º ano.");

        String confirmar = Utils.lerCampo("Confirmar fecho? (S/N): ", scanner);

        if (!confirmar.equalsIgnoreCase("S")) {
            System.out.println("  Operação cancelada.");
            Utils.pausar(scanner);
            return;
        }

        ArrayList<Estudante> estudantes = estudanteController.listarEstudante();
        RelatorioFechoAnoLetivo relatorio = anoLetivoController.fecharAnoAtual(estudantes);

        for (Estudante estudante : estudantes) {
            estudanteController.guardarEstadoEstudante(estudante);
        }

        System.out.println("\n  [✓] Ano letivo fechado com sucesso.");
        System.out.println("  Avançados: " + relatorio.getEstudantesAvancados());
        System.out.println("  Mantidos: " + relatorio.getEstudantesMantidos());
        System.out.println("  Concluídos: " + relatorio.getEstudantesConcluidos());

        if (relatorio.getCaminhoFicheiroHistorico() != null && !relatorio.getCaminhoFicheiroHistorico().isBlank()) {
            System.out.println("  [✓] Histórico exportado para: " + relatorio.getCaminhoFicheiroHistorico());
        }

        if (!relatorio.getMensagens().isEmpty()) {
            System.out.println("\n  Detalhes:");
            for (String mensagem : relatorio.getMensagens()) {
                System.out.println("  - " + mensagem);
            }
        }

        Utils.pausar(scanner);
    }
}
