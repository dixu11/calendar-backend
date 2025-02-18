package szlicht.daniel.calendar.common.spring;

public interface WarningLogger {
    void notifyOwner(String subject, String message, boolean critical);
}
