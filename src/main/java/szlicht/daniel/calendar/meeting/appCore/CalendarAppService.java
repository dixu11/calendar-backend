package szlicht.daniel.calendar.meeting.appCore;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.java.JavaUtils;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;


@Service
public class CalendarAppService {
    private PropositionsDomainService propositionsDomainService;
    private ArrangeMeetingDomainService arrangeMeetingDomainService;
    private MeetingsSender meetingsSender;
    private WarningLogger warningLogger;

    public CalendarAppService(PropositionsDomainService propositionsDomainService, ArrangeMeetingDomainService arrangeMeetingDomainService, MeetingsSender meetingsSender, WarningLogger warningLogger) {
        this.propositionsDomainService = propositionsDomainService;
        this.arrangeMeetingDomainService = arrangeMeetingDomainService;
        this.meetingsSender = meetingsSender;
        this.warningLogger = warningLogger;
    }

    @EventListener
    @Async
    public void handle(AppStartedEvent appStartedEvent) {
        sendPropositions(60, params.mail().owner());
    }

    public void sendPropositions(Integer minutes, String to) {
        System.out.println("trying to send propositions to " + to);
        try {
            Propositions meetingPropositions = getMeetingPropositions(minutes);
            meetingsSender.sendPropositions(meetingPropositions, to);
        } catch (CalendarOfflineException e) {
            System.err.println("calendar offline");
        }
    }

    public Propositions getMeetingPropositions(Integer minutes) {
        return propositionsDomainService.createMeetingPropositions(minutes);
    }

    public void arrangeMeeting(Meeting meeting,String providedDescription, String mail) {
        try {
            meeting.setDetails(new Meeting.Details(params.values().summaryPrefix() + mail,
                    "Spotkanie umówione automatycznie",
                    providedDescription, mail)
            );
            arrangeMeetingDomainService.arrange(meeting);
            meetingsSender.notifyArrangementComplete(meeting);
            System.err.println(meeting.getDetails().getMail() + " meeting proposition at: " + meeting.when() + " approved");
        } catch (CalendarOfflineException | MeetingCollisionException e) {
            System.err.println(meeting.getDetails().getMail() + " meeting proposition at: " + meeting.when() + " declined");
            meetingsSender.notifyArrangementFailed(meeting, e.getMessage());
        } catch (Exception e) {
            warningLogger.notifyOwner("Unexpected error",
                    e.getMessage() + " " + JavaUtils.getStackTrace(e),
                    true);
            e.printStackTrace();
        }
    }
}
