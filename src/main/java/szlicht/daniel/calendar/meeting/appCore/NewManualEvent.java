package szlicht.daniel.calendar.meeting.appCore;

public class NewManualEvent {
    private MeetingDto meetingDto;

    public NewManualEvent(MeetingDto meetingDto) {
        this.meetingDto = meetingDto;
    }

    public MeetingDto getMeetingDto() {
        return meetingDto;
    }
}
