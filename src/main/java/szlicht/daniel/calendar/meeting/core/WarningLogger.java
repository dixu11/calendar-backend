package szlicht.daniel.calendar.meeting.core;

public interface WarningLogger {
    void notifyOwner(String subject, String message, boolean critical);
}
