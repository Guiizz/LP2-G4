package View;

import Controller.DocenteController;
import Controller.EstudanteController;
import Controller.AvaliacaoController;
import Model.Docente;
import Model.Estudante;
import Model.Inscricao;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.List;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;

    /**
     * View da área pessoal do Docente.
     * Apenas acessível após autenticação via LoginView.
     */
public class DocenteView {

    private AvaliacaoController avaliacaoController;
    private DocenteController controller;
    private EstudanteController estudanteController;
    private Scanner scanner;

        public DocenteView(DocenteController controller, EstudanteController estudanteController,
                           AvaliacaoController avaliacaoController,Scanner scanner) {
            this.controller = controller;
            this.scanner = scanner;
            this.estudanteController = estudanteController;
            this.avaliacaoController = avaliacaoController;
        }

        /**
     * Ponto de entrada da área do docente.
     * @param docente O docente autenticado.
     */
    public void iniciar(Docente docente) {
        String[] opcoes = {
                "Ver a minha Ficha",
                "Ver as minhas Unidades Curriculares",
                "Ver a lista dos meus Alunos",
                "Lançar nota a um aluno"
        };

        int opcao;
        do {
            opcao = Utils.mostrarMenu("ÁREA DO DOCENTE  [" + docente.getSigla() + "]", opcoes, scanner);
            switch (opcao) {
                case 1: verFicha(docente); break;
                case 2: verUnidadesCurriculares(docente); break;
                case 3: verAlunos(docente); break;
                case 4: lancarNotaAluno(docente); break;
                case 0: System.out.println("  A terminar sessão…"); break;
            }
        } while (opcao != 0);
    }

    /**
     * Ações do Docente
     * @param docente
     */

    private void verFicha(Docente docente) {
        System.out.println("\n" + docente);
    }

    private void verUnidadesCurriculares(Docente docente) {
        System.out.println("\n— As minhas Unidades Curriculares —");
        List<UnidadeCurricular> ucs = docente.getUnidadesLecionadas();

        if (ucs == null || ucs.isEmpty()) {
            System.out.println("  (sem unidades curriculares atribuídas)");
            return;
        }

        for (int i = 0; i < ucs.size(); i++) {
            UnidadeCurricular uc = ucs.get(i);
            System.out.println("  " + (i + 1) + ". " + uc.getNome()
                    + " | Ano: " + uc.getAnoCurricular()
                    + " | ECTS: " + uc.getEts());
        }
    }

    private void verAlunos(Docente docente) {
        System.out.println("\n— Procurar Aluno —");

        List<UnidadeCurricular> ucsDocente = docente.getUnidadesLecionadas();

        if (ucsDocente == null || ucsDocente.isEmpty()) {
            System.out.println("  (sem unidades curriculares atribuídas)");
            return;
        }

        System.out.print("  Número mecanográfico: ");
        String numMec = scanner.nextLine().trim();

        Estudante estudante;
        try {
            estudante = estudanteController.procurarPorNumMecanografico(numMec);
        } catch (IllegalArgumentException e) {
            System.out.println("  [!] " + e.getMessage());
            return;
        }

        boolean eAluno = false;
        for (Inscricao inscricao : estudante.getInscricoes()) {
            for (UnidadeCurricular ucCurso : inscricao.getCurso().getUnidades()) {
                for (UnidadeCurricular ucDocente : ucsDocente) {
                    if (ucCurso.getNome().equals(ucDocente.getNome())) {
                        eAluno = true;
                        break;
                    }
                }
                if (eAluno) break;
            }
            if (eAluno) break;
        }

        if (!eAluno) {
            System.out.println("  [!] Este estudante não está inscrito em nenhuma das suas unidades curriculares.");
            return;
        }

        System.out.println("\n" + estudante);
    }
        private void lancarNotaAluno(Docente docente) {
            System.out.println("\n— Lançar Nota a um Aluno —");

            // 1. Listar UCs do docente
            List<UnidadeCurricular> ucs = docente.getUnidadesLecionadas();
            if (ucs == null || ucs.isEmpty()) {
                System.out.println("  (sem unidades curriculares atribuídas)");
                return;
            }

            for (int i = 0; i < ucs.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + ucs.get(i).getNome());
            }

            System.out.print("  Escolha a UC: ");
            int idx;
            try {
                idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (idx < 0 || idx >= ucs.size()) {
                    System.out.println("  [!] Opção inválida.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Opção inválida.");
                return;
            }
            UnidadeCurricular uc = ucs.get(idx);

            // 2. Pedir número mecanográfico
            System.out.print("  Nº mecanográfico do aluno: ");
            String numMec = scanner.nextLine().trim();

            // 3. Pedir nome do momento
            System.out.print("  Nome do momento (ex: Teste 1): ");
            String nomeMomento = scanner.nextLine().trim();

            // 4. Pedir peso
            System.out.print("  Peso (%): ");
            double peso;
            try {
                peso = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Peso inválido.");
                return;
            }

            // 5. Pedir data
            System.out.print("  Data (dd/MM/yyyy): ");
            Date data;
            try {
                data = new SimpleDateFormat("dd/MM/yyyy").parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("  [!] Data inválida.");
                return;
            }

            // 6. Pedir nota
            System.out.print("  Nota (0-20): ");
            double nota;
            try {
                nota = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Nota inválida.");
                return;
            }

            // 7. Chamar o controller — a View não toca em nenhum Model
            try {
                avaliacaoController.lancarNotaAluno(docente, uc, numMec, nomeMomento, peso, data, nota);
                System.out.println("  [✓] Nota lançada com sucesso!");
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
}
