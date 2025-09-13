package cat.itacademy.s05.t02.n01.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MailService {

    private final Optional<JavaMailSender> mailSender;
    private final String from;

    public MailService(Optional<JavaMailSender> mailSender,
                       @Value("${app.mail.from:no-reply@example.com}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void send(String to, String subject, String text) {
        try {
            if (mailSender.isEmpty()) {
                System.err.println("[MailService] SMTP no configurado. Email simulado a " + to + ": " + subject);
                return;
            }
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.get().send(msg);
        } catch (Exception e) {
            System.err.println("[MailService] No se pudo enviar el email: " + e.getMessage());
        }
    }
}
