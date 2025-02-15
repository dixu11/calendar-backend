package szlicht.daniel.calendar.meeting.appCore;

public interface WarningLogger {
    void notifyOwner(String subject, String message, boolean critical);
}
