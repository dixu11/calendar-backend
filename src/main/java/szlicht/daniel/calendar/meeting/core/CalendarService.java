package szlicht.daniel.calendar.meeting.core;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static szlicht.daniel.calendar.common.spring.SpringUtils.getParams;
import static szlicht.daniel.calendar.meeting.Params.DEFAULT_MEETING_LENGTH_MINUTES;

@Service
class CalendarService {



    private final MeetingsPlanner meetingsPlanner;
    private final WarningService warningService;

    //max arrangements per hour for safety
    private static final int ARRANGE_RESTART_EVERY = 60;
    private static final int WARNING_WHEN_ARRANGED = 5;
    private static final int SHUT_DOWN_WHEN_ARRANGED = 10;
    private int arrangedLastHour;
    private LocalDateTime arrangeCounterLastRestart = LocalDateTime.now();
    private boolean canArrange = true;

    CalendarService(MeetingsPlanner meetingsPlanner, WarningService warningService) {
        this.meetingsPlanner = meetingsPlanner;
        this.warningService = warningService;
    }

    Propositions getMeetingPropositions(Integer minutesLength) {
        if (minutesLength == null) {
            minutesLength = DEFAULT_MEETING_LENGTH_MINUTES;
        }
        if (notAcceptableLength(minutesLength)) {
            throw new IllegalArgumentException(
                    String.format("Only length of %s hours is acceptable for automatic meeting with me.",
                            getParams().params().hours())
            );
        }
        return meetingsPlanner.getMeetingSuggestions(minutesLength);
    }

    void arrangeMeeting(Meeting meeting) {
        if (!canArrange) {
            throw new CalendarOfflineException("Nie mogę umówić Twojego spotkania. " +
                    "Serwis chwilowo niedostępny, spróbuj za kilka godzin.");
        }
        Propositions propositions = getMeetingPropositions(meeting.getLengthMinutes());
        if (propositions.isNotValid(meeting)) {
            throw new MeetingCollisionException("Zdaje się, że termin jest już zajęty :( " +
                    "Pobierz jeszcze raz aktualne terminy i spróbuj umówić się na inny dostępny termin!");
        }
        meetingsPlanner.arrange(meeting);
        warningService.notifyOwner(meeting.getMail() + " at "+ meeting.when(),"Użytkownik dodał się do kalendarza",false);
        arrangedLastHour++;
        if (arrangeCounterLastRestart.until(LocalDateTime.now(), ChronoUnit.MINUTES) >= ARRANGE_RESTART_EVERY) {
            arrangeCounterLastRestart = LocalDateTime.now();
            arrangedLastHour = 0;
        }
        if (arrangedLastHour >= SHUT_DOWN_WHEN_ARRANGED) {
            canArrange = false;
            String subject = "Max arrangements in hour reached";
            String message = "Performing arrangement system shutdown";
            warningService.notifyOwner(subject, message, true);
        }
        if (arrangedLastHour >= WARNING_WHEN_ARRANGED) {
            String subject = "Big number of arrangements last hour:" + arrangedLastHour;
            String message = "have a nice day!";
            warningService.notifyOwner(subject, message, false);
        }
    }

    private boolean notAcceptableLength(int minutes) {
        return getParams().params().hours().stream()
                .mapToInt(hour -> (int) (hour * 60))
                .noneMatch(minutesAccepted -> minutesAccepted == minutes);
    }
}
