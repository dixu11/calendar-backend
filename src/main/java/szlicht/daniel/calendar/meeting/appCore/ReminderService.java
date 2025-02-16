package szlicht.daniel.calendar.meeting.appCore;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;
import szlicht.daniel.calendar.meeting.infrastructure.GoogleCalendarRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@Service
class ReminderService  {

    private static final int NOTIFY_MINUTES_BEFORE_MEETING_END = 15;

    private final TaskScheduler taskScheduler;
    private final PropositionsDomainService propositionsDomainService;
    private final CalendarAppService calendarAppService;
    private final CalendarRepository calendarRepository;

    ReminderService(TaskScheduler taskScheduler, PropositionsDomainService propositionsDomainService, CalendarAppService calendarAppService, CalendarRepository calendarRepository) {
        this.taskScheduler = taskScheduler;
        this.propositionsDomainService = propositionsDomainService;
        this.calendarAppService = calendarAppService;
        this.calendarRepository = calendarRepository;
    }

    @EventListener
    public void onStart(AppStartedEvent appStartedEvent) {
        Set<Meeting> todayMeetings = calendarRepository.getTodayMeetings();
        for (Meeting todayMeeting : todayMeetings) {
            LocalDateTime notificationTime = todayMeeting.getEnd().minusMinutes(NOTIFY_MINUTES_BEFORE_MEETING_END);
            if (notificationTime.isBefore(LocalDateTime.now())) {
                continue;
            }
            if (todayMeeting.getDetails().getMail().isBlank()) {
                continue;
            }
            if (!todayMeeting.isMentoring()) {
                continue;
            }
            System.out.println(ZoneId.systemDefault());
            taskScheduler.schedule(() -> calendarAppService.sendPropositions(
                            todayMeeting.getLengthMinutes(),
                            todayMeeting.getDetails().getMail()),
                    notificationTime.atZone(ZoneId.systemDefault()).toInstant());
            System.out.println("Will send notification at " + notificationTime + " for event " + todayMeeting);
        }
    }
}
