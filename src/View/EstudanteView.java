package View;

import Controller.EstudanteController;
import Model.Estudante;
import Model.Inscricao;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Scanner;

public class EstudanteView {

    private final EstudanteController estudanteController;
    private final Scanner scanner;

    public EstudanteView(EstudanteController estudanteController, Scanner scanner) {
        this.estudanteController = estudanteController;
        this.scanner = scanner;
    }

    public void iniciar(Estudante estudante) {
        String[] opcoes = {
                "Ver a minha Ficha",
                "Ver as minhas Inscrições",
                "Ver as minhas Avaliações",
                "Atualizar os meus Dados"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("ÁREA DO ESTUDANTE [" + estudante.getEmail() + "]", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: verFicha(estudante);      break;
                    case 2: verInscricoes(estudante); break;
                    case 3: verAvaliacoes(estudante); break;
                    case 4: atualizar(estudante);     break;
                    case 0: System.out.println("  A terminar sessão..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void verFicha(Estudante estudante) {
        Utils.limparEcra();
        System.out.println("\n--- A minha Ficha ---");
        System.out.println(estudante);
        Utils.pausar(scanner);
    }

    private void verInscricoes(Estudante estudante) {
        Utils.limparEcra();
        System.out.println("\n--- As minhas Inscrições ---");
        ArrayList<Inscricao> inscricoes = estudante.getInscricoes();
        if (inscricoes.isEmpty()) {
            System.out.println("  (sem inscrições registadas)");
            Utils.pausar(scanner);
            return;
        }
        for (Inscricao i : inscricoes) { System.out.println(i + "\n"); }
        Utils.pausar(scanner);
    }

    private void verAvaliacoes(Estudante estudante) {
        Utils.limparEcra();
        System.out.println("\n--- As minhas Avaliações ---");
        ArrayList<Inscricao> inscricoes = estudante.getInscricoes();
        if (inscricoes.isEmpty()) {
            System.out.println("  (sem inscrições registadas)");
            Utils.pausar(scanner);
            return;
        }
        for (Inscricao i : inscricoes) {
            System.out.println("  Ano " + i.getAnoDeCurso() + " — " + i.getCurso().getNomeCurso());
            if (i.getAvaliacoes() == null || i.getAvaliacoes().isEmpty()) {
                System.out.println("    (sem avaliações)");
            } else {
                for (Object a : i.getAvaliacoes()) { System.out.println("    " + a); }
            }
        }
        Utils.pausar(scanner);
    }

    private void atualizar(Estudante estudante) {
        System.out.println("\n--- Atualizar os meus Dados --- (0 para cancelar)");
        System.out.println("  Dados atuais: " + estudante.getNome() + " | " + estudante.getMorada());

        String novoNome = Utils.lerCampo("Novo nome (Enter para manter): ", scanner);
        String novaMorada = Utils.lerCampo("Nova morada (Enter para manter): ", scanner);

        String nomeAtualizar = novoNome.isEmpty() ? estudante.getNome() : novoNome;
        String moradaAtualizar = novaMorada.isEmpty() ? estudante.getMorada() : novaMorada;

        estudanteController.atualizarEstudante(estudante.getNumMecanografico(), nomeAtualizar, moradaAtualizar);
        System.out.println("  [✓] Dados atualizados com sucesso.");
        Utils.pausar(scanner);
    }
}