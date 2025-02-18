package szlicht.daniel.calendar.common.spring;

import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.mail.EmailService;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

@Service
class EmailWarningLogger implements WarningLogger {

    private final EmailService emailService;

    public EmailWarningLogger(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void notifyOwner(String subject, String message, boolean critical) {
        if (critical) {
            subject = subject.toUpperCase();
            message = message.toUpperCase();
        }
        emailService.sendSimpleEmail(params.mail().owner(), subject, message);
        if (message.length() > 100) {
            message = message.substring(0, 100) + "...";
        }
        System.err.printf("%s - %s - notification mail send to owner\n", subject, message);
    }
}
