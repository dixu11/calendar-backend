package szlicht.daniel.calendar.presenter;

import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.common.mail.EmailService;
import szlicht.daniel.calendar.dialog.DialogView;
import szlicht.daniel.calendar.dialog.DialogPresenter;
import szlicht.daniel.calendar.dialog.EmailData;

@Component
public class EmailDialogPresenter implements DialogPresenter {
    private EmailService emailService;

    public EmailDialogPresenter(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void showDialog(DialogView dialogView, EmailData emailData) {
        HtmlDialog htmlDialog = new HtmlDialog(dialogView);
        emailService.sendHtmlEmail(emailData.getEmail(), htmlDialog.getSubject(), htmlDialog.getHtml());
    }
}
