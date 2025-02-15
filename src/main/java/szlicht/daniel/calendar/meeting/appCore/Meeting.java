package szlicht.daniel.calendar.meeting.appCore;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import szlicht.daniel.calendar.common.calendar.GoogleCalendarColor;
import szlicht.daniel.calendar.common.java.LocalDateUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Objects;
import static szlicht.daniel.calendar.common.calendar.GoogleCalendarUtils.toEventDateTime;
import static szlicht.daniel.calendar.common.calendar.GoogleCalendarUtils.toLocalDateTime;

public class Meeting implements Comparable<Meeting>{
    private static final int BUFFER = 15;
    private static final int NO_BUFFER_BELOW = 60;

    private LocalDateTime start;
    private LocalDateTime end;
    private MeetingType type = MeetingType.MENTORING;
    private Details details = null;


    public Meeting(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public Meeting(LocalDateTime start, int minutes) {
        this(start, start.plusMinutes(minutes));
    }

    Meeting(Meeting meetingAfter, int lengthMinutes) {
        this(meetingAfter.getStart().minusMinutes(lengthMinutes),meetingAfter.getStart());
        moveBy(-getBufferAfter());
    }

    public Meeting(Event event) {
        this(toLocalDateTime(event.getStart().getDateTime()),
                toLocalDateTime(event.getEnd().getDateTime()));
        String summary = "";
        String description = "";
        String email = "";

        if (event.getSummary() != null) {
            summary = event.getSummary();
        }
        if (event.getDescription() != null) {
            description = event.getDescription();
        }
        if (event.getAttendees() != null) {
            email = event.getAttendees().get(0).getEmail();
        }
        type = MeetingType.fromColorId(event.getColorId());
        details = new Details(summary, description, email);
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

    public boolean isMentoring() {
        return type == MeetingType.MENTORING;
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
        event.setDescription("Spotkanie umówione automatycznie");
        event.setStart(toEventDateTime(start));
        event.setEnd(toEventDateTime(end));
        event.setColorId(GoogleCalendarColor.PINK.getColorId());
        if (details != null) {
            EventAttendee attendee = new EventAttendee().setEmail(details.mail);
            event.setDescription(details.providedDescription + "\n\n" + event.getDescription());
            event.setAttendees(Collections.singletonList(attendee));
            event.setSummary("Mentoring IT " + attendee.getDisplayName());
            if (!details.providedDescription.isBlank()) {
                event.setSummary("*"+event.getSummary());
            }
        }
        return event;
    }

    public String getMail() {
        return details.mail;
    }

    public String when() {
        return LocalDateUtils.simpleDateTime(start) + "-" + LocalDateUtils.simpleTime(end);
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

    @Override
    public int compareTo(Meeting o) {
        return start.compareTo(o.start);
    }

    public static class Details {
        private String summary = "";
        private String providedDescription = "";
        private String mail = "";

        public Details(String summary, String providedDescription, String mail) {
            this.summary = summary;
            this.providedDescription = providedDescription;
            this.mail = mail;
        }
    }
}
