package Utils;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Classe com métodos de validação reutilizáveis por todas as BLLs.
 */
public class Utils {

    /**
     * Valida o nome de um utilizador.
     * @param nome O nome a validar.
     * @throws IllegalArgumentException Se o nome for nulo ou vazio.
     */
    public static void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
    }

    /**
     * Valida o NIF de um utilizador.
     * @param nif O NIF a validar.
     * @throws IllegalArgumentException Se o NIF não tiver exatamente 9 dígitos.
     */
    public static void validarNif(String nif) {
        if (nif == null || nif.length() != 9) {
            throw new IllegalArgumentException("NIF inválido. Deve conter exatamente 9 dígitos.");
        }
    }

    /**
     * Valida a data de nascimento de um utilizador.
     * @param dataNascimento A data de nascimento a validar.
     * @throws IllegalArgumentException Se a data for nula ou no futuro.
     */
    public static void validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null || dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento inválida.");
        }
    }

    /**
     * Valida a morada de um utilizador.
     * @param morada A morada a validar.
     * @throws IllegalArgumentException Se a morada for nula ou vazia.
     */
    public static void validarMorada(String morada) {
        if (morada == null || morada.trim().isEmpty()) {
            throw new IllegalArgumentException("A morada não pode ser vazia.");
        }
    }

    /**
     * Valida o formato do email institucional (@issmf.pt).
     * @param email O email a validar.
     * @throws IllegalArgumentException Se o email for nulo, vazio ou com formato inválido.
     */
    public static void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("O email não pode ser vazio.");
        }
        if (!email.endsWith("@issmf.pt")) {
            throw new IllegalArgumentException("Email inválido. Deve ter o formato @issmf.pt.");
        }
    }

    /**
     * Valida se uma nota está dentro do intervalo permitido (0 a 20).
     * @param nota A nota a validar.
     * @throws IllegalArgumentException Se a nota estiver fora do intervalo.
     */
    public static void validarNota(double nota) {
        if (nota < 0 || nota > 20) {
            throw new IllegalArgumentException("Nota inválida. Nota deve ser entre 0 e 20.");
        }
    }

    /**
     * Valida se uma palavra-passe não é nula ou vazia.
     * @param password A palavra-passe a validar.
     * @throws IllegalArgumentException Se a palavra-passe for nula ou vazia.
     */
    public static void validarPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("A palavra-passe não pode ser vazia.");
        }
    }

    // validação das siglas.
    /**
     * Valida a sigla de uma entidade (ex: departamento ou docente).
     * A sigla deve conter exatamente 3 caracteres alfabéticos (letras).
     *
     * @param sigla A sigla a validar.
     * @throws IllegalArgumentException Se a sigla for nula, tiver comprimento diferente de 3
     *                                  ou contiver caracteres que não sejam letras.
     */
    public static void validarSigla(String sigla) {
        if (sigla == null || !sigla.matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Sigla inválida. Deve conter exatamente 3 letras.");
        }
    }


    public static int mostrarMenu(String titulo, String[] opcoes, Scanner scanner) {
        while (true) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.printf( "║  %-36s║%n", titulo);
            System.out.println("╠══════════════════════════════════════╣");
            for (int i = 0; i < opcoes.length; i++) {
                System.out.printf("║  %d - %-33s║%n", i + 1, opcoes[i]);
            }
            System.out.println("║  0 - Voltar / Sair                   ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("  Opção: ");

            String linha = scanner.nextLine().trim();
            try {
                int opcao = Integer.parseInt(linha);
                if (opcao >= 0 && opcao <= opcoes.length) {
                    return opcao;
                }
            } catch (NumberFormatException ignored) {}

            System.out.println("  [!] Opção inválida. Tente novamente.");
        }
    }
}
