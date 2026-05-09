package View;

import Controller.*;
import Model.Gestor;
import Utils.Utils;

import java.util.Scanner;

public class GestorView {

    private final GestorController gestorController;
    private final EstudanteController estudanteController;
    private final DocenteController docenteController;
    private final DepartamentoController departamentoController;
    private final CursoController cursoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final AvaliacaoController avaliacaoController;
    private final InscricaoController inscricaoController;
    private final Scanner scanner;

    public GestorView(GestorController gestorController, EstudanteController estudanteController, DocenteController docenteController, DepartamentoController departamentoController, CursoController cursoController, UnidadeCurricularController unidadeCurricularController, AvaliacaoController avaliacaoController, InscricaoController inscricaoController, Scanner scanner) {
        this.gestorController = gestorController;
        this.estudanteController = estudanteController;
        this.docenteController = docenteController;
        this.departamentoController = departamentoController;
        this.cursoController = cursoController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.avaliacaoController = avaliacaoController;
        this.inscricaoController = inscricaoController;
        this.scanner = scanner;
    }

    public void iniciar(Gestor gestor) {
        String[] opcoes = {
                "Ver a minha Ficha",
                "Gerir Gestores",
                "Gerir Estudantes",
                "Gerir Docentes",
                "Gerir Departamentos",
                "Gerir Cursos",
                "Gerir Unidades Curriculares",
                "Gerir Avaliações"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ÁREA DO GESTOR [" + gestor.getEmail() + "]", opcoes, scanner);
            switch (opcao) {
                case 1:
                    Utils.limparEcra();
                    System.out.println("\n" + gestor);
                    Utils.pausar(scanner);
                    break;
                case 2: new GestorMenuView(gestorController, scanner).iniciar(); break;
                case 3: new EstudanteGestorView(estudanteController, cursoController, inscricaoController, scanner).iniciar(); break;
                case 4: new DocenteGestorView(docenteController, scanner).iniciar(); break;
                case 5: new DepartamentoView(departamentoController, scanner).iniciar(); break;
                case 6: new CursoView(cursoController, departamentoController, unidadeCurricularController, estudanteController, scanner).iniciar(); break;
                case 7: new UnidadeCurricularView(unidadeCurricularController, docenteController, scanner).iniciar(); break;
                case 8: new AvaliacaoView(avaliacaoController, unidadeCurricularController, scanner).iniciar(); break;
                case 0: System.out.println("  A terminar sessão..."); break;
            }
        } while (opcao != 0);
    }
}
