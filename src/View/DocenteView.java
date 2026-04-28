package View;

import Controller.DocenteController;
import Controller.EstudanteController;
import Model.Docente;
import Model.Estudante;
import Model.Inscricao;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

    /**
     * View da área pessoal do Docente.
     * Apenas acessível após autenticação via LoginView.
     */
public class DocenteView {

    private DocenteController controller;
    private EstudanteController estudanteController;
    private Scanner scanner;

        public DocenteView(DocenteController controller, Scanner scanner) {
            this.controller = controller;
            this.scanner = scanner;
        }

        /**
     * Ponto de entrada da área do docente.
     * @param docente O docente autenticado.
     */
    public void iniciar(Docente docente) {
        String[] opcoes = {
                "Ver a minha Ficha",
                "Ver as minhas Unidades Curriculares",
                "Ver a lista dos meus Alunos"
        };

        int opcao;
        do {
            opcao = Utils.mostrarMenu("ÁREA DO DOCENTE  [" + docente.getSigla() + "]", opcoes, scanner);
            switch (opcao) {
                case 1: verFicha(docente); break;
                case 2: verUnidadesCurriculares(docente); break;
                case 3: verAlunos(docente); break;
                case 4: verAlunosPorUC(docente); break;
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

    private void verAlunosPorUC(Docente docente) {
        System.out.println("\n— Alunos inscritos nas minhas Unidades Curriculares —");

        List<UnidadeCurricular> ucsDocente = docente.getUnidadesLecionadas();

        if (ucsDocente == null || ucsDocente.isEmpty()) {
            System.out.println("  (sem unidades curriculares atribuídas)");
            return;
        }

        ArrayList<Estudante> todosEstudantes = estudanteController.listarEstudante();

        if (todosEstudantes == null || todosEstudantes.isEmpty()) {
            System.out.println("  (sem estudantes registados no sistema)");
            return;
        }

        boolean encontrouAlgum = false;

        for (UnidadeCurricular ucDocente : ucsDocente) {
            System.out.println("\n  UC: " + ucDocente.getNome() + " | Ano " + ucDocente.getAnoCurricular());
            boolean temAlunos = false;

            for (Estudante estudante : todosEstudantes) {
                if (estudante.getInscricoes() == null) continue;

                for (Inscricao inscricao : estudante.getInscricoes()) {
                    if (inscricao.getCurso() == null || inscricao.getCurso().getUnidades() == null) continue;

                    for (UnidadeCurricular ucCurso : inscricao.getCurso().getUnidades()) {
                        if (ucCurso.getNome().equalsIgnoreCase(ucDocente.getNome())
                                && inscricao.getAnoDeCurso() == ucDocente.getAnoCurricular()) {
                            System.out.println("    - " + estudante.getNumMecanografico()
                                    + " | " + estudante.getNome()
                                    + " | Ano letivo: " + inscricao.getAnoLetivo());
                            temAlunos = true;
                            encontrouAlgum = true;
                            break;
                        }
                    }
                }
            }

            if (!temAlunos) {
                System.out.println("    (sem alunos inscritos nesta UC)");
            }
        }

        if (!encontrouAlgum) {
            System.out.println("\n  (nenhum aluno inscrito nas suas UCs)");
        }
    }
}
