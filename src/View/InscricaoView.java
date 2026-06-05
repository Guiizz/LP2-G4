package View;

import Controller.InscricaoController;
import Model.Estudante;
import Model.Inscricao;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Scanner;

public class InscricaoView {

    private final InscricaoController inscricaoController;
    private final Scanner scanner;

    public InscricaoView(InscricaoController inscricaoController, Scanner scanner) {
        this.inscricaoController = inscricaoController;
        this.scanner = scanner;
    }

    public void iniciar(Estudante estudante) {
        String[] opcoes = {
                "Ver Inscrição Atual",
                "Ver Histórico de Inscrições",
                "Verificar Progressão de Ano"
        };

        int opcao;
        do {
            Utils.limparEcra();
            opcao = Utils.mostrarMenu("INSCRIÇÕES [" + estudante.getEmail() + "]", opcoes, scanner);
            try {
                switch (opcao) {
                    case 1: verInscricaoAtual(estudante); break;
                    case 2: verHistorico(estudante); break;
                    case 3: verificarProgressao(estudante); break;
                    case 0: System.out.println("  A voltar..."); break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
                Utils.pausar(scanner);
            }
        } while (opcao != 0);
    }

    private void verInscricaoAtual(Estudante estudante) {
        Utils.limparEcra();
        Utils.tituloPagina("Inscrição Atual");
        Inscricao atual = inscricaoController.obterInscricaoAtual(estudante);
        if (atual == null) {
            System.out.println("  (sem inscrição ativa)");
        } else {
            System.out.println(atual);
        }
        Utils.pausar(scanner);
    }

    private void verHistorico(Estudante estudante) {
        Utils.limparEcra();
        Utils.tituloPagina("Histórico de Inscrições");
        ArrayList<Inscricao> inscricoes = inscricaoController.listarInscricoes(estudante);
        if (inscricoes.isEmpty()) {
            System.out.println("  (sem inscrições registadas)");
            Utils.pausar(scanner);
            return;
        }
        for (Inscricao i : inscricoes) {
            System.out.println(i + "\n");
        }
        Utils.pausar(scanner);
    }

    private void verificarProgressao(Estudante estudante) {
        Utils.limparEcra();
        Utils.tituloPagina("Verificar Progressão de Ano");
        try {
            inscricaoController.verificarProgressaoAno(estudante);
            System.out.println("  [✓] Cumpre os requisitos para progredir para o ano seguinte.");
        } catch (IllegalArgumentException e) {
            System.out.println("  [!] " + e.getMessage());
        }
        Utils.pausar(scanner);
    }
}