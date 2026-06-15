package View;

import Controller.CursoController;
import Controller.DocenteController;
import Controller.EstudanteController;
import Controller.InscricaoController;
import Model.Curso;
import Model.Estudante;
import Model.Inscricao;
import Model.Propina;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class EstudanteGestorView {

    private final EstudanteController estudanteController;
    private final CursoController cursoController;
    private final InscricaoController inscricaoController;
    private final DocenteController docenteController;
    private final Scanner scanner;

    public EstudanteGestorView(EstudanteController estudanteController, CursoController cursoController,
                               InscricaoController inscricaoController, DocenteController docenteController,
                               Scanner scanner) {
        this.estudanteController = estudanteController;
        this.cursoController = cursoController;
        this.inscricaoController = inscricaoController;
        this.docenteController = docenteController;
        this.scanner = scanner;
    }

    public void iniciar() {
        String[] opcoes = {
                "Dados do Estudante",
                "Inscrições",
                "Propinas"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("GESTÃO DE ESTUDANTES", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: menuDados(); break;
                    case 2: menuInscricoes(); break;
                    case 3: menuPropinas(); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void menuDados() {
        String[] opcoes = {
                "Registar Estudante",
                "Listar Estudantes",
                "Procurar Estudante",
                "Atualizar Estudante",
                "Remover Estudante"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("DADOS DO ESTUDANTE", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: registar(); break;
                    case 2: listar(); break;
                    case 3: procurar(); break;
                    case 4: atualizar(); break;
                    case 5: remover(); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void menuInscricoes() {
        String[] opcoes = {
                "Inscrever Estudante em Curso",
                "Desinscrever Estudante de Curso",
                "Verificar Progressão de Aluno"
        };
        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("INSCRIÇÕES", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: inscrever(); break;
                    case 2: desinscrever(); break;
                    case 3: verificarProgressao(); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void registar() {
        Utils.tituloPagina("DADOS DO ESTUDANTE", "Registar Estudante");
        String nome = Utils.lerNome("Nome: ", scanner);
        LocalDate data = Utils.lerDataNascimento("Data de nascimento (AAAA-MM-DD): ", scanner);
        String nif;
        while (true) {
            nif = Utils.lerNif("NIF: ", scanner);
            if (!estudanteController.nifJaExiste(nif) && !docenteController.nifJaExiste(nif)) break;
            System.out.println("  [!] Este NIF já existe no sistema.");
        }

        String morada = Utils.lerMorada("Morada: ", scanner);

        Estudante e = estudanteController.registarEstudante(nome, data, nif, morada);
        System.out.println("  [✓] Estudante registado com sucesso.");
        System.out.println("  Nº Mecanográfico : " + e.getNumMecanografico());
        System.out.println("  E-mail           : " + e.getEmail());
        System.out.println("  Password inicial : enviada por email");
        Utils.pausar(scanner);
    }

    private void listar() {
        Utils.tituloPagina("DADOS DO ESTUDANTE", "Lista de Estudantes");
        ArrayList<Estudante> lista = estudanteController.listarEstudante();
        if (lista.isEmpty()) { System.out.println("  (sem estudantes registados)"); Utils.pausar(scanner); return; }
        for (Estudante e : lista) { System.out.println(e + "\n"); }
        Utils.pausar(scanner);
    }

    private void procurar() {
        Utils.tituloPagina("DADOS DO ESTUDANTE", "Procurar Estudante");
        Estudante e = selecionarEstudante();
        if (e == null) return;
        System.out.println("\n" + e.toStringDetalhado());
        Utils.pausar(scanner);
    }

    private void atualizar() {
        Utils.tituloPagina("DADOS DO ESTUDANTE", "Atualizar Estudante");
        Estudante e = selecionarEstudante();
        if (e == null) return;

        System.out.println("  Dados atuais: " + e.getNome() + " | " + e.getMorada());
        System.out.println("  Email, NIF, data de nascimento e nº mecanográfico não são editáveis.");
        String novoNome   = Utils.lerCampo("Novo nome (Enter para manter): ", scanner);
        String novaMorada = Utils.lerCampo("Nova morada (Enter para manter): ", scanner);

        String nomeAtualizar   = novoNome.isEmpty()   ? e.getNome()    : novoNome;
        String moradaAtualizar = novaMorada.isEmpty() ? e.getMorada() : novaMorada;

        estudanteController.atualizarEstudante(e.getNumMecanografico(), nomeAtualizar, moradaAtualizar);
        System.out.println("  [✓] Estudante atualizado com sucesso.");
        Utils.pausar(scanner);
    }

    private void remover() {
        Utils.tituloPagina("DADOS DO ESTUDANTE", "Remover Estudante");
        Estudante e = selecionarEstudante();
        if (e == null) return;

        if (!Utils.confirmar("Remover o estudante '" + e.getNome() + "'?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }
        estudanteController.removerEstudante(e.getNumMecanografico());
        System.out.println("  [✓] Estudante removido com sucesso.");
        Utils.pausar(scanner);
    }

    private Estudante selecionarEstudante() {
        ArrayList<Estudante> lista = estudanteController.listarEstudante();
        if (lista.isEmpty()) {
            System.out.println("  [!] Não existem estudantes registados.");
            Utils.pausar(scanner);
            return null;
        }
        System.out.println("\n  Estudantes registados:");
        for (int i = 0; i < lista.size(); i++) {
            Estudante e = lista.get(i);
            String curso = (e.getCursoAtual() != null) ? e.getCursoAtual().getNomeCurso() : "sem curso";
            System.out.println("  " + (i + 1) + ". " + e.getNome()
                    + " (" + e.getNumMecanografico() + ") — " + curso);
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

    private void inscrever() {
        Utils.tituloPagina("INSCRIÇÕES", "Inscrever em Curso");
        Estudante e = selecionarEstudante();
        if (e == null) return;

        ArrayList<Curso> cursos = cursoController.listarCursos();
        if (cursos.isEmpty()) { System.out.println("  [!] Não existem cursos registados."); Utils.pausar(scanner); return; }

        System.out.println("\n  Cursos disponíveis:");
        for (int i = 0; i < cursos.size(); i++) {
            Curso c = cursos.get(i);
            System.out.println("  " + (i + 1) + ". " + c.getNomeCurso() + " | " + c.getDepartamento().getNome() + " [" + c.getEstado() + "]");
        }

        int escolha = Utils.lerInteiro("  Selecione o curso (0 para voltar): ", scanner);
        if (escolha == 0) return;
        if (escolha < 1 || escolha > cursos.size()) {
            System.out.println("  [!] Opção inválida."); Utils.pausar(scanner); return;
        }
        Curso curso = cursos.get(escolha - 1);

        inscricaoController.inscreverEstudante(e, curso, LocalDate.now().getYear());

        estudanteController.guardarEstadoEstudante(e);
        System.out.println("  [✓] Estudante inscrito em '" + curso.getNomeCurso() + "' com sucesso.");
        System.out.println("  Propina de " + String.format("%.2f €", curso.getValorPropina())
                + " criada. O pagamento fica disponível após o curso ser iniciado.");
        Utils.pausar(scanner);
    }

    private void desinscrever() {
        Utils.tituloPagina("INSCRIÇÕES", "Desinscrever de Curso");
        Estudante e = selecionarEstudante();
        if (e == null) return;

        Inscricao inscricao = inscricaoController.obterInscricaoAtual(e);
        if (inscricao == null) {
            System.out.println("  [!] Este estudante não está inscrito em nenhum curso.");
            Utils.pausar(scanner); return;
        }

        String nomeCurso = inscricao.getCurso() != null ? inscricao.getCurso().getNomeCurso() : "desconhecido";
        System.out.println("\n  Estudante : " + e.getNome() + " (" + e.getNumMecanografico() + ")");
        System.out.println("  Curso     : " + nomeCurso);
        System.out.println("  Ano       : " + inscricao.getAnoDeCurso());

        if (!Utils.confirmar("Desinscrever '" + e.getNome() + "' do curso '" + nomeCurso + "'?", scanner)) {
            System.out.println("  Operação cancelada."); Utils.pausar(scanner); return;
        }

        inscricaoController.desinscreverEstudante(e);
        System.out.println("  [✓] Estudante desinscrido do curso com sucesso.");
        Utils.pausar(scanner);
    }

    private void verificarProgressao() {
        Utils.tituloPagina("INSCRIÇÕES", "Verificar Progressão");
        Estudante e = selecionarEstudante();
        if (e == null) return;

        System.out.println("\n  " + e.getNome() + " | Ano " + e.getAnoAtual() + " | " + e.getEstado());
        System.out.println("  Curso: " + (e.getCursoAtual() != null ? e.getCursoAtual().getNomeCurso() : "(sem curso)"));

        try {
            inscricaoController.verificarProgressaoAno(e);
            System.out.println("\n  [✓] Cumpre todos os requisitos para progredir para o ano seguinte.");
        } catch (IllegalArgumentException ex) {
            System.out.println("\n  [!] Não pode progredir: " + ex.getMessage());
        }
        Utils.pausar(scanner);
    }

    private void marcarPropinaPaga() {
        Utils.tituloPagina("PROPINAS", "Marcar Propina como Paga");
        Estudante e = selecionarEstudante();
        if (e == null) return;

        estudanteController.marcarPropinaAtualComoPaga(e.getNumMecanografico());

        System.out.println("  [✓] Propina atual marcada como paga.");
        Utils.pausar(scanner);
    }

    private void listarTodasAsPropinas() {
        Utils.tituloPagina("PROPINAS", "Todas as Propinas");

        ArrayList<Estudante> todos = estudanteController.listarEstudante();
        if (todos.isEmpty()) {
            System.out.println("  (sem estudantes registados)");
            Utils.pausar(scanner);
            return;
        }

        String cabecalho = String.format("  %-12s %-22s %-8s %-10s %-10s %s",
                "Nº Mecano.", "Nome", "Ano", "Total", "Pago", "Estado");
        System.out.println("  " + "─".repeat(72));
        System.out.println(cabecalho);
        System.out.println("  " + "─".repeat(72));

        double totalGlobal = 0;
        double totalPago = 0;
        int semInscricao = 0;
        for (Estudante e : todos) {
            Inscricao inscricaoAtual = estudanteController.obterInscricaoAtual(e);
            if (inscricaoAtual == null || inscricaoAtual.getPropina() == null) {
                System.out.printf("  %-12s %-22s %-8s %-10s %-10s %s%n",
                        e.getNumMecanografico(), e.getNome(), "-", "-", "-", "Sem inscrição");
                semInscricao++;
                continue;
            }
            Propina p = inscricaoAtual.getPropina();
            String estado;
            if (p.isTotalmentePaga()) {
                estado = "PAGA";
            } else if (p.getValorPago() > 0) {
                estado = "PARCIAL";
            } else {
                estado = "POR PAGAR";
            }
            System.out.printf("  %-12s %-22s %-8d %8.2f€ %8.2f€ %s%n",
                    e.getNumMecanografico(),
                    e.getNome(),
                    e.getAnoAtual(),
                    p.getValorTotal(),
                    p.getValorPago(),
                    estado);
            totalGlobal += p.getValorTotal();
            totalPago   += p.getValorPago();
        }
        double totalEmDivida = totalGlobal - totalPago;
        System.out.println("  " + "─".repeat(72));
        System.out.printf("  %-44s %8.2f€ %8.2f€ %s%n",
                "TOTAL (" + (todos.size() - semInscricao) + " aluno(s) com inscrição)",
                totalGlobal, totalPago,
                String.format("%.2f€ em dívida", totalEmDivida));
        Utils.pausar(scanner);
    }

    private void listarPropinasEmDivida() {
        Utils.tituloPagina("PROPINAS", "Propinas em Dívida");

        ArrayList<Estudante> estudantes = estudanteController.listarComPropinaEmDivida();

        if (estudantes.isEmpty()) {
            System.out.println("  (sem dívidas registadas)");
            Utils.pausar(scanner);
            return;
        }

        String cabecalho = String.format("  %-12s %-22s %-20s %-10s %-10s %s",
                "Nº Mecano.", "Nome", "Curso", "Total", "Pago", "Em Dívida");
        System.out.println("  " + "─".repeat(82));
        System.out.println(cabecalho);
        System.out.println("  " + "─".repeat(82));

        for (Estudante e : estudantes) {
            Inscricao inscricaoAtual = estudanteController.obterInscricaoAtual(e);
            String nomeCurso = (inscricaoAtual != null && inscricaoAtual.getCurso() != null)
                    ? inscricaoAtual.getCurso().getNomeCurso() : "sem curso";
            Propina p = (inscricaoAtual != null) ? inscricaoAtual.getPropina() : null;

            if (p != null) {
                System.out.printf("  %-12s %-22s %-20s %8.2f€ %8.2f€ %8.2f€%n",
                        e.getNumMecanografico(),
                        e.getNome(),
                        nomeCurso,
                        p.getValorTotal(),
                        p.getValorPago(),
                        p.getSaldoEmDebito());
            } else {
                System.out.printf("  %-12s %-22s %-20s%n",
                        e.getNumMecanografico(), e.getNome(), nomeCurso);
            }
        }
        System.out.println("  " + "─".repeat(82));
        Utils.pausar(scanner);
    }

    private void menuPropinas() {
        String[] opcoesPropinas = {
                "Listar Todas as Propinas",
                "Listar Estudantes com Propina em Dívida",
                "Marcar Propina Atual como Paga"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("PROPINAS", opcoesPropinas, scanner);
            try {
                switch (opcao) {
                    case 1: listarTodasAsPropinas(); break;
                    case 2: listarPropinasEmDivida(); break;
                    case 3: marcarPropinaPaga(); break;
                    case 0: break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }
}