package szlicht.daniel.calendar.meeting.core;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import szlicht.daniel.calendar.common.GoogleCalendarClient;
import szlicht.daniel.calendar.common.GoogleCalendarColor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Objects;

import static szlicht.daniel.calendar.common.GoogleCalendarClient.toEventDateTime;

public class Meeting {
    private static final int BUFFER = 15;
    private static final int NO_BUFFER_BELOW = 60;

    private LocalDateTime start;
    private LocalDateTime end;
    private Details details = null;

    public Meeting(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public Meeting(LocalDateTime start, int minutes) {
        this.start = start;
        this.end = start.plusMinutes(minutes);
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
        return new Meeting(meetingAfter, minutes);
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

    private int getBufferAfter() {
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

    public Meeting setDetails(Details details) {
        this.details = details;
        return this;
    }

    public Event asEvent() {
        Event event = new Event();
        event.setSummary("Nowe spotkanie");
        event.setDescription("Spotkanie umówione automatycznie");
        event.setStart(toEventDateTime(start));
        event.setEnd(toEventDateTime(end));
        event.setColorId(GoogleCalendarColor.WAITING.getColorId());
        if (details != null) {
            EventAttendee attendee = new EventAttendee().setEmail(details.mail);
            event.setDescription(details.providedDescription + "\n\n" + event.getDescription());
            event.setAttendees(Collections.singletonList(attendee));
            event.setSummary(details.mail);
            if (!details.providedDescription.isBlank()) {
                event.setColorId(GoogleCalendarColor.WAITING_WITH_SUMMARY.getColorId());
            }
        }
        return event;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
        return Objects.equals(start, meeting.start) && Objects.equals(end, meeting.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
    public static class Details {
        private String mail = "";
        private String providedDescription = "";

        public Details(String mail, String providedDescription) {
            this.mail = mail;
            this.providedDescription = providedDescription;
        }
    }
}
