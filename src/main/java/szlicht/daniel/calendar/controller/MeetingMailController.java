package szlicht.daniel.calendar.controller;

import jakarta.mail.MessagingException;
import org.eclipse.angus.mail.imap.IMAPMessage;
import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.common.mail.MailResponder;
import szlicht.daniel.calendar.meeting.Logger;
import szlicht.daniel.calendar.dialog.DialogAppService;
import szlicht.daniel.calendar.dialog.RawEmail;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static szlicht.daniel.calendar.common.mail.MailUtils.extractTextFromMessage;

@Component
class MeetingMailController implements MailResponder {

    private final DialogAppService dialogAppService;
    private final Logger logger;

    MeetingMailController(DialogAppService dialogAppService, Logger logger) {
        this.dialogAppService = dialogAppService;
        this.logger = logger;
    }

    @Override
    public void respondToMail(IMAPMessage message) {
        try {
            System.out.println("New mail received! "
                    + message.getSubject() + " email: "
                    + message.getSender());
            List<String> elements = extractSenderNameAndEmail(message.getSender().toString());
            String name = elements.get(0);
            String email = elements.get(1);
            String subject = message.getSubject();
            dialogAppService.processNewEmail(new RawEmail(email,name,subject,extractTextFromMessage(message)));
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }catch (IllegalArgumentException e){
            logger.notifyOwner("Tried to arrange but parsing failed", e.getMessage(),false );
        }
    }

    private List<String> extractSenderNameAndEmail(String input) {
        Pattern pattern = Pattern.compile("^(.*?)\\s*<(.*?)>$");
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            String name = matcher.group(1).strip();
            String email = matcher.group(2).strip();
            return List.of(name, email);
        }
        throw new IllegalArgumentException("Input format is invalid. Expected format: 'Name Surname <email@example.com>', but got:" + input);
    }
}
