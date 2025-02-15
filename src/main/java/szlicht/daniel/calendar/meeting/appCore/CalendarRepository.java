package szlicht.daniel.calendar.meeting.appCore;

import java.util.Set;

public interface CalendarRepository {
    Set<Meeting> getMonthRangeMeetings();
    void save(Meeting meeting);
    Set<Meeting> getTodayMeetings();
}
