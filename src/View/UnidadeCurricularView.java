package View;

import Controller.DocenteController;
import Controller.UnidadeCurricularController;
import Model.Docente;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Scanner;

public class UnidadeCurricularView {

    private final UnidadeCurricularController unidadeCurricularController;
    private final DocenteController docenteController;
    private final Scanner scanner;

    public UnidadeCurricularView(UnidadeCurricularController unidadeCurricularController, DocenteController docenteController, Scanner scanner) {
        this.unidadeCurricularController = unidadeCurricularController;
        this.docenteController = docenteController;
        this.scanner = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Registar Unidade Curricular",
                "Listar Unidades Curriculares",
                "Atualizar Unidade Curricular",
                "Remover Unidade Curricular",
                "Definir Momentos de Avaliação",
                "Iniciar Unidade Curricular"
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
                    case 5: definirMomentos(); break;
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

        UnidadeCurricular uc = new UnidadeCurricular(nome, ano, 0);
        unidadeCurricularController.adicionarUnidade(uc);
        System.out.println("  [✓] Unidade Curricular '" + nome + "' registada com sucesso.");
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

    private void definirMomentos() {
        System.out.println("\n--- Definir Momentos de Avaliação ---");
        ArrayList<UnidadeCurricular> lista = unidadeCurricularController.listarUnidades();
        if (lista.isEmpty()) { System.out.println("  [!] Não existem UCs registadas."); Utils.pausar(scanner); return; }

        System.out.println("  UCs disponíveis:");
        UnidadeCurricular uc = selecionarUC(lista);
        if (uc == null) return;

        if (uc.getMomentosAvaliacao().size() >= 3) {
            System.out.println("  [!] A UC já tem 3 momentos definidos:");
            for (var m : uc.getMomentosAvaliacao()) System.out.println("    " + m);
            Utils.pausar(scanner); return;
        }

        System.out.println("  Momentos: " + uc.getMomentosAvaliacao().size() + "/3 | Soma: "
                + String.format("%.1f", uc.somaPesos()) + "% | Disponível: "
                + String.format("%.1f", 100.0 - uc.somaPesos()) + "%");

        String nomeMomento = Utils.lerCampo("Nome do momento (ex: Frequência, Exame, Projeto): ", scanner);
        double peso = Utils.lerDouble("Peso (%): ", scanner);

        unidadeCurricularController.adicionarMomento(uc, nomeMomento, peso);
        System.out.println("  [✓] Momento '" + nomeMomento + "' (" + String.format("%.1f", peso) + "%) adicionado.");
        Utils.pausar(scanner);
    }

    private void iniciarUC() {
        System.out.println("\n--- Iniciar UC ---");
        ArrayList<UnidadeCurricular> lista = unidadeCurricularController.listarUnidades();
        if (lista.isEmpty()) { System.out.println("  [!] Não existem UCs registadas."); Utils.pausar(scanner); return; }

        System.out.println("  UCs disponíveis:");
        UnidadeCurricular uc = selecionarUC(lista);
        if (uc == null) return;

        unidadeCurricularController.iniciarUC(uc);
        System.out.println("  [✓] UC '" + uc.getNome() + "' iniciada com sucesso!");
        Utils.pausar(scanner);
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
