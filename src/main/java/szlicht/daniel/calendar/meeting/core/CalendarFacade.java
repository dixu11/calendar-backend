package szlicht.daniel.calendar.meeting.core;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalendarFacade {
    private CalendarService calendarService;
    private MeetingsSender meetingsSender;

    public CalendarFacade(CalendarService calendarService, MeetingsSender meetingsSender) {
        this.calendarService = calendarService;
        this.meetingsSender = meetingsSender;
    }

    public void sendPropositions(Integer minutes, String to) {
        Propositions meetingPropositions = calendarService.getMeetingPropositions(minutes);
        meetingsSender.sendPropositions(meetingPropositions, to);
    }

    public Propositions getMeetingPropositions(Integer minutes) {
        return calendarService.getMeetingPropositions(minutes);
    }

    public void arrangeMeeting(Meeting meeting) {
        calendarService.arrangeMeeting(meeting);
    }
}
