package szlicht.daniel.calendar.meeting;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

@Service
public class CalendarService {

    private static final int DEFAULT_MEETING_LENGTH_MINUTES = 90;
    private static final double[] ACCEPTABLE_LENGTH_HOURS = {1, 1.5, 2, 2.5, 3};

    private MeetingsPlanner meetingsPlanner;

    public CalendarService(MeetingsPlanner meetingsPlanner) {
        this.meetingsPlanner = meetingsPlanner;
    }

    public Map<LocalDate, Meeting> getMeetingPropositions(Integer minutesLength) {
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

    private boolean notAcceptableLength(int minutes) {
        return Arrays.stream(ACCEPTABLE_LENGTH_HOURS)
                .mapToInt(hour -> (int) (hour * 60))
                .noneMatch(minutesAccepted -> minutesAccepted == minutes);
    }
}
