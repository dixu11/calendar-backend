package szlicht.daniel.calendar.meeting.core;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.java.JavaUtils;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;


@Service
public class CalendarFacade  {
    private CalendarService calendarService;
    private MeetingsSender meetingsSender;
    private WarningService warningService;

    public CalendarFacade(CalendarService calendarService, MeetingsSender meetingsSender) {
        this.calendarService = calendarService;
        this.meetingsSender = meetingsSender;
    }

    @EventListener
    public void handle(AppStartedEvent appStartedEvent) {
        sendPropositions(60, params.mail().owner());
    }



    public void sendPropositions(Integer minutes, String to) {
        if (minutes == null) {
            minutes = params.values().minutes();
        }
        try {
            Propositions meetingPropositions = calendarService.getMeetingPropositions(minutes);
            meetingsSender.sendPropositions(meetingPropositions, to);
        } catch (CalendarOfflineException e) {
            System.err.println("calendar offline");
        }
    }

    public Propositions getMeetingPropositions(Integer minutes) {
        return calendarService.getMeetingPropositions(minutes);
    }

    public void arrangeMeeting(Meeting meeting) {
        try {
            calendarService.arrangeMeeting(meeting);
            meetingsSender.notifyArrangementComplete(meeting);
            System.err.println(meeting.getMail() + " meeting proposition at: " + meeting.when() + " approved");
        } catch (CalendarOfflineException | MeetingCollisionException e) {
            System.err.println(meeting.getMail() + " meeting proposition at: " + meeting.when() + " declined");
            meetingsSender.notifyArrangementFailed(meeting, e.getMessage());
        } catch (Exception e) {
            warningService.notifyOwner("Unexpected error",
                    e.getMessage() + " " + JavaUtils.getStackTrace(e),
                    true);
            e.printStackTrace();
        }
    }
}
