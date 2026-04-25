package Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {

    private static final String PREFIXO_HASH = "SHA256:";

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(PREFIXO_HASH);
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo SHA-256 não disponível.", e);
        }
    }

    public static boolean verificarPassword(String passwordTextoLimpo, String passwordArmazenada) {
        if (passwordArmazenada == null || passwordTextoLimpo == null) return false;
        if (estaHasheada(passwordArmazenada)) {
            return hashPassword(passwordTextoLimpo).equals(passwordArmazenada);
        }
        return passwordTextoLimpo.equals(passwordArmazenada); // fallback legado
    }

    public static boolean estaHasheada(String password) {
        return password != null && password.startsWith(PREFIXO_HASH);
    }
}