package Utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServicoEmail {

    private static final String REMETENTE;
    private static final String APP_PASSWORD;
    private static final String DESTINATARIO;

    static {
        Properties config = new Properties();
        try (InputStream input = new java.io.FileInputStream("src/Resources/email.properties")) {
            config.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar email.properties: " + e.getMessage());
        }
        REMETENTE    = config.getProperty("email.remetente");
        APP_PASSWORD = config.getProperty("email.password");
        DESTINATARIO = config.getProperty("email.destinatario");
    }

    public static void enviarCredenciais(String emailSistema, String passwordTemporaria, String tipoUtilizador) {
        String assunto = "ISSMF - As suas credenciais de acesso";
        String corpo =
                "Bem-vindo ao portal ISSMF!\n\n"
                        + "Foi registado como " + tipoUtilizador + ".\n"
                        + "As suas credenciais de acesso são:\n"
                        + "Utilizador: " + emailSistema + "\n"
                        + "Password: " + passwordTemporaria + "\n\n"
                        + "Por segurança, altere a sua password no primeiro login.\n"
                        + "ISSMF - Sistema de Gestão Académica";

        boolean emailFicticio = !emailSistema.equalsIgnoreCase("gestor@issmf.pt")
                && DESTINATARIO != null;

        enviar(emailSistema, emailFicticio ? DESTINATARIO : null, assunto, corpo);
    }

    public static void enviarPasswordTemporaria(String emailDestino, String passwordTemporaria, String tipoUtilizador) {
        String assunto = "ISSMF - Recuperação de password";
        String corpo =
                "Recuperação de password - Portal ISSMF\n\n"
                        + "Foi solicitada a recuperação da password da sua conta de " + tipoUtilizador + ".\n\n"
                        + "A sua password temporária é:\n"
                        + "  " + passwordTemporaria + "\n\n"
                        + "Por segurança, será pedido que defina uma nova password no próximo login.\n"
                        + "Se não solicitou esta recuperação, contacte o administrador do sistema.\n\n"
                        + "ISSMF - Sistema de Gestão Académica";

        boolean emailFicticio = !emailDestino.equalsIgnoreCase("gestor@issmf.pt")
                && DESTINATARIO != null;

        enviar(emailDestino, emailFicticio ? DESTINATARIO : null, assunto, corpo);
    }

    private static void enviar(String emailDestino, String emailCC, String assunto, String corpo) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMETENTE, APP_PASSWORD);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(REMETENTE));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestino));

            // CC só adicionado se email for fictício
            if (emailCC != null && !emailCC.isBlank()) {
                msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(emailCC));
            }

            msg.setSubject(assunto);
            msg.setText(corpo);
            Transport.send(msg);
            System.out.println("[✓] E-mail enviado para " + emailDestino);
        } catch (MessagingException e) {
            System.out.println("[!] Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}