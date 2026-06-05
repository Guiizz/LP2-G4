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
    private final AnoLetivoController anoLetivoController;
    private final Scanner scanner;

    public GestorView(GestorController gestorController, EstudanteController estudanteController, DocenteController docenteController, DepartamentoController departamentoController, CursoController cursoController, UnidadeCurricularController unidadeCurricularController, AvaliacaoController avaliacaoController, InscricaoController inscricaoController, AnoLetivoController anoLetivoController, Scanner scanner) {
        this.gestorController = gestorController;
        this.estudanteController = estudanteController;
        this.docenteController = docenteController;
        this.departamentoController = departamentoController;
        this.cursoController = cursoController;
        this.unidadeCurricularController = unidadeCurricularController;
        this.avaliacaoController = avaliacaoController;
        this.inscricaoController = inscricaoController;
        this.anoLetivoController = anoLetivoController;
        this.scanner = scanner;
    }

    public void iniciar(Gestor gestor) {
        // Ordem segue o fluxo natural de configuração do sistema
        String[] opcoes = {
                "Ano Letivo",
                "Gerir Departamentos",
                "Gerir Cursos",
                "Gerir Unidades Curriculares",
                "Gerir Docentes",
                "Gerir Estudantes",
                "Gerir Avaliações",
                "Ver a minha Ficha",
                "Gerir Gestores"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ÁREA DO GESTOR [" + gestor.getEmail() + "]", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: new AnoLetivoView(anoLetivoController, estudanteController, scanner).iniciar(); break;
                    case 2: new DepartamentoView(departamentoController, cursoController, scanner).iniciar(); break;
                    case 3: new CursoView(cursoController, departamentoController, unidadeCurricularController, estudanteController, scanner).iniciar(); break;
                    case 4: new UnidadeCurricularView(unidadeCurricularController, docenteController, anoLetivoController, cursoController, scanner).iniciar(); break;
                    case 5: new DocenteGestorView(docenteController, estudanteController, unidadeCurricularController, cursoController, scanner).iniciar(); break;
                    case 6: new EstudanteGestorView(estudanteController, cursoController, inscricaoController, docenteController, scanner).iniciar(); break;
                    case 7: new AvaliacaoView(avaliacaoController, unidadeCurricularController, cursoController, scanner).iniciar(); break;
                    case 8:
                        Utils.limparEcra();
                        System.out.println("\n" + gestor);
                        Utils.pausar(scanner);
                        break;
                    case 9: new GestorMenuView(gestorController, scanner).iniciar(); break;
                    case 0: System.out.println("  A terminar sessão..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }
}