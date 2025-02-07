package szlicht.daniel.calendar.meeting.core;

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
class CalendarService {

    private static final int DEFAULT_MEETING_LENGTH_MINUTES = 90;
    private static final double[] ACCEPTABLE_LENGTH_HOURS = {1, 1.25, 1.5, 2, 2.5, 3};

    private MeetingsPlanner meetingsPlanner;

    CalendarService(MeetingsPlanner meetingsPlanner) {
        this.meetingsPlanner = meetingsPlanner;
    }

    Propositions getMeetingPropositions(Integer minutesLength) {
        if (minutesLength == null) {
            minutesLength = DEFAULT_MEETING_LENGTH_MINUTES;
        }
        if (notAcceptableLength(minutesLength)) {
            throw new IllegalArgumentException(
                    String.format("Only length of %s hours is acceptable for automatic meeting with me.",
                            Arrays.toString(ACCEPTABLE_LENGTH_HOURS))
            );
        }
        return meetingsPlanner.getMeetingSuggestions(minutesLength);
    }

    void arrangeMeeting(Meeting meeting) {
        Propositions propositions = getMeetingPropositions(meeting.getLengthMinutes());
        if (propositions.isNotValid(meeting)) {
            throw new IllegalArgumentException("(collision) Cannot arrange meeting: " + meeting);
        }
        meetingsPlanner.arrange(meeting);
    }

    private boolean notAcceptableLength(int minutes) {
        return Arrays.stream(ACCEPTABLE_LENGTH_HOURS)
                .mapToInt(hour -> (int) (hour * 60))
                .noneMatch(minutesAccepted -> minutesAccepted == minutes);
    }
}
