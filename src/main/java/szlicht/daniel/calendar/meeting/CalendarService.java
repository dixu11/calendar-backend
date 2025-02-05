package szlicht.daniel.calendar.meeting;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.GoogleCalendarClient;
import com.google.api.services.calendar.Calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalendarService {

    @PostConstruct
    public void printUpcomingEvents() throws GeneralSecurityException, IOException {
        Calendar calendar = GoogleCalendarClient.getCalendarService();
        DateTime now = new DateTime(System.currentTimeMillis());
        Calendar.CalendarList.List request = calendar.calendarList().list();
        CalendarList calendarList = request.execute();
        for (CalendarListEntry entry : calendarList.getItems()) {
            System.out.printf("Kalendarz: %s, ID: %s\n", entry.getSummary(), entry.getId());
        }
        Events events = calendar.events().list("8jl5qj89qrqreh2ir4k24ole94@group.calendar.google.com")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("Brak zbliżających się wydarzeń.");
        }
        System.out.println("Nadchodzące wydarzenia:");
        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                start = event.getStart().getDate();
            }
            System.out.printf("%s (%s)\n", event.getSummary(), start);
        }

    }
}
