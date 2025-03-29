package szlicht.daniel.calendar.dialog;

import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.meeting.CalendarAppService;
import szlicht.daniel.calendar.meeting.CalendarOfflineException;
import szlicht.daniel.calendar.meeting.Propositions;

@Component
public class PropositionsScenario extends DialogScenario{
    private CalendarAppService calendarAppService;
    private MeetingsSender meetingsSender;

    public PropositionsScenario(DialogPresenter dialogPresenter, CalendarAppService calendarAppService, MeetingsSender meetingsSender) {
        super(dialogPresenter);
        this.calendarAppService = calendarAppService;
        this.meetingsSender = meetingsSender;
    }

    @Override
    public String keyword() {
        return "terminy";
    }

    @Override
    public void runScenario(EmailParser emailParser) {
        try {
            Propositions propositions = calendarAppService.getPropositions(emailParser.getMinutes());
            meetingsSender.sendPropositions(propositions, emailParser.getEmail());
        } catch (CalendarOfflineException e) { //todo refactor
            System.err.println("calendar offline");
        }
    }
}
