package szlicht.daniel.calendar.meeting;

import com.google.api.services.calendar.model.Event;
import szlicht.daniel.calendar.common.GoogleCalendarClient;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class Meeting {
    private static final int BUFFER = 15;
    private static final int NO_BUFFER_BELOW = 60;

    private LocalDateTime start;
    private LocalDateTime end;

    public Meeting(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public Meeting(Meeting meetingAfter, int lengthMinutes) {
        this.start = meetingAfter.getStart().minusMinutes(lengthMinutes);
        this.end = meetingAfter.getStart();
        moveBy(-getBufferAfter());
    }

    public Meeting(Event event) {
        this.start = GoogleCalendarClient.toLocalDateTime(event.getStart().getDateTime());
        this.end = GoogleCalendarClient.toLocalDateTime(event.getEnd().getDateTime());
    }

    public Meeting() {
    }

    public static Meeting createBefore(Meeting meetingAfter, int minutes) {
        return new Meeting(meetingAfter,minutes);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }


    public boolean collideWith(Meeting otherMeeting) {
        LocalDateTime thisEndWithBuffer = getEndPlusBuffer();
        LocalDateTime otherEndWithBuffer = otherMeeting.getEndPlusBuffer();
        boolean collide = thisEndWithBuffer.isAfter(otherMeeting.start)
                && otherEndWithBuffer.isAfter(this.start);
        return collide;
    }

    private LocalDateTime getEndPlusBuffer() {
        return end.plusMinutes(getBufferAfter());
    }

    private int getBufferAfter(){
        if (getLengthMinutes() <= NO_BUFFER_BELOW) {
            return 0;
        }
        return BUFFER;
    }

    private int getLengthMinutes() {
        return (int) start.until(end, ChronoUnit.MINUTES);
    }

    private void moveBy(int minutes) {
        start = start.plusMinutes(minutes);
        end = end.plusMinutes(minutes);
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
