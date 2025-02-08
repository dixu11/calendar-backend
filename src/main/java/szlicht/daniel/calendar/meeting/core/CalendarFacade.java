package szlicht.daniel.calendar.meeting.core;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.java.JavaUtils;
import szlicht.daniel.calendar.common.spring.SpringUtils;
import szlicht.daniel.calendar.meeting.MeetingParams;

import java.time.DayOfWeek;

import static szlicht.daniel.calendar.common.spring.SpringUtils.params;


@Service
public class CalendarFacade implements ApplicationListener<ApplicationReadyEvent> {
    private CalendarService calendarService;
    private MeetingsSender meetingsSender;
    private WarningService warningService;

    public CalendarFacade(CalendarService calendarService, MeetingsSender meetingsSender) {
        this.calendarService = calendarService;
        this.meetingsSender = meetingsSender;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SpringUtils.initParams();
        sendPropositions(60, params.mail().owner());
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
