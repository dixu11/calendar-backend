package szlicht.daniel.calendar.meeting;

import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.mail.EmailService;
import szlicht.daniel.calendar.meeting.core.WarningService;

import static szlicht.daniel.calendar.meeting.Params.OWNER_MAIL;

@Service
public class EmailWarningService implements WarningService {


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
        System.err.printf("%s - %s - notification mail send to owner\n", subject, message);
        emailService.sendSimpleEmail(OWNER_MAIL, subject, message);
    }
}
