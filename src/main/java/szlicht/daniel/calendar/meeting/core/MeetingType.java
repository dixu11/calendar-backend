package szlicht.daniel.calendar.meeting.core;

import szlicht.daniel.calendar.common.calendar.GoogleCalendarColor;

import java.util.Arrays;

public enum MeetingType {
    MENTORING(GoogleCalendarColor.PINK),OTHER_MEETING(GoogleCalendarColor.DARK_BLUE);

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
