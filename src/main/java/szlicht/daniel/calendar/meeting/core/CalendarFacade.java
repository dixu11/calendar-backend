package szlicht.daniel.calendar.meeting.core;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.java.JavaUtils;

import static szlicht.daniel.calendar.meeting.Params.OWNER_MAIL;


@Service
public class CalendarFacade {
    private CalendarService calendarService;
    private MeetingsSender meetingsSender;
    private WarningService warningService;

    public CalendarFacade(CalendarService calendarService, MeetingsSender meetingsSender) {
        this.calendarService = calendarService;
        this.meetingsSender = meetingsSender;
    }

    @PostConstruct
        //todo for production
    void sendPropositionsToOwnerAtStartup() {
        sendPropositions(60,OWNER_MAIL);
    }

    public void sendPropositions(Integer minutes, String to) {
        Propositions meetingPropositions = calendarService.getMeetingPropositions(minutes);
        meetingsSender.sendPropositions(meetingPropositions, to);
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
