package szlicht.daniel.calendar.meeting.appCore;

import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.mail.EmailService;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static szlicht.daniel.calendar.common.mail.MailUtils.mailto;
import static szlicht.daniel.calendar.common.java.LocalDateUtils.*;
import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

@Service
class MeetingsSender {

    private EmailService emailService;

    MeetingsSender(EmailService emailService) {
        this.emailService = emailService;
    }

    void sendPropositions(Propositions propositions, String to) {
        String body = formatBody(propositions);
        emailService.sendHtmlEmail(to, "Mentoring z Daniel Szlicht - proponowane terminy", body);
        System.err.println("Propositions send to " + to);
    }

    private String formatBody(Propositions propositions) {
        String result = "";
        result += "<center>";
        result += String.format("<h2>Wybierz dogodny termin na lekcję trwającą <b>%.1fh</b></h2>", propositions.getHours());
        if (propositions.getHours() == 1) {
            result += "<h3>Uwaga!</h3>";
            result += "\"Godzinne\" spotkania trwają dokładnie <b>55 minut</b>.";
            result += "<br>";
            result += "Umów się na dłuże spotkanie jeśli chcesz odzyskać swoje 5 minut ;)";
        }
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
        result += "<br>";
        result += "Aby wybrać <b>inną długość</b> kliknij i wyślij mail:";
        result += "<br>";
        result += "Dostępne długości lekcji: " + formatMailtoHours();
        result += "<br>";
        result += String.format("Jeśli <b>żaden</b> z terminów Ci nie podpasował");
        result +=  " a zależy Ci na spotkaniu <b>napisz</b> kiedy jesteś dostępny/a na:";
        result += "<br>";
        result += params.mail().owner();
        result += "<br>";
        result += "poszukamy terminu <b>indywidualnie</b> :)";
        result += "<br>";
        result += "<br>";
        result += "<br>";
        result += "<b>AKTUALNA WERSJA APLIKACJI JEST BARDZO WCZESNA I MOŻE ZAWIERAĆ BŁĘDY!</b>";
        result += "<br>";
        result += "Jeśli odpowiedź nie przyjdzie w ciągu minuty, skontaktuj się ze mną w inny sposób.";
        result += "<br>";
        result += "<br>";
        result += "Jeśli znajdziesz <b>błędy</b> lub masz jakieś <b>uwagi i pomysły</b> - koniecznie mi o tym powiedz!";
        result += "</center>";
        return result;
    }

    private String formatMailtoHours() {
        StringBuilder mailto = new StringBuilder();
        for (Double meetingHour : params.values().hours()) {
            mailto.append(mailto(
                            "terminy " + meetingHour + "h",
                            String.format("Poproszę o proponowane terminy mentoringu o długości %.1fh", meetingHour),
                            String.format("%.1fh", meetingHour),
                            params.mail().bot()))
                    .append("  ");
        }
        return mailto.toString();
    }

    private String formatPropositions(List<Meeting> meetings) {
        StringBuilder propositions = new StringBuilder();
        propositions.append("<pre>");
        if (meetings.isEmpty()) {
            propositions.append("Brak terminów w tym okresie :(");
        }
        for (Meeting meeting : meetings) {
            StringBuilder line = new StringBuilder();
            String weekDay = meeting.getStart()
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("pl-PL"));
            line.append(String.format("%-6s", weekDay))
                    .append(simpleDate(meeting.getStart()))
                    .append("  ")
                    .append(simpleTime(meeting.getStart()))
                    .append(" - ")
                    .append(simpleTime(meeting.getEnd()));
            propositions.append(line)
                    .append("  ")
                    .append(formatMailtoProposition(meeting));
        }
        propositions.append("</pre>");
        return propositions.toString();
    }

    private String formatMailtoProposition(Meeting meeting) {
        return mailto(
                String.format("Chcę zaproponować spotkanie | meeting"),
                String.format("start#%s\nlength#%d\n\n" +
                                "Uwaga:\nNie modyfikuj powyższych kluczy aby aplikacja mogła je poprawnie zinterpretować.\n" +
                                "Jeśli masz do mnie jakieś uwagi przed spotkaniem dopisz je poniżej:\n%s"
                        , meeting.getStart(), meeting.getLengthMinutes(), params.keywords().description()),
                String.format("umów się"),
                params.mail().bot()
        );
    }

    public void notifyArrangementComplete(Meeting meeting) {
        String body = "<h2>Lekcja zaplanowana!</h2>";
        body += "Spotkanie można bezpłatnie anulować na <b>dobę</b> przed. " +
                "<br>W tym celu napisz mi SMS na numer: <b>"
                + params.mail().phone() + "</b> lub powiadom na <b>Skype</b>.";
        body += "<br>";
        body += "Mój brak odpowiedzi potraktuj jako <b>akceptację spotkania.</b>";
        body += "<br>";
        body += "<br>";
        body += "W szczególnej sytuacji mogę odrzucić zaproponowane spotkanie," +
                " dostaniesz wtedy o tym informację mailową.";
        body += "<br>";
        body += "<br>";
        body += "Do usłyszenia <b>" + meeting.when() + "</b> ! ";
        body += "<br>";
        body += "Tuż przed lekcją napisz mi, " +
                "że jesteś już gotowy/a to zadzwonię od razu :)";
        body += "<br>";
        body += "Do tego czasu <b>naskrob trochę kodu</b> !";
        emailService.sendHtmlEmail(meeting.getDetails().getMail(), "Zaplanowano lekcję: " + meeting.when(),
                body);
    }

    public void notifyArrangementFailed(Meeting meeting, String errorMessage) {
        emailService.sendSimpleEmail(meeting.getDetails().getMail(),"Nie mogę umówić Twojego spotkania.",
                errorMessage);
    }
}
