package szlicht.daniel.calendar.meeting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.EmailService;
import szlicht.daniel.calendar.meeting.core.WarningService;

@Service
public class EmailWarningService implements WarningService {
    @Value("${meeting.mail.owner}")
    private String OWNER_MAIL;

    private final EmailService emailService;

    public EmailWarningService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void notifyOwner(String subject, String message, boolean critical) {
        if (critical) {
            subject = subject.toUpperCase();
            message = message.toUpperCase();
        }
        System.err.printf("%s - %s\n", subject, message);
        emailService.sendSimpleEmail(OWNER_MAIL, subject, message);
    }
}
