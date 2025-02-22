package szlicht.daniel.calendar.meeting.app_core;

import szlicht.daniel.calendar.common.calendar.GoogleCalendarColor;

import java.util.Arrays;

public enum MeetingType {
    FIRST_MENTORING(GoogleCalendarColor.BLUE),
    MENTORING(GoogleCalendarColor.PINK),
    CYCLIC_MENTORING(GoogleCalendarColor.ORANGE),
    OTHER_MEETING(GoogleCalendarColor.RED),
    ABANDONED_MENTORING(GoogleCalendarColor.PURPLE),
    CANCELED_MENTORING(GoogleCalendarColor.GRAY);

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
