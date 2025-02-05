package szlicht.daniel.calendar.meeting;

import com.google.api.services.calendar.model.Events;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import com.google.api.services.calendar.Calendar;

import java.io.IOException;
import java.time.LocalDateTime;

import static szlicht.daniel.calendar.common.GoogleCalendarClient.toDateTime;

@Service
public class CalendarService {

    private static final String CALENDAR_OTHER_ID = "primary";
    private static final String CALENDAR_MEETINGS_ID = "8jl5qj89qrqreh2ir4k24ole94@group.calendar.google.com";


    private final Calendar calendar;

    public CalendarService(Calendar calendar) {
        this.calendar = calendar;
    }

    @PostConstruct
    public void printUpcomingEvents() throws IOException {
        MeetingsPlanner meetings = new MeetingsPlanner();
        meetings.addTimedEvents(getEvents(CALENDAR_MEETINGS_ID));
        meetings.addTimedEvents(getEvents(CALENDAR_OTHER_ID));

        meetings.print();
       /* for (Event event : events.getItems()) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                start = event.getStart().getDate();
            }
            System.out.printf("%s (%s)\n", event.getSummary(), start);
        }*/
    }

    private Events getEvents(String calendarId) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthEnd = now.plusMonths(1);
        return calendar.events().list(calendarId)
                .setMaxResults(100)
                .setTimeMin(toDateTime(now))
                .setTimeMax(toDateTime(monthEnd))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
    }




}
