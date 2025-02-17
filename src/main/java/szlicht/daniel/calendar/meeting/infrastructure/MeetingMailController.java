package szlicht.daniel.calendar.meeting.infrastructure;

import jakarta.mail.MessagingException;
import org.eclipse.angus.mail.imap.IMAPMessage;
import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.common.mail.MailResponder;
import szlicht.daniel.calendar.meeting.appCore.CalendarAppService;
import szlicht.daniel.calendar.meeting.appCore.Meeting;
import szlicht.daniel.calendar.meeting.appCore.MeetingDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static szlicht.daniel.calendar.common.mail.MailUtils.extractTextFromMessage;
import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

@Component
class MeetingMailController implements MailResponder {

    private final CalendarAppService calendarAppService;

    MeetingMailController(CalendarAppService calendarAppService) {
        this.calendarAppService = calendarAppService;
    }

    @Override
    public void respondToMail(IMAPMessage message) {
        try {
            System.out.println("New mail received! "
                    + message.getSubject() + " from: "
                    + message.getSender());
            String subject = message.getSubject();
            if (containsAnyOf(subject, params.keywords().propositions())) {
                sendPropositions(message);
            } else if (containsAnyOf(subject, params.keywords().arrange())) {
                processProposition(message);
            } else {
                System.out.println(String.format("(%s)%s don't mach to any patter so it's probably spam -> ignore\n",
                        message.getSender(), subject));
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendPropositions(IMAPMessage message) throws MessagingException {
        Integer optionalMinutes = extractMinutes(message.getSubject());
        calendarAppService.sendPropositions(optionalMinutes, message.getSender().toString());
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

            List<String> lines = Arrays.stream(content.split("\n"))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .toList();
            LocalDateTime start = LocalDateTime.parse(lines.get(0).split("#")[1]);
            int minutes = Integer.parseInt(lines.get(1).split("#")[1]);
            LocalDateTime end = start.plusMinutes(minutes);
            String mail = message.getSender().toString();
            String description = extractProvidedDescriptions(lines);

            MeetingDto meetingDto = MeetingDto.builder()
                    .start(start)
                    .end(end)
                    .email(mail)
                    .providedDescription(description)
                    .build();
            calendarAppService.arrangeMeeting(meetingDto);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            System.err.println("Cannot process body");
        }
    }

    private String extractProvidedDescriptions(List<String> allLines) {
        StringBuilder result = new StringBuilder();
        boolean startAdding = false;
        for (String line : allLines) {
            if (line.startsWith(params.keywords().description())) {
                startAdding = true;
                line = line.replace(params.keywords().description(), "");
            }
            if (!startAdding || line.isBlank()) {
                continue;
            }
            result.append(line)
                    .append("\n");
        }
        return result.toString();
    }

    private boolean containsAnyOf(String content, List<String> keywords) {
        return keywords.stream()
                .anyMatch(keyword -> content.toLowerCase().contains(keyword));
    }
}
