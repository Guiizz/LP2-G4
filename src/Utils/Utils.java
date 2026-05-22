package Utils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Classe com métodos utilitários reutilizáveis por todas as camadas.
 */
public class Utils {

    // ── Validações ────────────────────────────────────────────────────────────

    public static void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
        if (nome.trim().split("\\s+").length < 2) {
            throw new IllegalArgumentException("O nome deve conter pelo menos nome e apelido. ");
        }
    }

    public static void validarNif(String nif) {
        if (nif == null || nif.trim().isEmpty()) {
            throw new IllegalArgumentException("O NIF não pode estar vazio.");
        }
        if (nif.length() != 9) {
            throw new IllegalArgumentException("NIF inválido. Deve conter exatamente 9 dígitos.");
        }
        for (char c : nif.toCharArray()) {
            if(!Character.isDigit(c)) {
                throw new IllegalArgumentException("NIF inválido. Deve conter apenas digitos.");
            }
        }
    }

    public static void validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalArgumentException("Data de nascimento não pode ser nula.");
        }
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento não pode ser no futuro.");
        }
        if (dataNascimento.isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("O utilizador deve ter pelo menos 18 anos.");
        }
    }

    public static void validarMorada(String morada) {
        if (morada == null || morada.trim().isEmpty()) {
            throw new IllegalArgumentException("A morada não pode ser vazia.");
        }
        if (morada.trim().length() < 5) {
            throw new IllegalArgumentException("A morada deve ter pelo menos 5 caracteres.");
        }
    }

    public static void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("O email não pode ser vazio.");
        }
        if (!email.endsWith("@issmf.pt")) {
            throw new IllegalArgumentException("Email inválido. Deve ter o formato @issmf.pt.");
        }
    }

    public static void validarNota(double nota) {
        if (nota < 0 || nota > 20) {
            throw new IllegalArgumentException("Nota inválida. Deve estar entre 0 e 20.");
        }
    }

    public static void validarPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("A palavra-passe não pode ser vazia.");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("A palavra-passe deve ter no mínimo 8 caracteres.");
        }
        boolean temLetra = false;
        boolean temNumero = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) temLetra = true;
            if (Character.isDigit(c)) temNumero = true;
        }
        if (!temLetra) {
            throw new IllegalArgumentException("A palavra-passe deve conter pelo menos uma letra.");
        }
        if (!temNumero) {
            throw new IllegalArgumentException("A palavra-passe deve conter pelo menos um número.");
        }
    }

    public static void validarSigla(String sigla) {
        if (sigla == null || !sigla.matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Sigla inválida. Deve conter exatamente 3 letras.");
        }
    }

    // ── Leitura ──────────────────────────────────────────────────────

    /**
     * Lê um campo de texto. Se o utilizador escrever "0", cancela o registo.
     */
    public static String lerCampo(String mensagem, Scanner scanner) {
        System.out.print(mensagem);
        String valor = scanner.nextLine().trim();
        if (valor.equals("0")) {
            throw new IllegalArgumentException("Registo cancelado pelo utilizador.");
        }
        return valor;
    }
    public static String lerNome(String mensagem, Scanner scanner) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();
            if (valor.equals("0")) throw new IllegalArgumentException("Registo cancelado pelo utilizador.");
            try {
                validarNome(valor);
                return valor;
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }

    public static String lerNif(String mensagem, Scanner scanner) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();
            if (valor.equals("0")) throw new IllegalArgumentException("Registo cancelado pelo utilizador.");
            try {
                validarNif(valor);
                return valor;
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }

    public static String lerMorada(String mensagem, Scanner scanner) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();
            if (valor.equals("0")) throw new IllegalArgumentException("Registo cancelado pelo utilizador.");
            try {
                validarMorada(valor);
                return valor;
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }

    public static String lerSigla(String mensagem, Scanner scanner) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();
            if (valor.equals("0")) throw new IllegalArgumentException("Registo cancelado pelo utilizador.");
            try {
                validarSigla(valor);
                return valor;
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }

    public static String lerEmail(String mensagem, Scanner scanner) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();
            if (valor.equals("0")) throw new IllegalArgumentException("Registo cancelado pelo utilizador.");
            try {
                validarEmail(valor);
                return valor;
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }

    public static String lerPassword(String mensagem, Scanner scanner) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();
            if (valor.equals("0")) throw new IllegalArgumentException("Registo cancelado pelo utilizador.");
            try {
                validarPassword(valor);
                return valor;
            } catch (IllegalArgumentException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }

    public static LocalDate lerDataNascimento(String mensagem, Scanner scanner) {
        while (true) {
            try {
                System.out.print(mensagem);
                String input = scanner.nextLine().trim();
                if (input.equals("0")) throw new IllegalArgumentException("Registo cancelado.");
                LocalDate data = LocalDate.parse(input);
                validarDataNascimento(data);
                return data;
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                System.out.println("  [!] Data inválida. Use o formato AAAA-MM-DD.");
            }
        }
    }

    /**
     * Lê um número inteiro. Repete até o utilizador introduzir um valor válido.
     */
    public static int lerInteiro(String mensagem, Scanner scanner) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Valor inválido. Introduza um número inteiro.");
            }
        }
    }

    /**
     * Lê um número decimal. Repete até o utilizador introduzir um valor válido.
     * Aceita vírgula ou ponto como separador decimal.
     */
    public static double lerDouble(String mensagem, Scanner scanner) {
        while (true) {
            try {
                System.out.print(mensagem);
                String input = scanner.nextLine().trim();
                if (input.equals("0")) throw new IllegalArgumentException("Operação cancelada.");
                return Double.parseDouble(input.replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("  [!] Valor inválido. Introduza um número (ex: 9.5).");
            } catch (IllegalArgumentException e) {
                throw e;
            }
        }
    }

    /**
     * Lê uma data no formato AAAA-MM-DD (LocalDate).
     * Se o utilizador escrever "0", cancela o registo.
     */
    public static LocalDate lerData(String mensagem, Scanner scanner) {
        while (true) {
            try {
                System.out.print(mensagem);
                String input = scanner.nextLine().trim();
                if (input.equals("0")) throw new IllegalArgumentException("Registo cancelado.");
                return LocalDate.parse(input);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                System.out.println("  [!] Data inválida. Use o formato AAAA-MM-DD.");
            }
        }
    }

    /**
     * Lê uma data no formato DD/MM/AAAA (java.util.Date — usado em Avaliacao).
     * Se o utilizador escrever "0", cancela o registo.
     */
    public static Date lerDataAvaliacao(String mensagem, Scanner scanner) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        while (true) {
            try {
                System.out.print(mensagem);
                String input = scanner.nextLine().trim();
                if (input.equals("0")) throw new IllegalArgumentException("Operação cancelada.");
                return sdf.parse(input);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (ParseException e) {
                System.out.println("  [!] Data inválida. Use o formato DD/MM/AAAA.");
            }
        }
    }

    // ── Consola ───────────────────────────────────────────────────────────────

    /**
     * Pausa a execução até o utilizador pressionar Enter.
     */
    public static void pausar(Scanner scanner) {
        System.out.print("\n  Pressione Enter para continuar...");
        scanner.nextLine();
    }

    /**
     * Limpa o ecrã da consola imprimindo linhas em branco.
     */
    public static void limparEcra() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    // ── Menu ──────────────────────────────────────────────────────────────────

    public static int mostrarMenu(String titulo, String[] opcoes, Scanner scanner) {
        int larguraMaxima = ("  " + titulo).length();

        for (int i = 0; i < opcoes.length; i++) {
            String linhaOpcao = "  " + (i + 1) + " - " + opcoes[i];
            if (linhaOpcao.length() > larguraMaxima) {
                larguraMaxima = linhaOpcao.length();
            }
        }

        String linhaSair = "  0 - Voltar / Sair";
        if (linhaSair.length() > larguraMaxima) {
            larguraMaxima = linhaSair.length();
        }

        final int LARGURA = Math.max(38, larguraMaxima + 2);

        while (true) {
            System.out.println("\n╔" + "═".repeat(LARGURA) + "╗");
            System.out.println("║" + padDir("  " + titulo, LARGURA) + "║");
            System.out.println("╠" + "═".repeat(LARGURA) + "╣");
            for (int i = 0; i < opcoes.length; i++) {
                System.out.println("║" + padDir("  " + (i + 1) + " - " + opcoes[i], LARGURA) + "║");
            }
            System.out.println("║" + padDir("  0 - Voltar / Sair", LARGURA) + "║");
            System.out.println("╚" + "═".repeat(LARGURA) + "╝");
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

    private static String padDir(String texto, int largura) {
        int visualLen = texto.codePointCount(0, texto.length());
        int espacos = largura - visualLen;
        if (espacos < 0) espacos = 0;
        StringBuilder sb = new StringBuilder(texto);
        for (int i = 0; i < espacos; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public static void criarFicheiroSeNaoExistir(String caminho, String cabecalho) {
        File ficheiro = new File(caminho);
        if (ficheiro.getParentFile() != null) {
            ficheiro.getParentFile().mkdirs();
        }
        if (!ficheiro.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ficheiro))) {
                pw.println(cabecalho);
            } catch (IOException e) {
                System.err.println("Erro ao criar ficheiro CSV (" + caminho + "): " + e.getMessage());
            }
        }
    }

    public static List<String[]> lerLinhasCSV(String caminho, String separador) {
        List<String[]> linhas = new ArrayList<>();
        File ficheiro = new File(caminho);
        if (!ficheiro.exists()) return linhas;

        try (BufferedReader br = new BufferedReader(new FileReader(ficheiro))) {
            String linha;
            boolean primeiraLinha = true;
            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) { primeiraLinha = false; continue; }
                if (linha.trim().isEmpty()) continue;
                linhas.add(linha.split(separador, -1));
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler ficheiro CSV (" + caminho + "): " + e.getMessage());
        }
        return linhas;
    }

    public static PrintWriter abrirEscritorCSV(String caminho, String cabecalho) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(caminho));
        pw.println(cabecalho);
        return pw;
    }
}