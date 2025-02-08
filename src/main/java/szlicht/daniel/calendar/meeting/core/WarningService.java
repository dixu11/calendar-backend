package szlicht.daniel.calendar.meeting.core;

public interface WarningService {
    void notifyOwner(String subject, String message, boolean critical);
}
