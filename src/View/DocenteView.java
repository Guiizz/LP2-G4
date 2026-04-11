package View;

import BLL.DocenteBLL;
import Controller.DocenteController;
import Model.Docente;
import Utils.Utils;

import java.util.Scanner;

    /**
     * View da área pessoal do Docente.
     * Apenas acessível após autenticação via LoginView.
     */
public class DocenteView {

    private DocenteController controller;
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
        System.out.println("  (funcionalidade a implementar)");
    }

    private void verAlunos(Docente docente) {
        System.out.println("\n— Os meus Alunos —");
        System.out.println("  (funcionalidade a implementar)");
    }
}
