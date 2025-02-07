package szlicht.daniel.calendar.common;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${meeting.mail.bot}")
    private String fromEmail;
    @Value("${meeting.mail.owner}")
    private String testMail;
    private final JavaMailSender sender;

    public EmailService(JavaMailSender sender) {
        this.sender = sender;
    }

//    @PostConstruct
    public void testSend() {
        sendSimpleEmail(testMail,
                "zobaczmy teraz",
                "Siema Dixu, działam");
    }

    @Async
    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromEmail);
        message.setSubject(subject);
        message.setText(body);
        sender.send(message);
    }

    @Async
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setFrom(fromEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            sender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
