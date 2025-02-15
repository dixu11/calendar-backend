package szlicht.daniel.calendar.common.calendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;

import java.time.*;
import java.util.Date;

public class GoogleCalendarUtils {

    private static final String APP_ZONE = "Europe/Warsaw";

    public static EventDateTime toEventDateTime(LocalDateTime localDateTime) {
        return new EventDateTime()
                .setDateTime(toDateTime(localDateTime))
                .setTimeZone(APP_ZONE);
    }

    public static DateTime toDateTime(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(APP_ZONE));
        return new DateTime(zonedDateTime.toInstant().toEpochMilli());
    }

    public static LocalDateTime toLocalDateTime(DateTime dateTime) {
       return Instant.ofEpochMilli(dateTime.getValue()).atZone(ZoneId.of(APP_ZONE))
                .toLocalDateTime();
    }
}
