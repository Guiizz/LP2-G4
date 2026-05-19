package View;

import Controller.EstudanteController;
import Model.Estudante;
import Model.Inscricao;
import Model.Avaliacao;
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
                "Atualizar a minha Morada"
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
        ArrayList<Avaliacao> ucsEmAtraso = estudante.getUCsEmAtraso();
        if (ucsEmAtraso != null && !ucsEmAtraso.isEmpty()) {
            System.out.println("\n--- UCs em Atraso ---");
            for (Avaliacao av : ucsEmAtraso) {
                String nomeUC = (av.getUc() != null && !av.getUc().isEmpty())
                        ? av.getUc().get(0).getNome()
                        : "UC desconhecida";
                System.out.println("  [!] " + nomeUC + " — Nota: " + av.getNotaFormatada());
            }
            System.out.println("  Total em atraso: " + ucsEmAtraso.size());
        } else {
            System.out.println("\n  (sem UCs em atraso)");
        }
        Utils.pausar(scanner);
    }

    private void atualizar(Estudante estudante) {
        System.out.println("\n--- Atualizar a minha Morada --- (0 para cancelar)");
        System.out.println("  Nome, nº mecanográfico, email, NIF e data de nascimento não são editáveis.");
        System.out.println("  Morada atual: " + estudante.getMorada());

        String novaMorada = Utils.lerCampo("Nova morada: ", scanner);
        estudanteController.atualizarMoradaPropria(estudante, novaMorada);

        System.out.println("  [✓] Morada atualizada com sucesso.");
        Utils.pausar(scanner);
    }
}