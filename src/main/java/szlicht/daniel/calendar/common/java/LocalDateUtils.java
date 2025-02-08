package szlicht.daniel.calendar.common.java;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LocalDateUtils {

    public static List<LocalDate> getDatesBetweenInclude(LocalDate date1, LocalDate date2){
        List<LocalDate> dates = new ArrayList<>();
        LocalDate nextDate = date1;
        while (!nextDate.equals(date2)) {
            dates.add(nextDate);
            nextDate = nextDate.plusDays(1);
        }
        dates.add(nextDate);
        return dates;
    }

    public static String simpleTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static String simpleDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM"));
    }

    public static String simpleDateTime(LocalDateTime localDateTime) {
        return simpleDate(localDateTime) + " " + simpleTime(localDateTime);
    }

    public static LocalDate nextMonday(LocalDate localDate) {
        LocalDate next = localDate.plusDays(1);
        while (next.getDayOfWeek() != DayOfWeek.MONDAY) {
            next = next.plusDays(1);
        }
        return next;
    }
}
