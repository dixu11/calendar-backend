package szlicht.daniel.calendar.dialog;

import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.meeting.CalendarAppService;
import szlicht.daniel.calendar.meeting.Logger;

@Component
public class SoloMentoringScenario extends DialogScenario{
    private CalendarAppService calendarAppService;
    private MeetingsSender meetingsSender;
    private Logger logger;

    public SoloMentoringScenario(DialogPresenter dialogPresenter, CalendarAppService calendarAppService, MeetingsSender meetingsSender, Logger logger) {
        super(dialogPresenter);
        this.calendarAppService = calendarAppService;
        this.meetingsSender = meetingsSender;
        this.logger = logger;
    }

    @Override
    public String keyword() {
        return "indywidualne lekcje";
    }

    @Override
    public void runScenario(EmailParser emailParser) {
        dialogPresenter.showDialog(new SoloMentoringDialogView(emailParser.getEmail(),
                        meetingsSender.getFormatedPropositions(calendarAppService.getPropositions(emailParser.getMinutes()))),
                emailParser);
        logger.notifyOwner("Solo mentoring offer sent to "+ emailParser.getName() + " mail:" + emailParser.parseEmail(),
                "response to decision: "+ emailParser.getContent() , false); //todo simplify and standardize
    }
}
