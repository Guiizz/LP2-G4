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
                "Atribuir Docente Responsável"
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
                    case 5: atribuirDocente(); break;
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
        int ects    = Utils.lerInteiro("ECTS: ", scanner);

        UnidadeCurricular uc = new UnidadeCurricular(nome, ano, ects);
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
        System.out.println("\n--- Atualizar Unidade Curricular --- (0 para cancelar)");
        ArrayList<UnidadeCurricular> lista = unidadeCurricularController.listarUnidades();
        if (lista.isEmpty()) { System.out.println("  (sem unidades curriculares registadas)"); Utils.pausar(scanner); return; }

        for (UnidadeCurricular uc : lista) { System.out.println("  - " + uc.getNome()); }

        System.out.print("Nome da UC a atualizar: ");
        String nome = scanner.nextLine().trim();
        UnidadeCurricular ucEscolhida = null;
        for (UnidadeCurricular uc : lista) {
            if (uc.getNome().equalsIgnoreCase(nome)) { ucEscolhida = uc; break; }
        }
        if (ucEscolhida == null) { System.out.println("  [!] UC não encontrada."); Utils.pausar(scanner); return; }

        String novoNome = Utils.lerCampo("Novo nome (Enter para manter): ", scanner);
        if (!novoNome.isEmpty()) ucEscolhida.setNome(novoNome);

        System.out.print("Novo ano curricular (Enter para manter): ");
        String anoStr = scanner.nextLine().trim();
        if (!anoStr.isEmpty()) {
            try {
                ucEscolhida.setAnoCurricular(Integer.parseInt(anoStr));
            } catch (NumberFormatException e) {
                System.out.println("  [!] Ano inválido — mantido o valor anterior.");
            }
        }

        unidadeCurricularController.atualizarUnidade(ucEscolhida);
        System.out.println("  [✓] Unidade Curricular atualizada com sucesso.");
        Utils.pausar(scanner);
    }

    private void remover() {
        System.out.println("\n--- Remover Unidade Curricular ---");
        ArrayList<UnidadeCurricular> lista = unidadeCurricularController.listarUnidades();
        if (lista.isEmpty()) { System.out.println("  (sem unidades curriculares registadas)"); Utils.pausar(scanner); return; }

        for (UnidadeCurricular uc : lista) { System.out.println("  - " + uc.getNome()); }

        System.out.print("Nome da UC a remover: ");
        String nome = scanner.nextLine().trim();
        UnidadeCurricular ucEscolhida = null;
        for (UnidadeCurricular uc : lista) {
            if (uc.getNome().equalsIgnoreCase(nome)) { ucEscolhida = uc; break; }
        }
        if (ucEscolhida == null) { System.out.println("  [!] UC não encontrada."); Utils.pausar(scanner); return; }

        unidadeCurricularController.removerUnidade(ucEscolhida);
        System.out.println("  [✓] UC '" + nome + "' removida com sucesso.");
        Utils.pausar(scanner);
    }

    private void atribuirDocente() {
        System.out.println("\n--- Atribuir Docente Responsável ---");

        ArrayList<UnidadeCurricular> ucs = unidadeCurricularController.listarUnidades();
        if (ucs.isEmpty()) { System.out.println("  [!] Não existem UCs registadas."); Utils.pausar(scanner); return; }

        ArrayList<Docente> docentes = docenteController.listarDocentes();
        if (docentes.isEmpty()) { System.out.println("  [!] Não existem docentes registados."); Utils.pausar(scanner); return; }

        System.out.println("  UCs disponíveis:");
        for (UnidadeCurricular uc : ucs) {
            String docResp = uc.getDocenteResponsavel() != null ? uc.getDocenteResponsavel() : "sem docente";
            System.out.println("    - " + uc.getNome() + " [" + docResp + "]");
        }
        System.out.print("Nome da UC: ");
        String nomeUC = scanner.nextLine().trim();

        System.out.println("  Docentes disponíveis:");
        for (Docente d : docentes) { System.out.println("    - " + d.getNome() + " (" + d.getSigla() + ")"); }
        System.out.print("Sigla do docente: ");
        String siglaDocente = scanner.nextLine().trim();

        UnidadeCurricular ucEscolhida = null;
        for (UnidadeCurricular uc : ucs) {
            if (uc.getNome().equalsIgnoreCase(nomeUC)) {
                ucEscolhida = uc;
                break;
            }
        }

        if (ucEscolhida == null) {
            throw new IllegalArgumentException("UC não encontrada.");
        }

        Docente docente = docenteController.procurarPorSigla(siglaDocente);
        if (docente == null) {
            throw new IllegalArgumentException("Não existe docente com a sigla '" + siglaDocente + "'.");
        }

        unidadeCurricularController.atribuirDocenteResponsavel(nomeUC, siglaDocente);

        if (docente.getUnidadesLecionadas() != null && !docente.getUnidadesLecionadas().contains(ucEscolhida)) {
            docente.getUnidadesLecionadas().add(ucEscolhida);
            docenteController.atualizarDocente(docente);
        }

        System.out.println("  [✓] Docente '" + siglaDocente + "' atribuído à UC '" + nomeUC + "'.");
        Utils.pausar(scanner);
    }

    private void definirMomentos() {
        System.out.println("\n--- Definir Momentos de Avaliação ---");
        ArrayList<UnidadeCurricular> lista = unidadeCurricularController.listarUnidades();
        if (lista.isEmpty()) { System.out.println("  [!] Não existem UCs registadas."); Utils.pausar(scanner); return; }

        System.out.println("  UCs disponíveis:");
        for (UnidadeCurricular uc : lista) {
            System.out.println("    - " + uc.getNome() +
                    " [" + uc.getMomentosAvaliacao().size() + "/3 momentos | " +
                    String.format("%.1f", uc.somaPesos()) + "%]");
        }

        System.out.print("Nome da UC: ");
        String nome = scanner.nextLine().trim();
        UnidadeCurricular uc = encontrarUC(lista, nome);
        if (uc == null) { System.out.println("  [!] UC não encontrada."); Utils.pausar(scanner); return; }

        if (uc.getMomentosAvaliacao().size() >= 3) {
            System.out.println("  [!] A UC já tem 3 momentos definidos:");
            for (var m : uc.getMomentosAvaliacao()) System.out.println(m);
            Utils.pausar(scanner);
            return;
        }

        System.out.println("  Momentos já definidos: " + uc.getMomentosAvaliacao().size() + "/3");
        System.out.println("  Soma atual dos pesos: " + String.format("%.1f", uc.somaPesos()) + "%");
        System.out.println("  (Faltam " + (3 - uc.getMomentosAvaliacao().size()) + " momento(s) | Peso disponível: "
                + String.format("%.1f", 100.0 - uc.somaPesos()) + "%)");

        String nomeMomento = Utils.lerCampo("Nome do momento (ex: Frequência, Exame, Projeto): ", scanner);
        double peso = Utils.lerDouble("Peso (%): ", scanner);

        unidadeCurricularController.adicionarMomento(uc, nomeMomento, peso);

        System.out.println("  [✓] Momento '" + nomeMomento + "' (" + String.format("%.1f", peso) + "%) adicionado.");
        System.out.println("  Momentos definidos: " + uc.getMomentosAvaliacao().size() + "/3 | Soma: " + String.format("%.1f", uc.somaPesos()) + "%");
        Utils.pausar(scanner);
    }

    private void iniciarUC() {
        System.out.println("\n--- Iniciar UC ---");
        ArrayList<UnidadeCurricular> lista = unidadeCurricularController.listarUnidades();
        if (lista.isEmpty()) { System.out.println("  [!] Não existem UCs registadas."); Utils.pausar(scanner); return; }

        System.out.println("  UCs disponíveis:");
        for (UnidadeCurricular uc : lista) {
            String estado = uc.isAtiva() ? "Ativa" : "Inativa";
            System.out.println("    - " + uc.getNome() +
                    " [" + estado + " | " + uc.getMomentosAvaliacao().size() + "/3 momentos | " +
                    String.format("%.1f", uc.somaPesos()) + "%]");
        }

        System.out.print("Nome da UC a iniciar: ");
        String nome = scanner.nextLine().trim();
        UnidadeCurricular uc = encontrarUC(lista, nome);
        if (uc == null) { System.out.println("  [!] UC não encontrada."); Utils.pausar(scanner); return; }

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
}
