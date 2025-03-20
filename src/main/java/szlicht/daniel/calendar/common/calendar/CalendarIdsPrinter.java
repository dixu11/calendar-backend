package szlicht.daniel.calendar.common.calendar;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.meeting.CalendarOfflineException;

import java.io.IOException;
import java.util.List;

@Component
@Profile("dev")
public class CalendarIdsPrinter {
    private Calendar calendar;

    public CalendarIdsPrinter(Calendar calendar) {
        this.calendar = calendar;
    }

    @PostConstruct
    public List<String> getCalendarNames() {
        System.err.println("loading ids");
        try {
            CalendarList calendarList = calendar.calendarList().list().execute();
            List<String> result = calendarList.getItems().stream()
                    .map(entry -> "<" +entry.getSummary()+":"+entry.getId()+ ">")
                    .toList();
            System.out.println(result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new CalendarOfflineException(e.getMessage());
        }
    }
}
