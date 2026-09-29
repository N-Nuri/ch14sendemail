package murach.util;

import java.util.Properties;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

// Sends mail through Gmail's SMTP server. Requires a Gmail account and an
// "app password" (not the regular account password), supplied via the
// GMAIL_USER / GMAIL_APP_PASSWORD environment variables.
public class MailUtilGmail {
    private static final String GMAIL_USER =
            System.getenv().getOrDefault("GMAIL_USER", "nguyenkhoinguyen051105@gmail.com");
    private static final String GMAIL_APP_PASSWORD = System.getenv().getOrDefault("GMAIL_APP_PASSWORD", "");

    public static void sendMail(String to, String from, String subject, String body, boolean bodyIsHTML)
            throws MessagingException {

        // 1 - get a mail session
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", "smtp.gmail.com");
        props.put("mail.smtps.port", 465);
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.quitwait", "false");
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);

        // 2 - create a message
        Message message = new MimeMessage(session);
        message.setSubject(subject);
        if (bodyIsHTML) {
            message.setContent(body, "text/html");
        } else {
            message.setText(body);
        }

        // 3 - address the message
        Address fromAddress = new InternetAddress(from);
        Address toAddress = new InternetAddress(to);
        message.setFrom(fromAddress);
        message.setRecipient(Message.RecipientType.TO, toAddress);

        // 4 - send the message, authenticating with the Gmail account
        Transport transport = session.getTransport();
        transport.connect(GMAIL_USER, GMAIL_APP_PASSWORD);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
