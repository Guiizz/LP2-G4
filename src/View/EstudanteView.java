package View;

import Controller.EstudanteController;
import Model.*;
import Utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
            System.out.println("  (Sem inscrições registadas...)");
            return;
        }

        for (Inscricao inscricao : inscricoes) {
            System.out.println("\n  Ano letivo: " + inscricao.getAnoDeCurso() + "/" + (inscricao.getAnoLetivo() + 1));
            System.out.println(" Ano de Curso: " + inscricao.getAnoDeCurso() + "º Ano");
            System.out.println(" Curso: " + inscricao.getCurso().getNomeCurso());
            System.out.println("  ─────────────────────────────────────");

            ArrayList<Avaliacao> avaliacoes = inscricao.getAvaliacoes();

            if (avaliacoes.isEmpty()) {
                System.out.println("    (Sem momentos de avaliação registados)");
                continue;
            }
            java.util.LinkedHashMap<String, double[]> totaisPorUC = new LinkedHashMap<>();
            java.util.LinkedHashMap<String, Boolean> pendentePorUC = new java.util.LinkedHashMap<>();
            System.out.printf("    %-20s %-10s %-8s %-10s%n",
                    "UC", "Data", "Peso", "Nota");
            System.out.println("    ──────────────────────────────────────────");
            for (Avaliacao av : avaliacoes) {
                for(UnidadeCurricular uc : av.getUc()){
                    String nomeUC = uc.getNome();
                    String notaStr = av.getNotaFormatada();
                    System.out.printf("    %-20s %-10s %-7.0f%% %s%n",
                         nomeUC,
                         av.getDataFormatada(),
                         av.getPeso(),
                         notaStr);
                    totaisPorUC.putIfAbsent(nomeUC, new double[]{0,0});
                    pendentePorUC.putIfAbsent(nomeUC, false);

                    if (!av.isLancada()){
                        pendentePorUC.put(nomeUC, true);
                    }else {
                     totaisPorUC.get(nomeUC)[0] += av.getNota() * av.getPeso();
                     totaisPorUC.get(nomeUC)[1] += av.getPeso();
                    }
                }
            }
            System.out.println("    ──────────────────────────────────────────");
            System.out.println("    NOTA FINAL POR UC:");
            for (String nomeUC : totaisPorUC.keySet()) {
                if (pendentePorUC.get(nomeUC)){
                    System.out.println(" " + nomeUC + ": Pendente");
                }else {
                    double[] totais = totaisPorUC.get(nomeUC);
                    double notaFinal = totais[1] > 0 ? totais[0] / totais[1] : 0;
                    String resultado = notaFinal >= 10 ? "Aprovado" : "Reprovado";
                    System.out.printf("      %-20s %.1f valores — %s%n",
                            nomeUC + ":", notaFinal, resultado);
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
