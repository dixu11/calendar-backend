package szlicht.daniel.calendar.meeting.appCore;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@Service
class ReminderService  {

    private static final int NOTIFY_MINUTES_BEFORE_MEETING_END = 15;

    private final TaskScheduler taskScheduler;
    private final PropositionsDomainService propositionsDomainService;
    private final CalendarService calendarService;

    ReminderService(TaskScheduler taskScheduler, PropositionsDomainService propositionsDomainService, CalendarService calendarService) {
        this.taskScheduler = taskScheduler;
        this.propositionsDomainService = propositionsDomainService;
        this.calendarService = calendarService;
    }

    @EventListener
    public void onStart(AppStartedEvent appStartedEvent) {
        Set<Meeting> todayMeetings = propositionsDomainService.getTodayMeetings();
        for (Meeting todayMeeting : todayMeetings) {
            LocalDateTime notificationTime = todayMeeting.getEnd().minusMinutes(NOTIFY_MINUTES_BEFORE_MEETING_END);
            if (notificationTime.isBefore(LocalDateTime.now())) {
                continue;
            }
            if (todayMeeting.getMail().isBlank()) {
                continue;
            }
            if (!todayMeeting.isMentoring()) {
                continue;
            }
            taskScheduler.schedule(() -> calendarService.sendPropositions(
                            todayMeeting.getLengthMinutes(),
                            todayMeeting.getMail()),
                    notificationTime.atZone(ZoneId.systemDefault()).toInstant());
            System.out.println("Will send notification at " + notificationTime + " for event " + todayMeeting);
        }
    }
}
