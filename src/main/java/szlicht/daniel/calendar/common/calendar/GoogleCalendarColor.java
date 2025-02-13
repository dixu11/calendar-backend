package szlicht.daniel.calendar.common.calendar;

public enum GoogleCalendarColor {
    LIGHT_BLUE("1"),       // Jasnoniebieski
    LIGHT_GREEN("2"),      // Jasnozielony
    PURPLE("3"), // purple
    PINK("4"),             // pink
    YELLOW("5"),           // yellow
    DARK_BLUE("6"),        // Ciemnoniebieski
    RED("7"),              // Czerwony
    GRAY("8"),             // Szary
    TEAL("9"),             // Niebieskozielony (Turkusowy)
    LIGHT_GRAY("10"),      // Jasnoszary
    DARK_GREEN("11");      // Ciemnozielony

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
