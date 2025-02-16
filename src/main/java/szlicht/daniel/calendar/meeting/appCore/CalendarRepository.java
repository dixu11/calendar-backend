package szlicht.daniel.calendar.meeting.appCore;

import java.util.Set;

public interface CalendarRepository {
    Set<Meeting> getMonthFromNowMeetings();
    void save(Meeting meeting);
    Set<Meeting> getTodayMeetings();
}
