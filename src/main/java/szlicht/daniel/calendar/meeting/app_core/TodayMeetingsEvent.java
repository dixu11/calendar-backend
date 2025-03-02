package szlicht.daniel.calendar.meeting.app_core;

import org.springframework.context.ApplicationEvent;

import java.util.List;

public class TodayMeetingsEvent  {

    private List<Meeting> meetings;
    public TodayMeetingsEvent(List<Meeting> todayMeetings) {
        meetings = todayMeetings;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }
}
