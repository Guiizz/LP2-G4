package View;

import Config.ModoPersistencia;
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
                "Remover ano letivo",
                "Ver histórico de fechos"
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
                    case 6: verHistorico(); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void verHistorico() {
        Utils.limparEcra();
        Utils.tituloPagina("Histórico de Fechos de Anos Letivos");
        List<String[]> registos = anoLetivoController.listarHistorico();
        if (registos.isEmpty()) {
            System.out.println("  (sem fechos registados)");
            Utils.pausar(scanner);
            return;
        }
        // Colunas: anoLetivo;dataFecho;estadoFinal;avancados;mantidos;concluidos;detalhes
        for (String[] r : registos) {
            if (r.length < 6) continue;
            System.out.println("  " + "─".repeat(55));
            System.out.println("  Ano Letivo : " + r[0] + "   (fechado em " + r[1] + ")");
            System.out.println("  Avançados  : " + r[3]
                    + "   Mantidos: " + r[4]
                    + "   Concluídos: " + r[5]);
            if (r.length >= 7 && !r[6].isBlank()) {
                System.out.println("  Detalhes:");
                for (String msg : r[6].split("\\|")) {
                    System.out.println("    - " + msg.trim());
                }
            }
        }
        System.out.println("  " + "─".repeat(55));
        Utils.pausar(scanner);
    }

    private void listarTodos() {
        Utils.limparEcra();
        Utils.tituloPagina("Histórico de Anos Letivos");
        List<AnoLetivo> anos = anoLetivoController.listarTodos();
        if (anos.isEmpty()) {
            System.out.println("  (sem anos letivos registados)");
            Utils.pausar(scanner);
            return;
        }
        System.out.println("  " + "─".repeat(50));
        System.out.printf("  %-12s %-10s %-12s %-12s%n", "Ano Letivo", "Estado", "Abertura", "Fecho");
        System.out.println("  " + "─".repeat(50));
        for (AnoLetivo a : anos) {
            System.out.printf("  %-12s %-10s %-12s %-12s%n",
                    a.getDesignacao(),
                    a.getEstado(),
                    a.getDataAbertura(),
                    a.getDataFecho() != null ? a.getDataFecho().toString() : "(aberto)");
        }
        System.out.println("  " + "─".repeat(50));
        Utils.pausar(scanner);
    }

    private void removerAnoLetivo() {
        Utils.tituloPagina("Remover Ano Letivo");
        List<AnoLetivo> anos = anoLetivoController.listarTodos();
        if (anos.isEmpty()) {
            System.out.println("  (sem anos letivos registados)");
            Utils.pausar(scanner);
            return;
        }
        System.out.println("  Anos letivos registados:");
        for (int i = 0; i < anos.size(); i++) {
            AnoLetivo a = anos.get(i);
            System.out.println("  " + (i + 1) + ". " + a.getDesignacao() + " [" + a.getEstado() + "]");
        }
        int escolha = Utils.lerInteiro("\n  Selecione o ano letivo a remover (0 para voltar): ", scanner);
        if (escolha == 0) return;
        if (escolha < 1 || escolha > anos.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return;
        }
        AnoLetivo alvo = anos.get(escolha - 1);
        if (!Utils.confirmar("Remover o ano letivo " + alvo.getDesignacao() + "?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }
        anoLetivoController.removerAnoLetivo(alvo.getAno());
        System.out.println("  [✓] Ano letivo " + alvo.getDesignacao() + " removido com sucesso.");
        Utils.pausar(scanner);
    }

    private void consultarAtual() {
        Utils.limparEcra();
        Utils.tituloPagina("Ano Letivo Atual");

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
        Utils.tituloPagina("Abrir Novo Ano Letivo");

        int anoSugerido = LocalDate.now().getYear();
        int ano = Utils.lerInteiro("Ano de início (ex.: " + anoSugerido + "): ", scanner);

        AnoLetivo novo = anoLetivoController.abrirAnoLetivo(ano);

        System.out.println("  [✓] Ano letivo " + novo.getDesignacao() + " aberto com sucesso.");
        Utils.pausar(scanner);
    }

    private void fecharAnoLetivo() {
        Utils.tituloPagina("Fechar Ano Letivo");

        AnoLetivo atual = anoLetivoController.consultarAnoAtual();

        if (atual == null) {
            System.out.println("  [!] Não existe ano letivo ativo para fechar.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("  Ano letivo ativo: " + atual.getDesignacao());
        System.out.println("  Esta operação fecha o ano letivo, avança estudantes aprovados e marca concluídos do 3.º ano.");

        if (!Utils.confirmar("Confirmar fecho do ano letivo?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }

        RelatorioFechoAnoLetivo relatorio;

        if (ModoPersistencia.isBaseDados()) {
            // Path BD: um único JOIN lê Estudante+Inscricao+Propina;
            // UPDATE/INSERT feitos directamente em SQL pelo DAL.
            relatorio = anoLetivoController.fecharAnoAtual();
        } else {
            // Path CSV: carrega objetos Estudante e persiste manualmente.
            ArrayList<Estudante> estudantes = estudanteController.listarEstudante();
            relatorio = anoLetivoController.fecharAnoAtual(estudantes);
            for (Estudante estudante : estudantes) {
                estudanteController.guardarEstadoEstudante(estudante);
            }
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
