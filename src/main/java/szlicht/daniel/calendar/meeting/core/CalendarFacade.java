package szlicht.daniel.calendar.meeting.core;

import org.springframework.stereotype.Service;

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
        try {
            calendarService.arrangeMeeting(meeting);
            meetingsSender.notifyArrangementComplete(meeting);
        } catch (CalendarOfflineException e) {
            meetingsSender.notifyCalendarOffline(meeting,e.getMessage());
        }
    }
}
