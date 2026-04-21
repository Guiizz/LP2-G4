package Utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class ServicoEmail {
    private static final String REMETENTE = "issmfportal@gmail.com";
    private static final String APP_PASSWORD = "tsle msnp efhy idvl";
    private static final String DESTINATARIO = "issmfportal@gmail.com";

    public static void enviarCredenciais(String emailSistema, String passwordTemporaria, String tipoUtilizador){
        Properties props = new Properties();
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            "smtp.gmail.com");
        props.put("mail.smtp.port",            "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMETENTE, APP_PASSWORD);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(REMETENTE));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(DESTINATARIO));
            msg.setSubject("ISSMF - As suas credenciais de acesso");
            msg.setText("Bem-vindo ao portal ISSMF!\n\n"                              +
                        "Foi registado como " + tipoUtilizador + ".\n"              +
                        "As suas credenciais de acesso são:\n"                        +
                        "  Utilizador : " + emailSistema + "\n"                       +
                        "  Password   : " + passwordTemporaria + "\n\n"               +
                        "Por segurança, altere a sua password no primeiro login.\n" +
                        "ISSMF - Sistema de Gestão Académica"
            );
            Transport.send(msg);
            System.out.println(" [✓] E-mail enviado para " + DESTINATARIO);
        } catch (MessagingException | IllegalArgumentException e) {
            System.out.println(" [!] Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}
