package szlicht.daniel.calendar.meeting.app_core;

import szlicht.daniel.calendar.common.java.LocalDateUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

public class Meeting implements Comparable<Meeting> {
    private static final int BUFFER = 15;
    private static final int NO_BUFFER_BELOW = 60;
    private String id;
    private LocalDateTime start;
    private LocalDateTime end;
    private MeetingType type = MeetingType.MENTORING;
    private Details details = null;
    private boolean noCollisions = false;


    public Meeting(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public Meeting(LocalDateTime start, int minutes) {
        this(start, start.plusMinutes(minutes));
    }

    Meeting(Meeting meetingAfter, int lengthMinutes) {
        this(meetingAfter.getStart().minusMinutes(lengthMinutes), meetingAfter.getStart());
        moveBy(-getBufferAfter());
    }

    Meeting() {
    }

    static Meeting createBefore(Meeting meetingAfter, int minutes) {
        return new Meeting(meetingAfter, minutes);
    }

    public boolean isManual() {
        return !details.summary.toLowerCase()
                .contains(params.values().summaryPrefix().toLowerCase());
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


    public String when() {
        return  LocalDateUtils.when(start,end) ;
    }

    private void moveBy(int minutes) {
        start = start.plusMinutes(minutes);
        end = end.plusMinutes(minutes);
    }

    public MeetingDto toDto() {
        return new MeetingDto(start,end,type,id, details.summary, details.ownerDescription,details.email,"",noCollisions);
    }

    public Meeting setDetails(Details details) {
        this.details = details;
        return this;
    }

    public Meeting setNoCollisions(boolean noCollisions) {
        this.noCollisions = noCollisions;
        return this;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public String getDescription() {
        return details.ownerDescription;
    }

    public Details getDetails() {
        return details;
    }

    public MeetingType getType() {
        return type;
    }

    public Meeting setType(MeetingType meetingType) {
        this.type = meetingType;
        return this;
    }

    public Meeting setId(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return id;
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

    public boolean isNoCollision() {
        return noCollisions;
    }


    public static class Details {
        private String summary = "";
        private String ownerDescription = "";
        private String providedDescription = "";
        private String email = "";

        public Details(String summary, String ownerDescription, String providedDescription, String email) {
            this.summary = summary;
            this.ownerDescription = ownerDescription;
            this.providedDescription = providedDescription;
            this.email = email;
        }

        String getFullDescription() {
            return ownerDescription + " " + providedDescription;
        }

        public String getSummary() {
            return summary;
        }

        public String getOwnerDescription() {
            return ownerDescription;
        }

        public String getProvidedDescription() {
            return providedDescription;
        }

        public String getEmail() {
            return email;
        }

        public void setSummary(String newSummary) {
            this.summary = newSummary;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setOwnerDescription(String ownerDescription) {
            this.ownerDescription = ownerDescription;
        }
    }
}
