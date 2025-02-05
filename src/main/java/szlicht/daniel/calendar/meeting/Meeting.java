package szlicht.daniel.calendar.meeting;

import com.google.api.services.calendar.model.Event;
import szlicht.daniel.calendar.common.GoogleCalendarClient;

import java.time.LocalDateTime;

public class Meeting {
    private LocalDateTime start;
    private LocalDateTime end;

    public Meeting(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public Meeting(Event event) {
        this.start = GoogleCalendarClient.toLocalDateTime(event.getStart().getDateTime());
        this.end = GoogleCalendarClient.toLocalDateTime(event.getEnd().getDateTime());
    }

    public Meeting() {
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }


    public boolean collideWith(Meeting otherMeeting, int minutesBuffer) {
        LocalDateTime thisEndWithBuffer = end.plusMinutes(minutesBuffer);
        LocalDateTime otherEndWithBuffer = otherMeeting.end.plusMinutes(minutesBuffer);
        boolean collide = thisEndWithBuffer.isAfter(otherMeeting.start)
                && otherEndWithBuffer.isAfter(this.start);
        return collide;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
