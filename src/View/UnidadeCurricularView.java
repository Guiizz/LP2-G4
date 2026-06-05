package View;

import Controller.AnoLetivoController;
import Controller.DocenteController;
import Controller.UnidadeCurricularController;
import Model.AnoLetivo;
import Model.Docente;
import Model.MomentoAvaliacao;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class UnidadeCurricularView {

    private final UnidadeCurricularController unidadeCurricularController;
    private final DocenteController          docenteController;
    private final AnoLetivoController        anoLetivoController;
    private final Scanner                    scanner;

    public UnidadeCurricularView(UnidadeCurricularController unidadeCurricularController,
                                 DocenteController docenteController,
                                 AnoLetivoController anoLetivoController,
                                 Scanner scanner) {
        this.unidadeCurricularController = unidadeCurricularController;
        this.docenteController           = docenteController;
        this.anoLetivoController         = anoLetivoController;
        this.scanner                     = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Registar Unidade Curricular",
                "Listar Unidades Curriculares",
                "Atualizar Unidade Curricular",
                "Remover Unidade Curricular",
                "Gerir Momentos de Avaliação",
                "Ativar UC Manualmente"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("GESTÃO DE UNIDADES CURRICULARES", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: registar(); break;
                    case 2: listar(); break;
                    case 3: atualizar(); break;
                    case 4: remover(); break;
                    case 5: gerirMomentos(); break;
                    case 6: iniciarUC(); break;
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
        System.out.println("\n--- Registar Unidade Curricular --- (0 para cancelar)");
        String nome = Utils.lerCampo("Nome: ", scanner);
        int ano     = Utils.lerInteiro("Ano curricular (1, 2 ou 3): ", scanner);

        String siglaDocente = selecionarDocente();

        UnidadeCurricular uc = new UnidadeCurricular(nome, ano, 0);
        if (siglaDocente != null) {
            uc.setDocenteResponsavel(siglaDocente);
        }
        unidadeCurricularController.adicionarUnidade(uc);

        String msgDocente = (siglaDocente != null) ? " | Docente: " + siglaDocente : " | Docente: (por atribuir)";
        System.out.println("  [✓] Unidade Curricular '" + nome + "' registada com sucesso." + msgDocente);
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.limparEcra();
        System.out.println("\n--- Lista de Unidades Curriculares ---");
        ArrayList<UnidadeCurricular> lista = unidadeCurricularController.listarUnidades();
        if (lista.isEmpty()) { System.out.println("  (sem unidades curriculares registadas)"); Utils.pausar(scanner); return; }
        for (UnidadeCurricular uc : lista) { System.out.println(uc + "\n"); }
        Utils.pausar(scanner);
    }

    private void atualizar() {
        System.out.println("\n--- Atualizar Unidade Curricular ---");
        ArrayList<UnidadeCurricular> lista = unidadeCurricularController.listarUnidades();
        if (lista.isEmpty()) { System.out.println("  (sem UCs registadas)"); Utils.pausar(scanner); return; }

        UnidadeCurricular uc = selecionarUC(lista);
        if (uc == null) return;

        String novoNome = Utils.lerCampo("Novo nome (Enter para manter): ", scanner);
        if (!novoNome.isEmpty()) uc.setNome(novoNome);

        System.out.print("Novo ano curricular (Enter para manter): ");
        String anoStr = scanner.nextLine().trim();
        if (!anoStr.isEmpty()) {
            try { uc.setAnoCurricular(Integer.parseInt(anoStr)); }
            catch (NumberFormatException e) { System.out.println("  [!] Ano inválido — mantido."); }
        }

        String docenteAtual = uc.temDocenteResponsavel() ? uc.getDocenteResponsavel() : "(por atribuir)";
        System.out.println("  Docente responsável atual: " + docenteAtual);
        System.out.print("  Alterar docente responsável? (S/N): ");
        String resposta = scanner.nextLine().trim();
        if (resposta.equalsIgnoreCase("S")) {
            String sigla = selecionarDocente();
            if (sigla != null) {
                uc.setDocenteResponsavel(sigla);
            }
        }

        unidadeCurricularController.atualizarUnidade(uc);
        System.out.println("  [✓] Unidade Curricular atualizada com sucesso.");
        Utils.pausar(scanner);
    }

    private void remover() {
        System.out.println("\n--- Remover Unidade Curricular ---");
        ArrayList<UnidadeCurricular> lista = unidadeCurricularController.listarUnidades();
        if (lista.isEmpty()) { System.out.println("  (sem UCs registadas)"); Utils.pausar(scanner); return; }

        UnidadeCurricular uc = selecionarUC(lista);
        if (uc == null) return;

        String confirmar = Utils.lerCampo("  Tem a certeza que deseja remover a UC '" + uc.getNome() + "'? (S/N): ", scanner);
        if (!confirmar.equalsIgnoreCase("S")) {
            System.out.println("  Operação cancelada.");
            Utils.pausar(scanner);
            return;
        }
        unidadeCurricularController.removerUnidade(uc);
        System.out.println("  [✓] UC '" + uc.getNome() + "' removida com sucesso.");
        Utils.pausar(scanner);
    }

    private void gerirMomentos() {
        System.out.println("\n--- Gerir Momentos de Avaliação ---");
        ArrayList<UnidadeCurricular> lista = unidadeCurricularController.listarUnidades();
        if (lista.isEmpty()) { System.out.println("  [!] Não existem UCs registadas."); Utils.pausar(scanner); return; }

        UnidadeCurricular uc = selecionarUC(lista);
        if (uc == null) return;

        // Determinar ano letivo actual
        AnoLetivo anoAberto = anoLetivoController.consultarAnoAtual();
        int anoLetivo = (anoAberto != null) ? anoAberto.getAno() : 0;
        String labelAno = (anoLetivo > 0)
                ? anoLetivo + "/" + (anoLetivo + 1)
                : "(sem ano letivo aberto)";

        // Momentos do ano actual (ou legados se não houver ano aberto)
        List<MomentoAvaliacao> momentosDoAno = uc.getMomentosParaAno(anoLetivo);

        System.out.println("\n  UC: " + uc.getNome()
                + " (Ano curricular " + uc.getAnoCurricular() + ")"
                + " | Estado: " + (uc.isAtiva() ? "Ativa" : "Inativa"));
        System.out.println("  Ano letivo: " + labelAno);
        System.out.println("  Momentos para este ano: " + momentosDoAno.size() + "/3"
                + " | Soma: " + String.format("%.1f", uc.somaPesosParaAno(anoLetivo)) + "%");

        if (momentosDoAno.isEmpty()) {
            System.out.println("  (sem momentos para este ano letivo)");
        } else {
            for (int i = 0; i < momentosDoAno.size(); i++) {
                System.out.printf("    %d. %s — %.1f%%%n",
                        i + 1,
                        momentosDoAno.get(i).getNome(),
                        momentosDoAno.get(i).getPeso());
            }
        }

        if (uc.isAtiva() && !momentosDoAno.isEmpty()) {
            System.out.println("\n  [!] A UC está activa — os momentos deste ano letivo não podem ser alterados.");
            Utils.pausar(scanner);
            return;
        }

        if (uc.isAtiva()) {
            System.out.println("\n  [!] A UC está activa mas ainda não tem momentos para este ano letivo.");
            System.out.println("      Pode adicionar momentos para o novo ano letivo.");
        }

        if (anoLetivo == 0) {
            System.out.println("\n  [!] Não existe ano letivo aberto. Abra um ano letivo para gerir momentos.");
            Utils.pausar(scanner);
            return;
        }

        String[] opcoesMomentos = { "Adicionar momento", "Remover momento" };
        int opcao = Utils.mostrarMenu("MOMENTOS DA UC '" + uc.getNome() + "'", opcoesMomentos, scanner);

        if (opcao == 1) {
            adicionarMomento(uc, anoLetivo);
        } else if (opcao == 2) {
            removerMomento(uc, momentosDoAno);
        }
    }

    private void adicionarMomento(UnidadeCurricular uc, int anoLetivo) {
        List<MomentoAvaliacao> momentosDoAno = uc.getMomentosParaAno(anoLetivo);
        if (momentosDoAno.size() >= 3) {
            System.out.println("  [!] Já tem 3 momentos para este ano letivo. Remova um antes de adicionar.");
            Utils.pausar(scanner);
            return;
        }
        double disponivel = 100.0 - uc.somaPesosParaAno(anoLetivo);
        System.out.printf("  Peso disponível para %d/%d: %.1f%%%n", anoLetivo, anoLetivo + 1, disponivel);

        String nomeMomento = Utils.lerCampo("Nome do momento (ex: Frequência, Exame, Projeto): ", scanner);
        double peso = Utils.lerDouble("Peso (%): ", scanner);

        unidadeCurricularController.adicionarMomento(uc, nomeMomento, peso);
        System.out.println("  [✓] Momento '" + nomeMomento + "' (" + String.format("%.1f", peso)
                + "%) adicionado para " + anoLetivo + "/" + (anoLetivo + 1)
                + ". Soma: " + String.format("%.1f", uc.somaPesosParaAno(anoLetivo)) + "%");
        Utils.pausar(scanner);
    }

    private void removerMomento(UnidadeCurricular uc, List<MomentoAvaliacao> momentosDoAno) {
        if (momentosDoAno.isEmpty()) {
            System.out.println("  [!] A UC não tem momentos para remover neste ano letivo.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("\n  Escolha o momento a remover:");
        for (int i = 0; i < momentosDoAno.size(); i++) {
            System.out.printf("    %d. %s — %.1f%%%n",
                    i + 1,
                    momentosDoAno.get(i).getNome(),
                    momentosDoAno.get(i).getPeso());
        }

        int escolha = Utils.lerInteiro("Selecione o momento a remover (número): ", scanner);
        if (escolha < 1 || escolha > momentosDoAno.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return;
        }

        MomentoAvaliacao momentoRemover = momentosDoAno.get(escolha - 1);
        // Encontrar o índice real na lista completa da UC
        int indiceReal = uc.getMomentosAvaliacao().indexOf(momentoRemover);
        if (indiceReal < 0) {
            System.out.println("  [!] Não foi possível localizar o momento.");
            Utils.pausar(scanner);
            return;
        }

        String nomeRemovido = momentoRemover.getNome();
        unidadeCurricularController.removerMomento(uc, indiceReal);
        System.out.println("  [✓] Momento '" + nomeRemovido + "' removido.");
        Utils.pausar(scanner);
    }

    private void iniciarUC() {
        System.out.println("\n--- Ativar UC Manualmente ---");
        System.out.println("  Nota: as UCs são ativadas automaticamente ao iniciar o curso,");
        System.out.println("  se tiverem momentos de avaliação válidos (soma = 100%).");
        System.out.println("  Use esta opção apenas para ativar uma UC individualmente.\n");

        ArrayList<UnidadeCurricular> lista = unidadeCurricularController.listarUnidades();
        if (lista.isEmpty()) { System.out.println("  [!] Não existem UCs registadas."); Utils.pausar(scanner); return; }

        // Mostrar apenas UCs inativas para não confundir
        ArrayList<UnidadeCurricular> inativas = new ArrayList<>();
        for (UnidadeCurricular uc : lista) {
            if (!uc.isAtiva()) inativas.add(uc);
        }
        if (inativas.isEmpty()) {
            System.out.println("  Todas as UCs já estão ativas.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("  UCs inativas:");
        UnidadeCurricular uc = selecionarUC(inativas);
        if (uc == null) return;

        unidadeCurricularController.iniciarUC(uc);
        System.out.println("  [✓] UC '" + uc.getNome() + "' ativada com sucesso.");
        Utils.pausar(scanner);
    }

    private String selecionarDocente() {
        ArrayList<Docente> docentes = docenteController.listarDocentes();
        if (docentes.isEmpty()) {
            System.out.println("  (sem docentes registados — pode atribuir mais tarde)");
            return null;
        }
        System.out.println("\n  Docentes disponíveis (0 para não atribuir agora):");
        for (int i = 0; i < docentes.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + docentes.get(i).getNome()
                    + " (" + docentes.get(i).getSigla() + ")");
        }
        int escolha = Utils.lerInteiro("Docente responsável: ", scanner);
        if (escolha == 0) return null;
        if (escolha < 1 || escolha > docentes.size()) {
            System.out.println("  [!] Opção inválida — docente não atribuído.");
            return null;
        }
        return docentes.get(escolha - 1).getSigla();
    }

    private UnidadeCurricular encontrarUC(ArrayList<UnidadeCurricular> lista, String nome) {
        for (UnidadeCurricular uc : lista) {
            if (uc.getNome().equalsIgnoreCase(nome)) return uc;
        }
        return null;
    }

    private UnidadeCurricular selecionarUC(ArrayList<UnidadeCurricular> lista) {
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + lista.get(i).getNome()
                    + " (Ano " + lista.get(i).getAnoCurricular() + ")");
        }
        int escolha = Utils.lerInteiro("Selecione a UC (número): ", scanner);
        if (escolha < 1 || escolha > lista.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return null;
        }
        return lista.get(escolha - 1);
    }
}
