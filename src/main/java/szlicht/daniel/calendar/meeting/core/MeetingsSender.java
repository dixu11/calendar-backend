package szlicht.daniel.calendar.meeting.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.EmailService;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static szlicht.daniel.calendar.common.HtmlUtils.mailto;
import static szlicht.daniel.calendar.common.LocalDateUtils.*;

@Service
class MeetingsSender {
    @Value("${meeting.mail.owner}")
    private String MY_MAIL;
    @Value("${meeting.mail.bot}")
    private String BOT_MAIL;
    @Value("${meeting.params.hours}")
    private List<Double> MEETING_HOURS;
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
        result += "Aby wybrać inną dłogość wybierz i wyślij mail:";
        result += "<br>";
        result += "Dostępne długości lekcji: " + formatMailtoHours();
        result += "<br>";
        result += String.format("Jeśli żaden z terminów Ci nie podpasował a zależy Ci na spotkaniu " +
                "napisz kiedy jesteś dostępny/a na: %s, poszukamy terminu indywidualnie :)", MY_MAIL);
        result += "<br>";
        result += "<br>";
        result += "UWAGA - AKTUALNA WERSJA APLIKACJI NIE ZOSTAŁA JESZCZE WDROŻONA.";
        result += "<br>";
        result += "Nie działa stale online, a tylko gdy mam ją uruchomioną.";
        result += "<br>";
        result += "Jeśli odpowiedź nie przyjdzie natychmiast skontaktuj się ze mną w inny sposób lub zaczekaj aż sprawdzę te maile ręcznie (zwykle raz dziennie)";
        return result;
    }

    private String formatMailtoHours() {
        StringBuilder mailto = new StringBuilder();
        for (Double meetingHour : MEETING_HOURS) {
            mailto.append(mailto(
                    String.valueOf(meetingHour),
                    String.format("Poproszę o proponowane terminy mentoringu o długości %.1fh",meetingHour),
                    String.format("%.1fh",meetingHour),
                    BOT_MAIL))
                    .append(" ");
        }
        return mailto.toString();
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
                    .append(simpleTime(meeting.getEnd()));
            propositions.append(line)
                    .append(" ")
                    .append(formatMailtoProposition(meeting));
            System.out.print(line);
        }
        propositions.append("</pre>");
        return propositions.toString();
    }

    private String formatMailtoProposition(Meeting meeting) {
        return mailto(
                String.format("Chcę zaproponować spotkanie | meeting"),
                String.format("meeting\nstart:%s\nlength:%d",meeting.getStart(),meeting.getLengthMinutes()),
                String.format("umów się"),
                BOT_MAIL
        );
    }
}
