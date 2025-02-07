package szlicht.daniel.calendar.meeting.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.EmailService;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static szlicht.daniel.calendar.common.LocalDateUtils.*;

@Service
class MeetingsSender {
    @Value("${test.mail}")
    private String myEmail;
    private EmailService emailService;

    MeetingsSender(EmailService emailService) {
        this.emailService = emailService;
    }

    void sendPropositions(Propositions propositions, String to) {
        String body = formatBody(propositions);
        emailService.sendHtmlEmail(to, "Mentoring z Daniel Szlicht - proponowane terminy", body);
    }

    private String formatBody(Propositions propositions) {
        String result = String.format("Wybierz dogodny termin na lekcję trwającą <b>%.1fh</b>", propositions.getHours());
        result += "<br>";
        result += "<br>";
        result += "Ten tydzień:";
        List<Meeting> firstWeekMeetings = propositions.getFirstWeek();
        result += formatPropositions(firstWeekMeetings);
        result += "Za tydzień:";
        List<Meeting> nextWeek = propositions.getNextWeek();
        result += formatPropositions(nextWeek);
        result += "Kolejne tygodnie:";
        List<Meeting> followingWeeks = propositions.getAfterNextWeek();
        result += formatPropositions(followingWeeks);
        result += "Aby wybrać inną dłogość lekcji:";
        result += "<br>";
        result += "Wyślij maila o tytule zawierającym interesującą długość lekcji";
        result += "<br>";
        result += "Dostępne długości lekcji: 1h, 1.5h, 2h, 2.5h, 3h";
        result += "<br>";
        result += String.format("Jeśli żaden z terminów Ci nie podpasował a zależy Ci na spotkaniu " +
                "napisz kiedy jesteś dostępny/a na: %s, poszukamy terminu indywidualnie :)", myEmail);
        result += "<br>";
        result += "<br>";
        result += "UWAGA - AKTUALNA WERSJA APLIKACJI NIE ZOSTAŁA JESZCZE WDROŻONA.";
        result += "<br>";
        result += "Nie działa stale online, a tylko gdy mam ją uruchomioną.";
        result += "<br>";
        result += "Jeśli odpowiedź nie przyjdzie natychmiast skontaktuj się ze mną w inny sposób lub zaczekaj aż sprawdzę te maile ręcznie (zwykle raz dziennie)";

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
