package szlicht.daniel.calendar.meeting.core;

import com.google.api.services.calendar.model.Event;
import szlicht.daniel.calendar.common.GoogleCalendarClient;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class Meeting {
    private static final int BUFFER = 15;
    private static final int NO_BUFFER_BELOW = 60;

    private LocalDateTime start;
    private LocalDateTime end;

    Meeting(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    Meeting(Meeting meetingAfter, int lengthMinutes) {
        this.start = meetingAfter.getStart().minusMinutes(lengthMinutes);
        this.end = meetingAfter.getStart();
        moveBy(-getBufferAfter());
    }

    Meeting(Event event) {
        this.start = GoogleCalendarClient.toLocalDateTime(event.getStart().getDateTime());
        this.end = GoogleCalendarClient.toLocalDateTime(event.getEnd().getDateTime());
    }

    Meeting() {
    }

    static Meeting createBefore(Meeting meetingAfter, int minutes) {
        return new Meeting(meetingAfter,minutes);
    }

    LocalDateTime getStart() {
        return start;
    }

    LocalDateTime getEnd() {
        return end;
    }


    boolean collideWith(Meeting otherMeeting) {
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

    public int getLengthMinutes() {
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
