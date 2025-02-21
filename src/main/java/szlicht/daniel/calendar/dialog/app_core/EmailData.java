package szlicht.daniel.calendar.dialog.app_core;

import lombok.Builder;
import lombok.Data;
import szlicht.daniel.calendar.meeting.app_core.MeetingDto;

@Data
@Builder
public class EmailData {

    private DialogType dialogType;
    private Integer minutes;
    private String email;
    private String name;
    private String content;
    private MeetingDto meetingDto;
    private StudentStartMessageDto studentStartMessageDto;
}
