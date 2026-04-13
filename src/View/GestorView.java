package View;

import Controller.CursoController;
import Controller.DepartamentoController;
import Controller.DocenteController;
import Controller.EstudanteController;
import Controller.GestorController;
import Controller.UnidadeCurricularController;
import Model.Curso;
import Model.Departamento;
import Model.Docente;
import Model.Estudante;
import Model.Gestor;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GestorView {

    private final GestorController gestorController;
    private final EstudanteController estudanteController;
    private final DocenteController docenteController;
    private final DepartamentoController departamentoController;
    private final CursoController cursoController;
    private final UnidadeCurricularController unidadeCurricularController;
    private final Scanner scanner;

    public GestorView(GestorController gestorController,
                      EstudanteController estudanteController,
                      DocenteController docenteController,
                      DepartamentoController departamentoController,
                      CursoController cursoController,
                      UnidadeCurricularController unidadeCurricularController,
                      Scanner scanner) {
        this.gestorController = gestorController;
        this.estudanteController = estudanteController;
        this.docenteController = docenteController;
        this.departamentoController = departamentoController;
        this.cursoController = cursoController;
        this.unidadeCurricularController = unidadeCurricularController;
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
                "Gerir Unidades Curriculares"
        };

        int opcao;
        do {
            opcao = Utils.mostrarMenu("ÁREA DO GESTOR [" + gestor.getEmail() + "]", opcoes, scanner);

            switch (opcao) {
                case 1:
                    verFicha(gestor);
                    break;
                case 2:
                    menuGestores();
                    break;
                case 3:
                    menuEstudantes();
                    break;
                case 4:
                    menuDocentes();
                    break;
                case 5:
                    menuDepartamentos();
                    break;
                case 6:
                    menuCursos();
                    break;
                case 7:
                    menuUnidadesCurriculares();
                    break;
                case 0:
                    System.out.println("  A terminar sessão...");
                    break;
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
                    case 1:
                        registarGestor();
                        break;
                    case 2:
                        listarGestores();
                        break;
                    case 3:
                        procurarGestorPorNif();
                        break;
                    case 4:
                        removerGestor();
                        break;
                    case 0:
                        System.out.println("  A voltar...");
                        break;
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
                    case 1:
                        registarEstudante();
                        break;
                    case 2:
                        listarEstudantes();
                        break;
                    case 3:
                        procurarEstudante();
                        break;
                    case 4:
                        removerEstudante();
                        break;
                    case 0:
                        System.out.println("  A voltar...");
                        break;
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
                    case 1:
                        registarDocente();
                        break;
                    case 2:
                        listarDocentes();
                        break;
                    case 3:
                        procurarDocente();
                        break;
                    case 4:
                        removerDocente();
                        break;
                    case 0:
                        System.out.println("  A voltar...");
                        break;
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
                    case 1:
                        registarDepartamento();
                        break;
                    case 2:
                        listarDepartamentos();
                        break;
                    case 3:
                        procurarDepartamento();
                        break;
                    case 0:
                        System.out.println("  A voltar...");
                        break;
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

        Departamento departamento = departamentoController.registarDepartamento(nome, sigla);
        System.out.println("  [✓] Departamento registado com sucesso.");
        System.out.println(departamento);
    }

    private void listarDepartamentos() {
        System.out.println("\n--- Lista de Departamentos ---");
        ArrayList<Departamento> departamentos = departamentoController.listarDepartamentos();

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

        Departamento departamento = departamentoController.procurarDepartamento(sigla);

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
                "Procurar Curso por Nome",
                "Associar UC a Curso",
                "Listar UCs de um Curso por Ano",
                "Remover Curso"
        };

        int opcao;
        do {
            opcao = Utils.mostrarMenu("GESTÃO DE CURSOS", opcoes, scanner);

            try {
                switch (opcao) {
                    case 1:
                        registarCurso();
                        break;
                    case 2:
                        listarCursos();
                        break;
                    case 3:
                        procurarCurso();
                        break;
                    case 4:
                        associarUCaCurso();
                        break;
                    case 5:
                        listarUCsCursoPorAno();
                        break;
                    case 6:
                        removerCurso();
                        break;
                    case 0:
                        System.out.println("  A voltar...");
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void registarCurso() {
        System.out.println("\n--- Registar Curso ---");

        ArrayList<Departamento> departamentos = departamentoController.listarDepartamentos();
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

        Departamento departamento = departamentoController.procurarDepartamento(sigla);
        if (departamento == null) {
            System.out.println("  [!] Departamento não encontrado.");
            return;
        }

        Curso curso = cursoController.registarCurso(nomeCurso, departamento);
        departamento.adicionarCurso(curso);

        System.out.println("  [✓] Curso registado com sucesso.");
        mostrarCursoDetalhado(curso);
    }

    private void listarCursos() {
        System.out.println("\n--- Lista de Cursos ---");
        ArrayList<Curso> cursos = cursoController.listarCursos();

        if (cursos.isEmpty()) {
            System.out.println("  (sem cursos registados)");
            return;
        }

        for (Curso c : cursos) {
            mostrarCursoDetalhado(c);
            System.out.println();
        }
    }

    private void procurarCurso() {
        System.out.print("\nNome do curso: ");
        String nome = scanner.nextLine().trim();

        Curso curso = cursoController.procurarPorNome(nome);

        if (curso == null) {
            System.out.println("  [!] Curso não encontrado.");
            return;
        }

        System.out.println();
        mostrarCursoDetalhado(curso);
    }

    private void associarUCaCurso() {
        System.out.println("\n--- Associar Unidade Curricular a Curso ---");

        ArrayList<Curso> cursos = cursoController.listarCursos();
        if (cursos.isEmpty()) {
            System.out.println("  [!] Não existem cursos registados.");
            return;
        }

        ArrayList<UnidadeCurricular> unidades = unidadeCurricularController.listarUnidades();
        if (unidades.isEmpty()) {
            System.out.println("  [!] Não existem unidades curriculares registadas.");
            return;
        }

        System.out.print("Nome do curso: ");
        String nomeCurso = scanner.nextLine().trim();
        Curso curso = cursoController.procurarPorNome(nomeCurso);

        if (curso == null) {
            System.out.println("  [!] Curso não encontrado.");
            return;
        }

        System.out.println("Unidades Curriculares disponíveis:");
        for (UnidadeCurricular uc : unidades) {
            System.out.println("  - " + uc.getNome() + " | Ano " + uc.getAnoCurricular() + " | " + uc.getEts() + " ECTS");
        }

        System.out.print("Nome da UC a associar: ");
        String nomeUC = scanner.nextLine().trim();

        UnidadeCurricular uc = procurarUCPorNome(nomeUC);
        if (uc == null) {
            System.out.println("  [!] Unidade Curricular não encontrada.");
            return;
        }

        cursoController.adicionarUnidadeCurricular(curso, uc);
        System.out.println("  [✓] Unidade Curricular associada com sucesso.");
    }

    private void listarUCsCursoPorAno() {
        System.out.println("\n--- Listar UCs de um Curso por Ano ---");

        System.out.print("Nome do curso: ");
        String nomeCurso = scanner.nextLine().trim();
        Curso curso = cursoController.procurarPorNome(nomeCurso);

        if (curso == null) {
            System.out.println("  [!] Curso não encontrado.");
            return;
        }

        int ano = lerInteiro("Ano curricular (1-3): ");
        List<UnidadeCurricular> ucs = cursoController.listarUCsPorAno(curso, ano);

        if (ucs.isEmpty()) {
            System.out.println("  (sem UCs neste ano)");
            return;
        }

        for (UnidadeCurricular uc : ucs) {
            System.out.println("  - " + uc.getNome() + " | " + uc.getEts() + " ECTS");
        }
    }

    private void removerCurso() {
        System.out.println("\n--- Remover Curso ---");

        System.out.print("Nome do curso: ");
        String nomeCurso = scanner.nextLine().trim();
        Curso curso = cursoController.procurarPorNome(nomeCurso);

        if (curso == null) {
            System.out.println("  [!] Curso não encontrado.");
            return;
        }

        cursoController.removerCurso(curso, estudanteController.listarEstudante());
        System.out.println("  [✓] Curso removido com sucesso.");
    }

    // =========================================================
    // UNIDADES CURRICULARES
    // =========================================================

    private void menuUnidadesCurriculares() {
        String[] opcoes = {
                "Registar Unidade Curricular",
                "Listar Unidades Curriculares",
                "Procurar Unidade Curricular por Nome",
                "Remover Unidade Curricular"
        };

        int opcao;
        do {
            opcao = Utils.mostrarMenu("GESTÃO DE UNIDADES CURRICULARES", opcoes, scanner);

            try {
                switch (opcao) {
                    case 1:
                        registarUC();
                        break;
                    case 2:
                        listarUCs();
                        break;
                    case 3:
                        procurarUC();
                        break;
                    case 4:
                        removerUC();
                        break;
                    case 0:
                        System.out.println("  A voltar...");
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void registarUC() {
        System.out.println("\n--- Registar Unidade Curricular ---");

        System.out.print("Nome da UC: ");
        String nome = scanner.nextLine().trim();

        int ano = lerInteiro("Ano curricular (1-3): ");
        int ects = lerInteiro("ECTS: ");

        UnidadeCurricular uc = new UnidadeCurricular(nome, ano, ects, new ArrayList<>());
        unidadeCurricularController.adicionarUnidade(uc);

        System.out.println("  [✓] Unidade Curricular registada com sucesso.");
    }

    private void listarUCs() {
        System.out.println("\n--- Lista de Unidades Curriculares ---");
        ArrayList<UnidadeCurricular> unidades = unidadeCurricularController.listarUnidades();

        if (unidades.isEmpty()) {
            System.out.println("  (sem unidades curriculares registadas)");
            return;
        }

        for (UnidadeCurricular uc : unidades) {
            System.out.println("  - " + uc.getNome() + " | Ano " + uc.getAnoCurricular() + " | " + uc.getEts() + " ECTS");
        }
    }

    private void procurarUC() {
        System.out.print("\nNome da UC: ");
        String nome = scanner.nextLine().trim();

        UnidadeCurricular uc = procurarUCPorNome(nome);
        if (uc == null) {
            System.out.println("  [!] Unidade Curricular não encontrada.");
            return;
        }

        System.out.println("\n" + uc);
    }

    private void removerUC() {
        System.out.print("\nNome da UC a remover: ");
        String nome = scanner.nextLine().trim();

        UnidadeCurricular uc = procurarUCPorNome(nome);
        if (uc == null) {
            System.out.println("  [!] Unidade Curricular não encontrada.");
            return;
        }

        unidadeCurricularController.removerUnidade(uc);
        System.out.println("  [✓] Unidade Curricular removida com sucesso.");
    }

    // =========================================================
    // AUXILIARES
    // =========================================================

    private UnidadeCurricular procurarUCPorNome(String nome) {
        ArrayList<UnidadeCurricular> unidades = unidadeCurricularController.listarUnidades();

        for (UnidadeCurricular uc : unidades) {
            if (uc.getNome().equalsIgnoreCase(nome.trim())) {
                return uc;
            }
        }
        return null;
    }

    private void mostrarCursoDetalhado(Curso curso) {
        System.out.println(curso);

        List<UnidadeCurricular> unidades = curso.getUnidades();
        if (unidades == null || unidades.isEmpty()) {
            System.out.println("  UCs: sem unidades curriculares associadas");
            return;
        }

        System.out.println("  UCs associadas:");
        for (UnidadeCurricular uc : unidades) {
            System.out.println("   - " + uc.getNome() + " | Ano " + uc.getAnoCurricular() + " | " + uc.getEts() + " ECTS");
        }
    }

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

    private int lerInteiro(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Introduz um número válido.");
            }
        }
    }
}