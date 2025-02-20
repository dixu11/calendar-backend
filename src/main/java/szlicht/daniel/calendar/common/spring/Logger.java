package szlicht.daniel.calendar.common.spring;

public interface Logger {
    void notifyOwner(String subject, String message, boolean critical);
}
