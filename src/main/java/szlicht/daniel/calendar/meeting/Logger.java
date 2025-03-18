package szlicht.daniel.calendar.meeting;

public interface Logger {
    void notifyOwner(String subject, String message, boolean critical);
}
