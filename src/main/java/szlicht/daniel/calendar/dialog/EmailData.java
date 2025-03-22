package szlicht.daniel.calendar.dialog;

import lombok.Builder;
import lombok.Data;
import szlicht.daniel.calendar.meeting.MeetingDto;

@Data
@Builder
public class EmailData {

    private ScenarioType scenarioType;
    private Integer minutes;
    private String email;
    private String name;
    private String content;
    private MeetingDto meetingDto;
    private StudentStartMessageDto studentStartMessageDto;
}
