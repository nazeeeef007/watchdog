package com.watchdog.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationHandler implements NotificationHandler {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationHandler.class);

    private final JavaMailSender mailSender;

    // Inject the sender email from application.properties
    @Value("${application.mail.sender-email}")
    private String senderEmail;

    @Autowired
    public EmailNotificationHandler(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendNotification(String destination, String message) throws MailException {
        try {
            log.info("Preparing to send email to {}", destination);
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            // Use the injected senderEmail value
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(destination);
            mailMessage.setSubject("Watchdog Uptime Alert!");
            mailMessage.setText(message);
            mailSender.send(mailMessage);
            log.info("Successfully sent email to {} with message: {}", destination, message);
        } catch (MailException e) {
            log.error("Failed to send email to {}: {}", destination, e.getMessage(), e);
            throw e;
        }
    }
}
