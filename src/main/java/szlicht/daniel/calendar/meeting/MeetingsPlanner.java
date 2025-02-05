package szlicht.daniel.calendar.meeting;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import szlicht.daniel.calendar.common.LocalDateUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static szlicht.daniel.calendar.common.GoogleCalendarClient.toLocalDateTime;

public class MeetingsPlanner {
    private static final LocalTime WORK_START = LocalTime.of(11, 30);
    private static final LocalTime WORK_END = LocalTime.of(16, 0);
    private static final int MEETING_TIME = 60;
    private static final int BUFFER = 15;

    private Set<Event> events =
            new TreeSet<>(Comparator.comparingLong(event -> event.getStart().getDateTime().getValue()));

    public void addTimedEvents(Events newEvents) {
        List<Event> timedEvents = newEvents.getItems().stream()
                .filter(event -> event.getStart().getDateTime() != null)
                .toList();
        events.addAll(timedEvents);
    }

    private Map<LocalDate, Meeting> getMeetingSuggestions() {
        Map<LocalDate, List<Event>> allEvents = sortEventsByDays();
        Map<LocalDate, Meeting> result = new TreeMap<>(LocalDate::compareTo);
        for (LocalDate date : allEvents.keySet()) {
            Optional<Meeting> meeting = getMeetingPropositionsFor(allEvents.get(date), MEETING_TIME, date);
            meeting.ifPresent(value -> result.put(date, value));
            System.out.printf("for date %s meeting: %s\n", date, meeting);
        }
        return result;
    }

    private Optional<Meeting> getMeetingPropositionsFor(List<Event> events, int minutes,LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return Optional.empty();
        }
        Meeting proposition = createMeetingAt(date.atTime(WORK_END).minusMinutes(minutes),minutes);
        for (Event event : events) {
            Meeting otherMeeting = new Meeting(event);
            if (!proposition.collideWith(otherMeeting,15)) { //todo margins!
                return Optional.of(proposition);
            }
            proposition = createMeetingAt(otherMeeting.getStart().minusMinutes(minutes + BUFFER), minutes);
            if (proposition.getStart().toLocalTime().isBefore(WORK_START)) {
                return Optional.empty();
            }
        }
        return Optional.of(proposition);
    }

    private Meeting createMeetingAt(LocalDateTime time, int minutes) {
        return new Meeting(time, time.plusMinutes(minutes));
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

    public void print() {
        getMeetingSuggestions();
    }
}
