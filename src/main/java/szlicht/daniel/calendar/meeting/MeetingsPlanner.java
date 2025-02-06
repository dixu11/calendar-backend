package szlicht.daniel.calendar.meeting;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.LocalDateUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static szlicht.daniel.calendar.common.GoogleCalendarClient.toDateTime;
import static szlicht.daniel.calendar.common.GoogleCalendarClient.toLocalDateTime;

@Service
public class MeetingsPlanner {
    private static final String CALENDAR_OTHER_ID = "primary";
    private static final String CALENDAR_MEETINGS_ID = "8jl5qj89qrqreh2ir4k24ole94@group.calendar.google.com";
    private static final LocalTime WORK_START = LocalTime.of(11, 30);
    private static final LocalTime WORK_END = LocalTime.of(16, 0);
    private final Calendar calendar;
    private Set<Event> events =
            new TreeSet<>(Comparator.comparingLong(event -> event.getStart().getDateTime().getValue()));

    public MeetingsPlanner(Calendar calendar) {
        this.calendar = calendar;
    }

    //generating suggestions ------------------------------

    public Propositions getMeetingSuggestions(int timeMinutes) {
        update();
        Map<LocalDate, List<Event>> allEvents = sortEventsByDays();
        Map<LocalDate, Meeting> result = new TreeMap<>(LocalDate::compareTo);
        for (LocalDate date : allEvents.keySet()) {
            Optional<Meeting> meeting = getMeetingPropositionsFor(allEvents.get(date), timeMinutes, date);
            meeting.ifPresent(value -> result.put(date, value));
        }
        return new Propositions(new ArrayList<>(result.values()));
    }

    private Map<LocalDate, List<Event>> sortEventsByDays() {
        Map<LocalDate, List<Event>> result = new TreeMap<>(LocalDate::compareTo);
        for (LocalDate date : LocalDateUtils.getDatesBetweenInclude(LocalDate.now(), LocalDate.now().plusMonths(1))) {
            List<Event> eventsForThisDay = events.stream()
                    .filter(event -> toLocalDateTime(event.getStart().getDateTime()).toLocalDate().equals(date))
                    .sorted(Comparator.comparingLong(event -> -event.getStart().getDateTime().getValue()))
                    .toList();
            result.put(date, eventsForThisDay);
        }
        return result;
    }

    private Optional<Meeting> getMeetingPropositionsFor(List<Event> events, int minutes,LocalDate date) {
        Meeting proposition = new Meeting(date.atTime(WORK_END).minusMinutes(minutes),date.atTime(WORK_END));
        for (Event event : events) {
            Meeting otherMeeting = new Meeting(event);
            if (!proposition.collideWith(otherMeeting)) {
                return Optional.of(proposition);
            }
            proposition = Meeting.createBefore(otherMeeting,minutes);
            if (proposition.getStart().toLocalTime().isBefore(WORK_START)) {
                return Optional.empty();
            }
        }
        return Optional.of(proposition);
    }

    //collecting events ----------------------------

    private void update() {
        events.clear();
        addTimedEvents(getEvents(CALENDAR_MEETINGS_ID));
        addTimedEvents(getEvents(CALENDAR_OTHER_ID));
    }

    private void addTimedEvents(Events newEvents) {
        List<Event> timedEvents = newEvents.getItems().stream()
                .filter(event -> event.getStart().getDateTime() != null)
                .toList();
        events.addAll(timedEvents);
    }

    private Events getEvents(String calendarId)  {
        try{
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime monthEnd = now.plusMonths(1);
            return calendar.events().list(calendarId)
                    .setMaxResults(100)
                    .setTimeMin(toDateTime(now))
                    .setTimeMax(toDateTime(monthEnd))
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
        }catch (IOException e){
            e.printStackTrace();
            throw new CalendarOfflineException(e.getMessage());
        }

    }
}
