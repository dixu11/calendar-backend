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
import static szlicht.daniel.calendar.common.java.LocalDateUtils.nextMonthEnd;
import static szlicht.daniel.calendar.common.java.LocalDateUtils.tomorrowStart;
import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

@Service
class CalendarManager {

    private final CalendarRepository calendarRepository;

    CalendarManager( CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }
    //generating suggestions ------------------------------

    Propositions getMeetingSuggestions(int timeMinutes) {
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
        Set<Meeting> events = calendarRepository.getMonthRangeMeetings();
        for (LocalDate date : LocalDateUtils.getDatesBetweenInclude(
                tomorrowStart().toLocalDate(),
                nextMonthEnd().toLocalDate())) {
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

    public void arrange(Meeting meeting) {
        calendarRepository.arrange(meeting);
    }

    public Set<Meeting> getTodayMeetings() {
        return calendarRepository.getTodayMeetings();
    }
}
