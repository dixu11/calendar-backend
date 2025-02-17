package szlicht.daniel.calendar.meeting.appCore;

import java.util.Set;

public interface CalendarRepository {
    Set<Meeting> getMonthFromNowMeetings();
    void saveFirst(Meeting meeting);
    Set<Meeting> getTodayMeetings();
    void updateMeeting(Meeting meeting);
}
