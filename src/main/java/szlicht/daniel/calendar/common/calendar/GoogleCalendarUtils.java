package szlicht.daniel.calendar.common.calendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class GoogleCalendarUtils {
    public static DateTime toDateTime(LocalDateTime localDateTime) {
        return new DateTime(localDateTime.toEpochSecond(ZoneOffset.UTC)*1000);
    }

    public static EventDateTime toEventDateTime(LocalDateTime localDateTime) {
        return new EventDateTime()
                .setDateTime(toDateTime(localDateTime.minusHours(1)))
                .setTimeZone("Europe/Warsaw");
    }

    public static LocalDateTime toLocalDateTime(DateTime dateTime) {
        return LocalDateTime.ofEpochSecond(dateTime.getValue() / 1000, 0, ZoneOffset.ofHours(1));
    }
}
