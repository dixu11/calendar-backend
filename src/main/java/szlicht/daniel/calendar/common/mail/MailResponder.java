package szlicht.daniel.calendar.common.mail;

import org.eclipse.angus.mail.imap.IMAPMessage;

public interface MailResponder {
    void respondToMail(IMAPMessage message);
}
