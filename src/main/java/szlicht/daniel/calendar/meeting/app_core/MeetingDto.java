package szlicht.daniel.calendar.meeting.app_core;

import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;
@Builder
@Data
public class MeetingDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private MeetingType type;
    private String id;
    private String summary;
    private String providedDescription;
    private String email;
    private String studentName;
    private boolean noCollisions;
}
