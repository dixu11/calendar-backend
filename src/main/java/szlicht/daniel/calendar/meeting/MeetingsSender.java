package szlicht.daniel.calendar.meeting;

import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.EmailService;

import java.time.LocalDate;
import java.time.format.TextStyle;
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

    public void sendPropositions(Map<LocalDate, Meeting> meetings, String to) {
        String body = formatBody(meetings);
        emailService.sendHtmlEmail(to, "Mentoring z Daniel Szlicht - proponowane terminy", body);
    }

    private String formatBody(Map<LocalDate, Meeting> meetings) {
        String result = "Wybierz dogodny termin: <br><br>";
        result += "Ten tydzień:\n";
        Map<LocalDate, Meeting> firstWeekMeetings = getBefore(meetings, nextMonday(LocalDate.now()));
        result += formatPropositions(firstWeekMeetings);
        result += "\n";
        result += "Kolejne tygodnie:\n";
        Map<LocalDate, Meeting> followingWeeksMeetings = getMinimum(meetings, nextMonday(LocalDate.now()));
        result += formatPropositions(followingWeeksMeetings);
        return result;
    }

    private Map<LocalDate, Meeting> getBefore(Map<LocalDate, Meeting> meetings, LocalDate beforeDate) {
      return   meetings.entrySet().stream()
                .filter(entry -> entry.getKey().isBefore(beforeDate))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<LocalDate,Meeting> getMinimum(Map<LocalDate, Meeting> meetings, LocalDate minimumDate){
       return meetings.entrySet().stream()
                .filter(entry -> entry.getKey().isAfter(minimumDate)|| entry.getKey().isEqual(minimumDate))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String formatPropositions(Map<LocalDate, Meeting> meetings){
        StringBuilder propositions = new StringBuilder();
        propositions.append("<pre>");
        for (LocalDate date : meetings.keySet()) {
            Meeting meeting = meetings.get(date);
            String weekDay = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("pl-PL"));
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
