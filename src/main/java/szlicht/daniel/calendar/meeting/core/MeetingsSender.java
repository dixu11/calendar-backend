package szlicht.daniel.calendar.meeting.core;

import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.EmailService;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static szlicht.daniel.calendar.common.LocalDateUtils.*;

@Service
class MeetingsSender {
    private EmailService emailService;

    MeetingsSender(EmailService emailService) {
        this.emailService = emailService;
    }

    void sendPropositions(Propositions propositions, String to) {
        String body = formatBody(propositions);
        emailService.sendHtmlEmail(to, "Mentoring z Daniel Szlicht - proponowane terminy", body);
    }

    private String formatBody(Propositions propositions) {
        String result = "Wybierz dogodny termin: <br><br>";
        result += "Ten tydzień:";
        List<Meeting> firstWeekMeetings = propositions.getFirstWeek();
        result += formatPropositions(firstWeekMeetings);
        result += "Za tydzień:";
        List<Meeting> nextWeek = propositions.getNextWeek();
        result += formatPropositions(nextWeek);
        result += "Kolejne tygodnie:";
        List<Meeting> followingWeeks = propositions.getAfterNextWeek();
        result += formatPropositions(followingWeeks);
        return result;
    }

    private String formatPropositions(List<Meeting> meetings) {
        StringBuilder propositions = new StringBuilder();
        propositions.append("<pre>");
        for (Meeting meeting : meetings) {
            StringBuilder line = new StringBuilder();
            String weekDay = meeting.getStart()
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("pl-PL"));
            line.append(String.format("%-5s", weekDay))
                    .append(simpleDate(meeting.getStart()))
                    .append("  ")
                    .append(simpleTime(meeting.getStart()))
                    .append(" - ")
                    .append(simpleTime(meeting.getEnd()))
                    .append("\n");
            propositions.append(line);
            System.out.print(line);
        }
        propositions.append("</pre>");
        return propositions.toString();
    }
}
