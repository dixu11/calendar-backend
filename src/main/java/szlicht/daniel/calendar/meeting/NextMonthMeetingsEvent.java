package szlicht.daniel.calendar.meeting;

import java.util.Set;

public class NextMonthMeetingsEvent {
    private Set<Meeting> meetings;

    public NextMonthMeetingsEvent(Set<Meeting> meetings) {
        this.meetings = meetings;
    }

    public Set<Meeting> getMeetings() {
        return meetings;
    }
}
