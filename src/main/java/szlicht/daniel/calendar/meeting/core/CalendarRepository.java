package szlicht.daniel.calendar.meeting.core;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static szlicht.daniel.calendar.common.calendar.GoogleCalendarUtils.toDateTime;
import static szlicht.daniel.calendar.common.java.LocalDateUtils.nextMonthEnd;
import static szlicht.daniel.calendar.common.java.LocalDateUtils.tomorrowStart;

public class CalendarRepository {

    private static final String CALENDAR_OTHER_ID = "primary";
    private static final String CALENDAR_MEETINGS_ID = "8jl5qj89qrqreh2ir4k24ole94@group.calendar.google.com";

    private Calendar calendar;

    public CalendarRepository(Calendar calendar) {
        this.calendar = calendar;
    }

    void arrange(Meeting meeting) {
        Event event = meeting.asEvent();
        try {
            calendar.events().insert(CALENDAR_MEETINGS_ID, event).execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    Set<Meeting> getMonthRangeMeetings() {
        LocalDateTime from = tomorrowStart();
        LocalDateTime to = nextMonthEnd();
        return getMeetings(from,to);
    }

    Set<Meeting> getTodayMeetings(){
        LocalDateTime from = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime to = LocalDateTime.now().with(LocalTime.MAX);
        return new TreeSet<>(getMeetings(from, to));
    }

    private Set<Meeting> getMeetings(LocalDateTime from, LocalDateTime to) {
        Set<Meeting> meetings = new TreeSet<>();
        meetings.addAll(getOneCalendarMeetings(CALENDAR_MEETINGS_ID,from,to));
        meetings.addAll(getOneCalendarMeetings(CALENDAR_OTHER_ID, from, to));
        return meetings;
    }

    private List<Meeting> getOneCalendarMeetings(String calendarId, LocalDateTime from, LocalDateTime to) {
        return getTimedEvents(getEvents(calendarId,from,to))
                .stream()
                .map(Meeting::new)
                .toList();

    }

    private List<Event> getTimedEvents(Events newEvents) {
        return newEvents.getItems().stream()
                .filter(event -> event.getStart().getDateTime() != null)
                .toList();
    }

    private Events getEvents(String calendarId,LocalDateTime from, LocalDateTime to) {
        try {
            return calendar.events().list(calendarId)
                    .setMaxResults(100)
                    .setTimeMin(toDateTime(from))
                    .setTimeMax(toDateTime(to))
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CalendarOfflineException(e.getMessage());
        }
    }

}
