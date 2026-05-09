package View;

import Controller.AvaliacaoController;
import Controller.DocenteController;
import Controller.EstudanteController;
import Controller.UnidadeCurricularController;
import Model.Avaliacao;
import Model.Docente;
import Model.Estudante;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class DocenteView {

    private final DocenteController docenteController;
    private final EstudanteController estudanteController;
    private final AvaliacaoController avaliacaoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final Scanner scanner;

    public DocenteView(DocenteController docenteController, EstudanteController estudanteController, AvaliacaoController avaliacaoController, UnidadeCurricularController unidadeCurricularController, Scanner scanner) {
        this.docenteController = docenteController;
        this.estudanteController = estudanteController;
        this.avaliacaoController = avaliacaoController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.scanner = scanner;
    }

    public void iniciar(Docente docente) {
        String[] opcoes = {
                "Ver a minha Ficha",
                "Ver as minhas Unidades Curriculares",
                "Ver os meus Alunos",
                "Lançar Avaliação",
                "Atualizar os meus Dados"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ÁREA DO DOCENTE [" + docente.getEmail() + "]", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: verFicha(docente);   break;
                    case 2: verUCs(docente);     break;
                    case 3: verAlunos(docente);  break;
                    case 4: lancarAvaliacao();   break;
                    case 5: atualizar(docente);  break;
                    case 0: System.out.println("  A terminar sessão..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void verFicha(Docente docente) {
        Utils.limparEcra();
        System.out.println("\n--- A minha Ficha ---");
        System.out.println(docente);
        Utils.pausar(scanner);
    }

    private void verUCs(Docente docente) {
        Utils.limparEcra();
        System.out.println("\n--- As minhas Unidades Curriculares ---");
        ArrayList<UnidadeCurricular> todasUCs = unidadeCurricularController.listarUnidades();
        boolean encontrou = false;
        for (UnidadeCurricular uc : todasUCs) {
            if (docente.getSigla().equalsIgnoreCase(uc.getDocenteResponsavel())) {
                System.out.println("  - " + uc.getNome() + " (Ano " + uc.getAnoCurricular() + ")");
                encontrou = true;
            }
        }
        if (!encontrou) System.out.println("  (sem unidades curriculares atribuídas)");
        Utils.pausar(scanner);
    }

    private void verAlunos(Docente docente) {
        Utils.limparEcra();
        System.out.println("\n--- Os meus Alunos ---");
        ArrayList<UnidadeCurricular> todasUCs        = unidadeCurricularController.listarUnidades();
        ArrayList<Estudante>         todosEstudantes = estudanteController.listarEstudante();

        boolean encontrouAluno = false;
        for (UnidadeCurricular uc : todasUCs) {
            if (docente.getSigla().equalsIgnoreCase(uc.getDocenteResponsavel())) {
                System.out.println("  UC: " + uc.getNome());
                for (Estudante e : todosEstudantes) {
                    System.out.println("    - " + e.getNome() + " (" + e.getNumMecanografico() + ")");
                    encontrouAluno = true;
                }
            }
        }
        if (!encontrouAluno) System.out.println("  (sem alunos associados)");
        Utils.pausar(scanner);
    }

    private void lancarAvaliacao() {
        System.out.println("\n--- Lançar Avaliação --- (0 para cancelar)");

        ArrayList<UnidadeCurricular> todasUCs = unidadeCurricularController.listarUnidades();
        if (todasUCs.isEmpty()) {
            System.out.println("  [!] Não existem UCs registadas.");
            Utils.pausar(scanner);
            return;
        }

        System.out.println("  UCs disponíveis:");
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
        Date   data = Utils.lerDataAvaliacao("Data (DD/MM/AAAA): ", scanner);
        double nota = Utils.lerDouble("Nota (0-20): ", scanner);

        List<UnidadeCurricular> ucs = new ArrayList<>();
        ucs.add(ucEscolhida);

        Avaliacao a = avaliacaoController.registarAvaliacao(ucs, peso, data, nota);
        System.out.println("  [✓] Avaliação lançada com sucesso.");
        System.out.println("  " + a);
        Utils.pausar(scanner);
    }

    private void atualizar(Docente docente) {
        System.out.println("\n--- Atualizar os meus Dados --- (0 para cancelar)");
        System.out.println("  Dados atuais: " + docente.getNome() + " | " + docente.getMorada());

        String novoNome   = Utils.lerCampo("Novo nome (Enter para manter): ", scanner);
        String novaMorada = Utils.lerCampo("Nova morada (Enter para manter): ", scanner);

        if (!novoNome.isEmpty())   docente.setNome(novoNome);
        if (!novaMorada.isEmpty()) docente.setMorada(novaMorada);

        docenteController.atualizarDocente(docente);
        System.out.println("  [✓] Dados atualizados com sucesso.");
        Utils.pausar(scanner);
    }
}