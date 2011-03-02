package org.bitcoma.hearts.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
    private static final String MAIL_SERVER = "mymail.server.org";
    private static final String MAIL_FROM = "bitcoma@gmail.com";

    public static void sendPlainText(String[] recipients, String subject,
            String body) throws MessagingException {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", MAIL_SERVER);

        Session mailSession = Session.getDefaultInstance(props, null);
        Transport transport = mailSession.getTransport();

        MimeMessage message = new MimeMessage(mailSession);
        message.setSubject(subject);
        message.setContent(body, "text/plain");

        // Set from address
        InternetAddress addressFrom = new InternetAddress(MAIL_FROM);
        message.setFrom(addressFrom);

        for (int i = 0; i < recipients.length; i++)
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                    recipients[i]));

        transport.connect();
        transport.sendMessage(message,
                message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

}
