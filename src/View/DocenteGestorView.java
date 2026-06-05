package View;

import Controller.CursoController;
import Controller.DocenteController;
import Controller.EstudanteController;
import Controller.UnidadeCurricularController;
import Model.Curso;
import Model.Docente;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DocenteGestorView {

    private final DocenteController           docenteController;
    private final EstudanteController         estudanteController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final CursoController             cursoController;
    private final Scanner                     scanner;

    public DocenteGestorView(DocenteController docenteController, EstudanteController estudanteController,
                             UnidadeCurricularController unidadeCurricularController,
                             CursoController cursoController, Scanner scanner) {
        this.docenteController           = docenteController;
        this.estudanteController         = estudanteController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.cursoController             = cursoController;
        this.scanner                     = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Registar Docente",
                "Listar Docentes",
                "Procurar Docente por Sigla",
                "Atualizar Docente",
                "Remover Docente",
                "Atribuir Docente Responsável"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("GESTÃO DE DOCENTES", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: registar(); break;
                    case 2: listar(); break;
                    case 3: procurar(); break;
                    case 4: atualizar(); break;
                    case 5: remover(); break;
                    case 6: atribuirDocente(); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void registar() {
        Utils.tituloPagina("Registar Docente");
        String nome = Utils.lerNome("Nome: ", scanner);
        LocalDate data = Utils.lerDataNascimento("Data de nascimento (AAAA-MM-DD): ", scanner);

        String nif;
        while (true) {
            nif = Utils.lerNif("NIF: ", scanner);
            if (!docenteController.nifJaExiste(nif) && !estudanteController.nifJaExiste(nif)) break;
            System.out.println("  [!] Este NIF já existe no sistema.");
        }

        String morada = Utils.lerMorada("Morada: ", scanner);
        // Gerar sigla automaticamente com base nas iniciais do nome
        java.util.List<String> siglasExistentes = new java.util.ArrayList<>();
        for (Docente doc : docenteController.listarDocentes()) {
            siglasExistentes.add(doc.getSigla().toLowerCase());
        }
        String siglaGerada = Utils.gerarSigla(nome, siglasExistentes);

        System.out.println("  Sigla sugerida: " + siglaGerada);

        Docente d = new Docente(nome, data, nif, morada, siglaGerada, new ArrayList<>());
        docenteController.registarDocente(d);

        System.out.println("  [✓] Docente registado com sucesso.");
        System.out.println("  Sigla: " + siglaGerada);
        System.out.println("  Email: " + siglaGerada + "@issmf.pt");
        System.out.println("  Password inicial: enviada por email para " + siglaGerada + "@issmf.pt");
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.limparEcra();
        Utils.tituloPagina("Lista de Docentes");
        ArrayList<Docente> lista = docenteController.listarDocentes();
        if (lista.isEmpty()) { System.out.println("  (sem docentes registados)"); Utils.pausar(scanner); return; }
        for (Docente d : lista) { System.out.println(d + "\n"); }
        Utils.pausar(scanner);
    }

    private void procurar() {
        Utils.limparEcra();
        Utils.tituloPagina("Procurar Docente");
        Docente d = selecionarDocente();
        if (d == null) return;
        System.out.println("\n" + d.toStringDetalhado());
        Utils.pausar(scanner);
    }

    private void atualizar() {
        Utils.tituloPagina("Atualizar Docente");
        Docente d = selecionarDocente();
        if (d == null) return;

        System.out.println("  Dados atuais: " + d.getNome() + " | " + d.getMorada());
        String novoNome   = Utils.lerCampo("Novo nome (Enter para manter): ", scanner);
        String novaMorada = Utils.lerCampo("Nova morada (Enter para manter): ", scanner);

        if (!novoNome.isEmpty())   d.setNome(novoNome);
        if (!novaMorada.isEmpty()) d.setMorada(novaMorada);

        docenteController.atualizarDocente(d);
        System.out.println("  [✓] Docente atualizado com sucesso.");
        Utils.pausar(scanner);
    }

    private void remover() {
        Utils.tituloPagina("Remover Docente");
        Docente d = selecionarDocente();
        if (d == null) return;

        String confirmar = Utils.lerCampo("  Tem a certeza que deseja remover '" + d.getNome() + "' (" + d.getSigla() + ")? (S/N): ", scanner);
        if (!confirmar.equalsIgnoreCase("S")) {
            System.out.println("  Operação cancelada.");
            Utils.pausar(scanner);
            return;
        }
        docenteController.removerDocente(d.getSigla());
        System.out.println("  [✓] Docente removido com sucesso.");
        Utils.pausar(scanner);
    }

    private Docente selecionarDocente() {
        ArrayList<Docente> lista = docenteController.listarDocentes();
        if (lista.isEmpty()) {
            System.out.println("  [!] Não existem docentes registados.");
            Utils.pausar(scanner);
            return null;
        }
        System.out.println("\n  Docentes registados:");
        for (int i = 0; i < lista.size(); i++) {
            Docente d = lista.get(i);
            int numUCs = d.getUnidadesLecionadas() != null ? d.getUnidadesLecionadas().size() : 0;
            System.out.println("  " + (i + 1) + ". " + d.getNome()
                    + " (" + d.getSigla() + ") — " + numUCs + " UC(s)");
        }
        int escolha = Utils.lerInteiro("  Selecione (0 para voltar): ", scanner);
        if (escolha == 0) return null;
        if (escolha < 1 || escolha > lista.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return null;
        }
        return lista.get(escolha - 1);
    }

    private void atribuirDocente() {
        Utils.tituloPagina("Atribuir Docente Responsável");

        ArrayList<UnidadeCurricular> todasUCs = unidadeCurricularController.listarUnidades();
        if (todasUCs.isEmpty()) { System.out.println("  [!] Não existem UCs registadas."); Utils.pausar(scanner); return; }

        // Mostrar apenas UCs SEM docente responsável — UCs já atribuídas não aparecem como opção
        ArrayList<UnidadeCurricular> ucsSemDocente = new ArrayList<>();
        for (UnidadeCurricular uc : todasUCs) {
            if (!uc.temDocenteResponsavel()) {
                ucsSemDocente.add(uc);
            }
        }

        if (ucsSemDocente.isEmpty()) {
            System.out.println("  Todas as UCs já têm docente responsável atribuído.");
            // Mostrar as atribuições actuais para contexto
            System.out.println("\n  Atribuições actuais:");
            for (UnidadeCurricular uc : todasUCs) {
                System.out.println("  - " + uc.getNome()
                        + " (Ano " + uc.getAnoCurricular() + ")"
                        + " → " + uc.getDocenteResponsavel());
            }
            Utils.pausar(scanner);
            return;
        }

        ArrayList<Docente> docentes = docenteController.listarDocentes();
        if (docentes.isEmpty()) { System.out.println("  [!] Não existem docentes registados."); Utils.pausar(scanner); return; }

        System.out.println("  UCs sem docente responsável:");
        UnidadeCurricular uc = selecionarUC(ucsSemDocente);
        if (uc == null) return;

        System.out.println("  Docentes disponíveis:");
        for (int i = 0; i < docentes.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + docentes.get(i).getNome()
                    + " (" + docentes.get(i).getSigla() + ")");
        }
        int escolha = Utils.lerInteiro("  Selecione o docente (0 para voltar): ", scanner);
        if (escolha == 0) return;
        if (escolha < 1 || escolha > docentes.size()) {
            System.out.println("  [!] Opção inválida."); Utils.pausar(scanner); return;
        }
        Docente docente = docentes.get(escolha - 1);

        unidadeCurricularController.atribuirDocenteResponsavel(uc.getNome(), docente.getSigla());

        if (docente.getUnidadesLecionadas() != null && !docente.getUnidadesLecionadas().contains(uc)) {
            docente.getUnidadesLecionadas().add(uc);
            docenteController.atualizarDocente(docente);
        }

        System.out.println("  [✓] Docente '" + docente.getSigla() + "' atribuído à UC '" + uc.getNome() + "'.");
        Utils.pausar(scanner);
    }

    private UnidadeCurricular selecionarUC(ArrayList<UnidadeCurricular> lista) {
        for (int i = 0; i < lista.size(); i++) {
            UnidadeCurricular uc = lista.get(i);
            System.out.println("  " + (i + 1) + ". " + uc.getNome()
                    + " (Ano " + uc.getAnoCurricular() + ")"
                    + " — " + cursosComUC(uc));
        }
        int escolha = Utils.lerInteiro("  Selecione a UC (0 para voltar): ", scanner);
        if (escolha == 0) return null;
        if (escolha < 1 || escolha > lista.size()) {
            System.out.println("  [!] Opção inválida.");
            Utils.pausar(scanner);
            return null;
        }
        return lista.get(escolha - 1);
    }

    private String cursosComUC(UnidadeCurricular uc) {
        StringBuilder sb = new StringBuilder();
        for (Curso c : cursoController.listarCursos()) {
            if (c.getUnidades().contains(uc)) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(c.getNomeCurso());
            }
        }
        return sb.length() > 0 ? sb.toString() : "(sem curso)";
    }
}