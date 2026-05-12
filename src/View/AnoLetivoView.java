package View;

import Controller.AnoLetivoController;
import Controller.EstudanteController;
import Model.AnoLetivo;
import Model.Estudante;
import Model.RelatorioFechoAnoLetivo;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
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
                "Abrir novo ano letivo",
                "Fechar ano letivo e avançar estudantes aprovados"
        };

        int opcao;

        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ANO LETIVO", opcoes, scanner);

            try {
                switch (opcao) {
                    case 1: consultarAtual(); break;
                    case 2: abrirNovoAno(); break;
                    case 3: fecharAnoLetivo(); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
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
