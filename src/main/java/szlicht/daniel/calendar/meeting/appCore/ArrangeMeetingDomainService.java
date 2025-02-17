package szlicht.daniel.calendar.meeting.appCore;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ArrangeMeetingDomainService {

    private static final int WARNING_WHEN_ARRANGED = 5;
    private static final int SHUT_DOWN_WHEN_ARRANGED = 10;
    private static final int ARRANGE_RESTART_EVERY = 60;

    private final PropositionsDomainService propositionsDomainService;
    private final CalendarRepository calendarRepository;
    private final WarningLogger warningLogger;
    private boolean canArrange = true;
    private int arrangedLastHour;
    private LocalDateTime arrangeCounterLastRestart = LocalDateTime.now();

    public ArrangeMeetingDomainService(PropositionsDomainService propositionsDomainService, CalendarRepository calendarRepository, WarningLogger warningLogger) {
        this.propositionsDomainService = propositionsDomainService;
        this.calendarRepository = calendarRepository;
        this.warningLogger = warningLogger;
    }

    public void arrange(Meeting meeting) {
        validate(meeting);
        calendarRepository.save(meeting);
        warningLogger.notifyOwner("Umówił się: " + meeting.getDetails().getMail() + " at "+ meeting.when(),
                "Użytkownik dodał się do kalendarza",false);
        preventTooManyArrangments();
    }

    private void validate(Meeting meeting) {
        if (!canArrange) {
            throw new CalendarOfflineException("Nie mogę umówić Twojego spotkania. " +
                    "Serwis chwilowo niedostępny, spróbuj za kilka godzin.");
        }
        Propositions propositions = propositionsDomainService.createMeetingPropositions(meeting.getLengthMinutes());
        if (propositions.isNotValid(meeting)) {
            throw new MeetingCollisionException("Zdaje się, że termin jest już zajęty :( " +
                    "Pobierz jeszcze raz aktualne terminy i spróbuj umówić się na inny dostępny termin!");
        }
    }

    private void preventTooManyArrangments() {
        arrangedLastHour++;
        if (arrangeCounterLastRestart.until(LocalDateTime.now(), ChronoUnit.MINUTES) >= ARRANGE_RESTART_EVERY) {
            arrangeCounterLastRestart = LocalDateTime.now();
            arrangedLastHour = 0;
        }
        if (arrangedLastHour >= SHUT_DOWN_WHEN_ARRANGED) {
            canArrange = false;
            String subject = "Max arrangements in hour reached";
            String message = "Performing arrangement system shutdown";
            warningLogger.notifyOwner(subject, message, true);
        }
        if (arrangedLastHour >= WARNING_WHEN_ARRANGED) {
            String subject = "Big number of arrangements last hour:" + arrangedLastHour;
            String message = "have a nice day!";
            warningLogger.notifyOwner(subject, message, false);
        }
    }
}
