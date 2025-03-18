package szlicht.daniel.calendar.meeting;

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
