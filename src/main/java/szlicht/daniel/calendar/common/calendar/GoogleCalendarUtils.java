package szlicht.daniel.calendar.common.calendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;

import java.time.*;
import java.util.Date;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

public class GoogleCalendarUtils {

    public static EventDateTime toEventDateTime(LocalDateTime localDateTime) {
        return new EventDateTime()
                .setDateTime(toDateTime(localDateTime))
                .setTimeZone(params.values().zone());
    }

    public static DateTime toDateTime(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(params.values().getZoneId());
        return new DateTime(zonedDateTime.toInstant().toEpochMilli());
    }

    public static LocalDateTime toLocalDateTime(DateTime dateTime) {
       return Instant.ofEpochMilli(dateTime.getValue()).atZone(params.values().getZoneId())
                .toLocalDateTime();
    }
}
