package szlicht.daniel.calendar.meeting.appCore;

import szlicht.daniel.calendar.common.calendar.GoogleCalendarColor;

import java.util.Arrays;

public enum MeetingType {
    MENTORING(GoogleCalendarColor.PINK),RECURSIVE(GoogleCalendarColor.ORANGE),OTHER_MEETING(GoogleCalendarColor.RED);

    private GoogleCalendarColor color;

    MeetingType(GoogleCalendarColor color) {
        this.color = color;
    }

    public static MeetingType fromColorId(String colorId) {
        return Arrays.stream(values())
                .filter(value -> value.getColor().getColorId().equals(colorId))
                .findAny()
                .orElse(OTHER_MEETING);
    }

    public GoogleCalendarColor getColor() {
        return color;
    }
}
