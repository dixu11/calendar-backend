package szlicht.daniel.calendar.meeting;

import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.mail.EmailService;
import szlicht.daniel.calendar.meeting.core.WarningService;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

@Service
class EmailWarningService implements WarningService {

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
        emailService.sendSimpleEmail(params.mail().owner(), subject, message);
    }
}
