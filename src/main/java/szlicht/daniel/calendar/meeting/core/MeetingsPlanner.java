package szlicht.daniel.calendar.meeting.core;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.Events;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.GoogleCalendarColor;
import szlicht.daniel.calendar.common.LocalDateUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static szlicht.daniel.calendar.common.GoogleCalendarClient.*;

@Service
class MeetingsPlanner {
    private static final String CALENDAR_OTHER_ID = "primary";
    private static final String CALENDAR_MEETINGS_ID = "8jl5qj89qrqreh2ir4k24ole94@group.calendar.google.com";
    private static final LocalTime WORK_START = LocalTime.of(11, 15);
    private static final LocalTime WORK_END = LocalTime.of(15, 45);
    private final Calendar calendar;
    private Set<Event> events =
            new TreeSet<>(Comparator.comparingLong(event -> event.getStart().getDateTime().getValue()));

    MeetingsPlanner(Calendar calendar) {
        this.calendar = calendar;
    }

    //generating suggestions ------------------------------

    Propositions getMeetingSuggestions(int timeMinutes) {
        update();
        Map<LocalDate, List<Event>> allEvents = sortEventsByDays();
        Map<LocalDate, Meeting> result = new TreeMap<>(LocalDate::compareTo);
        for (LocalDate date : allEvents.keySet()) {
            Optional<Meeting> meeting = getMeetingPropositionsFor(allEvents.get(date), timeMinutes, date);
            meeting.ifPresent(value -> result.put(date, value));
        }
        return new Propositions(new ArrayList<>(result.values()), timeMinutes / 60.0);
    }

    private Map<LocalDate, List<Event>> sortEventsByDays() {
        Map<LocalDate, List<Event>> result = new TreeMap<>(LocalDate::compareTo);
        for (LocalDate date : LocalDateUtils.getDatesBetweenInclude(
                firstDay().toLocalDate(),
                lastDay().toLocalDate())) {
            List<Event> eventsForThisDay = events.stream()
                    .filter(event -> toLocalDateTime(event.getStart().getDateTime()).toLocalDate().equals(date))
                    .sorted(Comparator.comparingLong(event -> -event.getStart().getDateTime().getValue()))
                    .toList();
            result.put(date, eventsForThisDay);
        }
        return result;
    }

    private Optional<Meeting> getMeetingPropositionsFor(List<Event> events, int minutes, LocalDate date) {
        Meeting proposition = new Meeting(date.atTime(WORK_END).minusMinutes(minutes), date.atTime(WORK_END));
        for (Event event : events) {
            Meeting otherMeeting = new Meeting(event);
            if (!proposition.collideWith(otherMeeting)) {
                continue;
            }
            proposition = Meeting.createBefore(otherMeeting, minutes);
            if (proposition.getStart().toLocalTime().isBefore(WORK_START)) {
                return Optional.empty();
            }
        }
        return Optional.of(proposition);
    }

    //arrange new meetings -------------------------

    void arrange(Meeting meeting) {
        Event event = meeting.asEvent();
        try {
            Event created = calendar.events().insert(CALENDAR_MEETINGS_ID, event).execute();
            System.out.println("Utworzono zdarzenie: " + created.getHtmlLink());
        } catch (IOException e) {
            e.printStackTrace();
            throw new CalendarOfflineException("Nie udało się wstawić spotkania do kalendarza: " + e.getMessage());
        }
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

    private Events getEvents(String calendarId) {
        try {
            LocalDateTime timeMin = firstDay();
            LocalDateTime timeMax = lastDay();
            return calendar.events().list(calendarId)
                    .setMaxResults(100)
                    .setTimeMin(toDateTime(timeMin))
                    .setTimeMax(toDateTime(timeMax))
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CalendarOfflineException(e.getMessage());
        }
    }

    private LocalDateTime firstDay() {
        return LocalDateTime.now().plusDays(1).with(LocalTime.MIN);
    }

    private LocalDateTime lastDay() {
        return LocalDateTime.now().plusMonths(1).with(LocalTime.MAX);
    }
}
