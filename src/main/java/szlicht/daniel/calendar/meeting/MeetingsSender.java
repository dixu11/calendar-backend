package szlicht.daniel.calendar.meeting;

import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.EmailService;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static szlicht.daniel.calendar.common.LocalDateUtils.*;

@Service
public class MeetingsSender {
    private EmailService emailService;

    public MeetingsSender(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendPropositions(Propositions propositions, String to) {
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

    private String formatPropositions(List<Meeting> meetings){
        StringBuilder propositions = new StringBuilder();
        propositions.append("<pre>");
        for (Meeting meeting : meetings) {
            String weekDay = meeting.getStart()
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("pl-PL"));
            propositions.append(String.format("%-5s", weekDay));
            propositions.append(simpleDate(meeting.getStart()));
            propositions.append("  ");
            propositions.append(simpleTime(meeting.getStart()));
            propositions.append(" - ");
            propositions.append(simpleTime(meeting.getEnd()));
            propositions.append("\n");
        }
        propositions.append("</pre>");
        return propositions.toString();
    }
}
