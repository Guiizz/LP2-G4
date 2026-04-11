package View;

import BLL.CursoBLL;
import BLL.DepartamentoBLL;
import Controller.DocenteController;
import Controller.EstudanteController;
import Controller.GestorController;
import Model.Curso;
import Model.Departamento;
import Model.Docente;
import Model.Estudante;
import Model.Gestor;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class GestorView {

    private GestorController gestorController;
    private EstudanteController estudanteController;
    private DocenteController docenteController;
    private DepartamentoBLL departamentoBLL;
    private CursoBLL cursoBLL;
    private Scanner scanner;

    public GestorView(GestorController gestorController, EstudanteController estudanteController, DocenteController docenteController, Scanner scanner) {
        this.gestorController = gestorController;
        this.estudanteController = estudanteController;
        this.docenteController = docenteController;
        this.departamentoBLL = new DepartamentoBLL();
        this.cursoBLL = new CursoBLL();
        this.scanner = scanner;
    }

    public void iniciar(Gestor gestor) {
        String[] opcoes = {
                "Ver a minha Ficha",
                "Gerir Gestores",
                "Gerir Estudantes",
                "Gerir Docentes",
                "Gerir Departamentos",
                "Gerir Cursos"
        };

        int opcao;
        do {
            opcao = Utils.mostrarMenu("ÁREA DO GESTOR [" + gestor.getEmail() + "]", opcoes, scanner);

            switch (opcao) {
                case 1: verFicha(gestor); break;
                case 2: menuGestores(); break;
                case 3: menuEstudantes(); break;
                case 4: menuDocentes(); break;
                case 5: menuDepartamentos(); break;
                case 6: menuCursos(); break;
                case 0: System.out.println("  A terminar sessão...");break;
            }
        } while (opcao != 0);
    }

    // =========================================================
    // FICHA
    // =========================================================

    private void verFicha(Gestor gestor) {
        System.out.println("\n" + gestor);
    }

    // =========================================================
    // GESTORES
    // =========================================================

    private void menuGestores() {
        String[] opcoes = {
                "Registar Gestor",
                "Listar Gestores",
                "Procurar Gestor por NIF",
                "Remover Gestor"
        };

        int opcao;
        do {
            opcao = Utils.mostrarMenu("GESTÃO DE GESTORES", opcoes, scanner);

            try {
                switch (opcao) {
                    case 1: registarGestor(); break;
                    case 2: listarGestores(); break;
                    case 3: procurarGestorPorNif(); break;
                    case 4: removerGestor(); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void registarGestor() {
        System.out.println("\n--- Registar Gestor ---");

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        LocalDate dataNascimento = lerData("Data de nascimento (AAAA-MM-DD): ");

        System.out.print("NIF: ");
        String nif = scanner.nextLine().trim();

        System.out.print("Morada: ");
        String morada = scanner.nextLine().trim();

        System.out.print("Email (@issmf.pt): ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        gestorController.registarGestor(nome, dataNascimento, nif, morada, email, password);
        System.out.println("  [✓] Gestor registado com sucesso.");
    }

    private void listarGestores() {
        System.out.println("\n--- Lista de Gestores ---");
        ArrayList<Gestor> gestores = gestorController.listarGestores();

        if (gestores.isEmpty()) {
            System.out.println("  (sem gestores registados)");
            return;
        }

        for (Gestor g : gestores) {
            System.out.println(g);
            System.out.println();
        }
    }

    private void procurarGestorPorNif() {
        System.out.print("\nNIF do gestor: ");
        String nif = scanner.nextLine().trim();

        Gestor gestor = gestorController.procurarPorNif(nif);

        if (gestor == null) {
            System.out.println("  [!] Gestor não encontrado.");
            return;
        }

        System.out.println("\n" + gestor);
    }

    private void removerGestor() {
        System.out.print("\nNIF do gestor a remover: ");
        String nif = scanner.nextLine().trim();

        gestorController.removerGestor(nif);
        System.out.println("  [✓] Gestor removido com sucesso.");
    }

    // =========================================================
    // ESTUDANTES
    // =========================================================

    private void menuEstudantes() {
        String[] opcoes = {
                "Registar Estudante",
                "Listar Estudantes",
                "Procurar Estudante por Nº Mecanográfico",
                "Remover Estudante"
        };

        int opcao;
        do {
            opcao = Utils.mostrarMenu("GESTÃO DE ESTUDANTES", opcoes, scanner);

            try {
                switch (opcao) {
                    case 1: registarEstudante(); break;
                    case 2: listarEstudantes(); break;
                    case 3: procurarEstudante();break;
                    case 4: removerEstudante(); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void registarEstudante() {
        System.out.println("\n--- Registar Estudante ---");

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        LocalDate dataNascimento = lerData("Data de nascimento (AAAA-MM-DD): ");

        System.out.print("NIF: ");
        String nif = scanner.nextLine().trim();

        System.out.print("Morada: ");
        String morada = scanner.nextLine().trim();

        Estudante estudante = estudanteController.registarEstudante(nome, dataNascimento, nif, morada);

        System.out.println("  [✓] Estudante registado com sucesso.");
        System.out.println("  Nº Mecanográfico: " + estudante.getNumMecanografico());
        System.out.println("  E-mail: " + estudante.getEmail());
        System.out.println("  Password: " + estudante.getPassword());
    }

    private void listarEstudantes() {
        System.out.println("\n--- Lista de Estudantes ---");
        ArrayList<Estudante> estudantes = estudanteController.listarEstudante();

        if (estudantes.isEmpty()) {
            System.out.println("  (sem estudantes registados)");
            return;
        }

        for (Estudante e : estudantes) {
            System.out.println(e);
            System.out.println();
        }
    }

    private void procurarEstudante() {
        System.out.print("\nNº Mecanográfico: ");
        String numero = scanner.nextLine().trim();

        Estudante estudante = estudanteController.procurarPorNumMecanografico(numero);

        if (estudante == null) {
            System.out.println("  [!] Estudante não encontrado.");
            return;
        }

        System.out.println("\n" + estudante);
    }

    private void removerEstudante() {
        System.out.print("\nNº Mecanográfico do estudante a remover: ");
        String numero = scanner.nextLine().trim();

        estudanteController.removerEstudante(numero);
        System.out.println("  [✓] Estudante removido com sucesso.");
    }

    // =========================================================
    // DOCENTES
    // =========================================================

    private void menuDocentes() {
        String[] opcoes = {
                "Registar Docente",
                "Listar Docentes",
                "Procurar Docente por Sigla",
                "Remover Docente"
        };

        int opcao;
        do {
            opcao = Utils.mostrarMenu("GESTÃO DE DOCENTES", opcoes, scanner);

            try {
                switch (opcao) {
                    case 1: registarDocente(); break;
                    case 2: listarDocentes(); break;
                    case 3: procurarDocente(); break;
                    case 4: removerDocente(); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void registarDocente() {
        System.out.println("\n--- Registar Docente ---");

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        LocalDate dataNascimento = lerData("Data de nascimento (AAAA-MM-DD): ");

        System.out.print("NIF: ");
        String nif = scanner.nextLine().trim();

        System.out.print("Morada: ");
        String morada = scanner.nextLine().trim();

        System.out.print("Sigla (3 letras): ");
        String sigla = scanner.nextLine().trim();

        Docente docente = new Docente(nome, dataNascimento, nif, morada, sigla, new ArrayList<>());
        docenteController.registarDocente(docente);

        System.out.println("  [✓] Docente registado com sucesso.");
        System.out.println("  E-mail: " + docente.getEmail());
        System.out.println("  Password: " + docente.getPassword());
    }

    private void listarDocentes() {
        System.out.println("\n--- Lista de Docentes ---");
        ArrayList<Docente> docentes = docenteController.listarDocentes();

        if (docentes.isEmpty()) {
            System.out.println("  (sem docentes registados)");
            return;
        }

        for (Docente d : docentes) {
            System.out.println(d.toStringDetalhado());
            System.out.println();
        }
    }

    private void procurarDocente() {
        System.out.print("\nSigla do docente: ");
        String sigla = scanner.nextLine().trim();

        Docente docente = docenteController.procurarPorSigla(sigla);

        if (docente == null) {
            System.out.println("  [!] Docente não encontrado.");
            return;
        }

        System.out.println("\n" + docente.toStringDetalhado());
    }

    private void removerDocente() {
        System.out.print("\nSigla do docente a remover: ");
        String sigla = scanner.nextLine().trim();

        docenteController.removerDocente(sigla);
        System.out.println("  [✓] Docente removido com sucesso.");
    }

    // =========================================================
    // DEPARTAMENTOS
    // =========================================================

    private void menuDepartamentos() {
        String[] opcoes = {
                "Registar Departamento",
                "Listar Departamentos",
                "Procurar Departamento por Sigla"
        };

        int opcao;
        do {
            opcao = Utils.mostrarMenu("GESTÃO DE DEPARTAMENTOS", opcoes, scanner);

            try {
                switch (opcao) {
                    case 1: registarDepartamento();break;
                    case 2: listarDepartamentos(); break;
                    case 3: procurarDepartamento(); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void registarDepartamento() {
        System.out.println("\n--- Registar Departamento ---");

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Sigla (3 letras): ");
        String sigla = scanner.nextLine().trim();

        Departamento departamento = departamentoBLL.registarDepartamento(nome, sigla);
        System.out.println("  [✓] Departamento registado com sucesso.");
        System.out.println(departamento);
    }

    private void listarDepartamentos() {
        System.out.println("\n--- Lista de Departamentos ---");
        ArrayList<Departamento> departamentos = departamentoBLL.listarDepartamentos();

        if (departamentos.isEmpty()) {
            System.out.println("  (sem departamentos registados)");
            return;
        }

        for (Departamento d : departamentos) {
            System.out.println(d);
        }
    }

    private void procurarDepartamento() {
        System.out.print("\nSigla do departamento: ");
        String sigla = scanner.nextLine().trim();

        Departamento departamento = departamentoBLL.procurarDepartamento(sigla);

        if (departamento == null) {
            System.out.println("  [!] Departamento não encontrado.");
            return;
        }

        System.out.println("\n" + departamento);
    }

    // =========================================================
    // CURSOS
    // =========================================================

    private void menuCursos() {
        String[] opcoes = {
                "Registar Curso",
                "Listar Cursos",
                "Procurar Curso por Nome"
        };

        int opcao;
        do {
            opcao = Utils.mostrarMenu("GESTÃO DE CURSOS", opcoes, scanner);

            try {
                switch (opcao) {
                    case 1: registarCurso(); break;
                    case 2: listarCursos(); break;
                    case 3: procurarCurso(); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void registarCurso() {
        System.out.println("\n--- Registar Curso ---");

        ArrayList<Departamento> departamentos = departamentoBLL.listarDepartamentos();
        if (departamentos.isEmpty()) {
            System.out.println("  [!] Não existem departamentos registados. Registe primeiro um departamento.");
            return;
        }

        System.out.print("Nome do curso: ");
        String nomeCurso = scanner.nextLine().trim();

        System.out.println("Departamentos disponíveis:");
        for (Departamento d : departamentos) {
            System.out.println("  - " + d.getSigla() + " | " + d.getNome());
        }

        System.out.print("Sigla do departamento: ");
        String sigla = scanner.nextLine().trim();

        Departamento departamento = departamentoBLL.procurarDepartamento(sigla);
        if (departamento == null) {
            System.out.println("  [!] Departamento não encontrado.");
            return;
        }

        Curso curso = cursoBLL.registarCurso(nomeCurso, departamento);
        departamento.adicionarCurso(curso);

        System.out.println("  [✓] Curso registado com sucesso.");
        System.out.println(curso);
    }

    private void listarCursos() {
        System.out.println("\n--- Lista de Cursos ---");
        ArrayList<Curso> cursos = cursoBLL.listarCursos();

        if (cursos.isEmpty()) {
            System.out.println("  (sem cursos registados)");
            return;
        }

        for (Curso c : cursos) {
            System.out.println(c);
            System.out.println();
        }
    }

    private void procurarCurso() {
        System.out.print("\nNome do curso: ");
        String nome = scanner.nextLine().trim();

        Curso curso = cursoBLL.procurarPorNome(nome);

        if (curso == null) {
            System.out.println("  [!] Curso não encontrado.");
            return;
        }

        System.out.println("\n" + curso);
    }

    // =========================================================
    // AUXILIAR
    // =========================================================

    private LocalDate lerData(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("  [!] Data inválida. Use o formato AAAA-MM-DD.");
            }
        }
    }
}