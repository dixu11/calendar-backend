package szlicht.daniel.calendar.meeting;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;
import org.eclipse.angus.mail.imap.IMAPMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.common.MailResponder;
import szlicht.daniel.calendar.meeting.core.CalendarFacade;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static szlicht.daniel.calendar.common.MailUtils.extractTextFromMessage;

@Component
class MeetingMailController implements MailResponder {

    @Value("${meeting.keywords.propositions}")
    private List<String> PROPOSITIONS_KEYWORDS;
    @Value("${meeting.keywords.arrange}")
    private List<String> ARRANGE_KEYWORDS;

    private final CalendarFacade facade;

    MeetingMailController(CalendarFacade facade) {
        this.facade = facade;
    }

    @Override
    public void respondToMail(IMAPMessage message) {
        try {
            System.out.println("New mail received! "
                    + message.getSubject() + " from: "
                    + message.getSender() +
                    " content: " + message.getContent());
            String subject = message.getSubject();
            if (containsAnyOf(subject, PROPOSITIONS_KEYWORDS)) {
                sendPropositions(message);
            } else if (containsAnyOf(subject, ARRANGE_KEYWORDS)) {
                processProposition(message);
            } else {
                System.out.println(String.format("(%s)%s don't mach to any patter so it's probably spam -> ignore\n",
                        message.getSender(), subject));
            }
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendPropositions(IMAPMessage message) throws MessagingException {
        Integer optionalMinutes = extractMinutes(message.getSubject()); //todo extract all this

        facade.sendPropositions(optionalMinutes, message.getSender().toString());
    }

    private Integer extractMinutes(String subject) {
        try {
            Pattern findingNumberPattern = Pattern.compile("(\\d+(?:[.,]\\d+)?)");
            Matcher matcher = findingNumberPattern.matcher(subject);
            if (!matcher.find()) {
                return null;
            }
            String foundNumber = matcher.group(1);
            double value = Double.parseDouble(foundNumber.replace(",", "."));
            return (int) (value * 60);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    private void processProposition(IMAPMessage message) {
        try {
            String content = extractTextFromMessage(message);
            if (content == null || content.isBlank()) {
                System.err.println("Empty or unsupported email body format.");
                return;
            }

            List<String> firstLines = Arrays.stream(content.split("\n"))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .limit(2)
                    .toList();

            System.out.println("First 3 lines from email body:");
            firstLines.forEach(System.out::println);
            LocalDateTime start = LocalDateTime.parse(firstLines.get(0).split("#")[1]);
            int minutes = Integer.parseInt(firstLines.get(1).split("#")[1]);
            facade.arrangeMeeting(start, minutes);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            System.err.println("Cannot process body");
        }
    }

    private boolean containsAnyOf(String content, List<String> keywords) {
        return keywords.stream()
                .anyMatch(keyword -> content.toLowerCase().contains(keyword));
    }
}
