package szlicht.daniel.calendar.meeting;

import jakarta.mail.MessagingException;
import org.eclipse.angus.mail.imap.IMAPMessage;
import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.common.MailResponder;
import szlicht.daniel.calendar.meeting.core.CalendarFacade;

import java.io.IOException;

@Component
class MeetingMailResponder implements MailResponder {

    private final CalendarFacade facade;

    MeetingMailResponder(CalendarFacade facade) {
        this.facade = facade;
    }

    @Override
    public void respondToMail(IMAPMessage message) {
        try {
            System.out.println("New mail received! "
                    + message.getSubject() + " from: "
                    + message.getSender() +
                    " content: " + message.getContent());
            sendPropositions(message);
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendPropositions(IMAPMessage message) throws MessagingException {
        Integer minutes;
        try {
            String subject = message.getSubject();
            minutes = (int) (Double.parseDouble(subject.replace(",", ".")) * 60);
        } catch (NumberFormatException e) {
            minutes = null;
        }
        facade.sendPropositions(minutes, message.getSender().toString());
    }
}
