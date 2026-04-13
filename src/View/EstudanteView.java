package View;

import Controller.EstudanteController;
import Model.Avaliacao;
import Model.Curso;
import Model.Estudante;
import Model.Inscricao;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * View da área pessoal do Estudante.
 * Apenas acessível após autenticação via LoginView.
 */
public class EstudanteView {

   private EstudanteController control;
   private Scanner scanner;

    public EstudanteView(EstudanteController control, Scanner scanner) {
        this.control = control;
        this.scanner = scanner;
    }


    /**
     * Menu de entrada da área do estudante.
     * @param estudante O estudante autenticado.
     */
    public void iniciar(Estudante estudante) {
        String[] opcoes = {
                "Ver a minha Ficha de Estudante",
                "Ver as minhas Notas",
                "Verificar Progressão de Ano",
                "Avançar para o Proximo Ano"
        };

        int opcao;
        do{
            opcao = Utils.mostrarMenu("ÁREA DO ESTUDANTE [" + estudante.getNumMecanografico() + "]", opcoes, scanner);
            switch (opcao){
                case 1: verFichaEstudante(estudante); break;
                case 2: verNotasEstudante(estudante); break;
                case 3: verificarProgressaoAno(estudante); break;
                case 4: passarDeAno(estudante); break;
                case 0: System.out.printf(" A encerrar sessão..."); break;
            }
        }while (opcao != 0);
    }

    // ── Ações ────────────────────────────────────────────────────────────────

    private void verFichaEstudante(Estudante estudante) {
        System.out.println("\n" + estudante);
    }

    private void verNotasEstudante(Estudante estudante) {
        System.out.println("\n--- As minhas Notas ---");
        ArrayList<Inscricao> inscricoes = estudante.getInscricoes();

        if (inscricoes.isEmpty()) {
            System.out.println("  (sem inscrições registadas)");
            return;
        }

        for (Inscricao inscricao : inscricoes) {
            System.out.println("  Ano letivo: " + inscricao.getAnoDeCurso());
            ArrayList<Avaliacao> avaliacoes = inscricao.getAvaliacoes();
            if (avaliacoes.isEmpty()) {
                System.out.println("    (sem avaliações lançadas)");
            } else {
                for (Avaliacao av : avaliacoes) {
                    System.out.println("    - " + av);
                }
            }
        }
    }

    private void verificarProgressaoAno(Estudante estudante) {
        System.out.println("\n--- Verificar Progressão de Ano ---");
        try {
            control.verificarProgressaoAno(estudante);
            System.out.println("  [✓] Parabéns! Cumpre os requisitos para progredir para o ano "
                    + (estudante.getAnoAtual() + 1) + ".");
        } catch (IllegalArgumentException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }

    private void passarDeAno(Estudante estudante){
        System.out.println("\n--- Avançar para o Próximo Ano ---");
        try{
            control.verificarProgressaoAno(estudante);
            ArrayList<Inscricao> inscricaos = estudante.getInscricoes();
            if (inscricaos.isEmpty()){
                System.out.println(" [!] Não tem inscrições registadas.");
                return;
            }
            Curso curso = inscricaos.get(inscricaos.size() - 1).getCurso();

            int novoAno = estudante.getAnoAtual() + 1;
            int anoLetivo = LocalDate.now().getYear();
            Inscricao novaInscrição = new Inscricao(anoLetivo, novoAno, curso);

            control.passarAno(estudante, novaInscrição);
            
            System.out.println("  [✓] Avançou com sucesso para o ano " + novoAno + "!");
            System.out.println("      Ano letivo: " + anoLetivo + "/" + (anoLetivo + 1));
        } catch (IllegalArgumentException e) {
            System.out.println(" [!] " + e.getMessage());
        }
    }
}
