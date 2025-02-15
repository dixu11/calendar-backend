package szlicht.daniel.calendar.meeting.appCore;

import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.java.LocalDateUtils;

import java.time.LocalDate;
import java.util.*;

import static szlicht.daniel.calendar.common.java.LocalDateUtils.nextMonthEnd;
import static szlicht.daniel.calendar.common.java.LocalDateUtils.tomorrowStart;
import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

@Service
class PropositionsDomainService {

    private final CalendarRepository calendarRepository;

    PropositionsDomainService(CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }

    Propositions createMeetingPropositions(Integer minutes) {
        if (minutes == null) {
            minutes = params.values().minutes();
        }
        if (notAcceptableLength(minutes)) {
            throw new IllegalArgumentException(
                    String.format("Only length of %s hours is acceptable for automatic meeting with me.",
                            params.values().hours())
            );
        }
        Map<LocalDate, List<Meeting>> allMeetings = sortMeetingsByDaysReverse();
        Map<LocalDate, Meeting> result = new TreeMap<>(LocalDate::compareTo);
        for (LocalDate date : allMeetings.keySet()) {
            Optional<Meeting> meeting = getMeetingPropositionsFor(allMeetings.get(date), minutes, date);
            meeting.ifPresent(value -> result.put(date, value));
        }
        return new Propositions(new ArrayList<>(result.values()), minutes / 60.0);
    }

    private Map<LocalDate, List<Meeting>> sortMeetingsByDaysReverse() {
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

    private boolean notAcceptableLength(int minutes) {
        return params.values().hours().stream()
                .mapToInt(hour -> (int) (hour * 60))
                .noneMatch(minutesAccepted -> minutesAccepted == minutes);
    }
}
