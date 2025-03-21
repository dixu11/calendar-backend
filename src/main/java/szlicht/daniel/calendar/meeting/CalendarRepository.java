package szlicht.daniel.calendar.meeting;

import java.util.Set;

public interface CalendarRepository {
    Set<Meeting> getMonthFromNowMeetings();
    Set<Meeting> getMonthFromNowEvents();
    void save(Meeting meeting);
    Set<Meeting> getTodayMeetings();
    void updateMeeting(Meeting meeting);
    void removeMeetingById(String id);
}
