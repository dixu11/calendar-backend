package szlicht.daniel.calendar.common;

import java.time.LocalDate;
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
}
