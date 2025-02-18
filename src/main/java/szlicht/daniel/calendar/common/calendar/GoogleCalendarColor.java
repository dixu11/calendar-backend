package szlicht.daniel.calendar.common.calendar;

public enum GoogleCalendarColor {
    LIGHT_BLUE("1"),
    LIGHT_GREEN("2"),
    PURPLE("3"),
    PINK("4"),
    YELLOW("5"),
    ORANGE("6"),
    BLUE("7"),
    GRAY("8"),
    DARK_BLUE("9"),
    DARK_GREEN("10"),
    RED("11");

    private final String colorId;

    GoogleCalendarColor(String colorId) {
        this.colorId = colorId;
    }

    public String getColorId() {
        return colorId;
    }

    public static GoogleCalendarColor fromId(String id) {
        for (GoogleCalendarColor color : values()) {
            if (color.colorId.equals(id)) {
                return color;
            }
        }
        throw new IllegalArgumentException("Unknown color ID: " + id);
    }

    @Override
    public String toString() {
        return name() + " (" + colorId + ")";
    }
}
