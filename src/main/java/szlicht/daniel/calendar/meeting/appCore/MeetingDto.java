package szlicht.daniel.calendar.meeting.appCore;

import lombok.Builder;


import java.time.LocalDateTime;
@Builder
public class MeetingDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private MeetingType type;
    private String summary;
    private String providedDescription;
    private String email;

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public MeetingType getType() {
        return type;
    }

    public String getSummary() {
        return summary;
    }

    public String getProvidedDescription() {
        return providedDescription;
    }

    public String getEmail() {
        return email;
    }
}
