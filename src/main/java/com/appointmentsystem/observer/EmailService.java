package com.appointmentsystem.observer;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Represents an Email Notification Service.
 * Implements the Observer interface to receive notifications.
 * Sends real emails using SMTP and secure credentials via .env file.
 *
 * @author Elham
 * @version 1.1
 */
public class EmailService implements Observer {

    /** Logger for this class. */
    private static final Logger logger = Logger.getLogger(EmailService.class.getName());

    /** The sender's email address loaded from .env. */
    private String senderEmail;

    /** The sender's app password loaded from .env. */
    private String senderPassword;

    /**
     * Constructs a new EmailService.
     * Loads SMTP credentials securely from the .env file.
     */
    public EmailService() {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            this.senderEmail = dotenv.get("EMAIL_ADDRESS");
            this.senderPassword = dotenv.get("EMAIL_PASSWORD");

            logger.info("EmailService initialized. Sender: " + (senderEmail != null ? "Loaded" : "NULL"));
        } catch (Exception e) {
            logger.severe("Error loading .env file: " + e.getMessage());
        }
    }

    /**
     * Receives a message and sends it as a real email.
     * Expects the message format to start with the recipient's email.
     *
     * @param message the message containing receiver email and body
     */
    @Override
    public void update(String message) {
        if (senderEmail != null && senderPassword != null && message != null && !message.trim().isEmpty()) {
            String[] parts = message.split(" ", 2);
            String toEmail = parts[0];
            String body = (parts.length > 1) ? parts[1] : message;

            sendEmail(toEmail, "Appointment System Notification", body);
        } else {
            logger.info("Email credentials not configured or message empty.");
        }
    }

    /**
     * Sends an email using the configured SMTP server.
     *
     * @param toEmail the recipient's email address
     * @param subject the email subject
     * @param body    the email body text
     */
    private void sendEmail(String toEmail, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", true);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        if (session == null) {
            logger.severe("Failed to create email session.");
            return;
        }

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            logger.info("Real Email sent successfully to " + toEmail);
        } catch (MessagingException e) {
            logger.severe("Failed to send real email: " + e.getMessage());
        }
    }
}