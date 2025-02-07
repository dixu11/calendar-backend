package szlicht.daniel.calendar.meeting.core;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static szlicht.daniel.calendar.common.LocalDateUtils.nextMonday;

class Propositions {
    private List<Meeting> meetings;
    private double hours;

    Propositions(List<Meeting> meetings, double hours) {
        this.meetings = meetings;
        this.hours = hours;
    }

    Propositions() {
    }

    public double getHours() {
        return hours;
    }

    List<Meeting> getFirstWeek() {
        return getBetweenExclude(LocalDate.now().minusDays(1), nextMonday(LocalDate.now()));
    }

    List<Meeting> getNextWeek() {
        return getBetweenExclude(nextMonday(LocalDate.now()).minusDays(1),
                nextMonday(nextMonday(LocalDate.now())));
    }

    List<Meeting> getAfterNextWeek(){
        return getBetweenExclude(nextMonday(nextMonday(LocalDate.now())).minusDays(1),
                LocalDate.now().plusYears(100));
    }

    List<Meeting> getBetweenExclude(LocalDate start, LocalDate end) {
       return meetings.stream()
                .filter(meeting -> meeting.getStart().toLocalDate().isAfter(start))
                .filter(meeting -> meeting.getEnd().toLocalDate().isBefore(end))
                .toList();
    }



    private Map<LocalDate,Meeting> getMinimum(Map<LocalDate, Meeting> meetings, LocalDate minimumDate){
        return meetings.entrySet().stream()
                .filter(entry -> entry.getKey().isAfter(minimumDate)|| entry.getKey().isEqual(minimumDate))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    List<Meeting> getMeetings() {
        return meetings;
    }
}
