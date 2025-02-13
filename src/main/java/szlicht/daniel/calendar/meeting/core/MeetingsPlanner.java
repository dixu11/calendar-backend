package szlicht.daniel.calendar.meeting.core;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.java.LocalDateUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static szlicht.daniel.calendar.common.calendar.GoogleCalendarUtils.toDateTime;
import static szlicht.daniel.calendar.common.calendar.GoogleCalendarUtils.toLocalDateTime;
import static szlicht.daniel.calendar.common.spring.SpringUtils.params;

@Service
class MeetingsPlanner {
    private static final String CALENDAR_OTHER_ID = "primary";
    private static final String CALENDAR_MEETINGS_ID = "8jl5qj89qrqreh2ir4k24ole94@group.calendar.google.com";
    private final Calendar calendar;

    MeetingsPlanner(Calendar calendar) {
        this.calendar = calendar;
    }
    //generating suggestions ------------------------------

    Propositions getMeetingSuggestions(int timeMinutes) {
        getMonthlyEvents();
        Map<LocalDate, List<Meeting>> allMeetings = sortMeetingsByDays();
        Map<LocalDate, Meeting> result = new TreeMap<>(LocalDate::compareTo);
        for (LocalDate date : allMeetings.keySet()) {
            Optional<Meeting> meeting = getMeetingPropositionsFor(allMeetings.get(date), timeMinutes, date);
            meeting.ifPresent(value -> result.put(date, value));
        }
        return new Propositions(new ArrayList<>(result.values()), timeMinutes / 60.0);
    }

    private Map<LocalDate, List<Meeting>> sortMeetingsByDays() {
        Map<LocalDate, List<Meeting>> result = new TreeMap<>(LocalDate::compareTo);
        Set<Meeting> events = getMonthlyEvents();
        for (LocalDate date : LocalDateUtils.getDatesBetweenInclude(
                firstDay().toLocalDate(),
                lastDay().toLocalDate())) {
            List<Meeting> eventsForThisDay = events.stream()
                    .filter(meeting -> meeting.getStart().toLocalDate().equals(date))
                    .sorted((meeting1, meeting2) -> -meeting1.compareTo(meeting2))
                    .toList();
            result.put(date, eventsForThisDay);
        }
        return result;
    }

    private Optional<Meeting> getMeetingPropositionsFor(List<Meeting> meetings, int minutes, LocalDate date) {
        var workHours = params.values().workHours().forDay(date.getDayOfWeek());
        Meeting proposition = new Meeting(date.atTime(workHours.end()).minusMinutes(minutes), date.atTime(workHours.end()));
        for (Meeting otherMeeting : meetings) {
            if (!proposition.collideWith(otherMeeting)) {
                continue;
            }
            proposition = Meeting.createBefore(otherMeeting, minutes);
            if (proposition.getStart().toLocalTime().isBefore(workHours.start())) {
                return Optional.empty();
            }
        }
        return Optional.of(proposition);
    }

    //arrange new meetings -------------------------

    void arrange(Meeting meeting) {
        Event event = meeting.asEvent();
        try {
            calendar.events().insert(CALENDAR_MEETINGS_ID, event).execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //collecting events ----------------------------

    private Set<Meeting> getMonthlyEvents() {
        Set<Meeting> meetings = new TreeSet<>();
        meetings.addAll(getMeetings(CALENDAR_MEETINGS_ID));
        meetings.addAll(getMeetings(CALENDAR_OTHER_ID));
        return meetings;
    }

    private List<Meeting> getMeetings(String calendarId) {
        return getTimedEvents(getEvents(calendarId))
                .stream()
                .map(Meeting::new)
                .toList();

    }

    private List<Event> getTimedEvents(Events newEvents) {
        return newEvents.getItems().stream()
                .filter(event -> event.getStart().getDateTime() != null)
                .toList();
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
