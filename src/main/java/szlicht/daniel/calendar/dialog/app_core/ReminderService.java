package szlicht.daniel.calendar.dialog.app_core;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;
import szlicht.daniel.calendar.meeting.app_core.CalendarRepository;
import szlicht.daniel.calendar.meeting.app_core.Meeting;
import szlicht.daniel.calendar.meeting.app_core.MeetingType;

import java.time.LocalDateTime;
import java.util.Set;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

@Service
class ReminderService  {

    private static final int NOTIFY_MINUTES_BEFORE_MEETING_END = 15;

    private final TaskScheduler taskScheduler;
    private final CalendarRepository calendarRepository;
    private final DialogAppService dialogAppService;

    ReminderService(TaskScheduler taskScheduler, CalendarRepository calendarRepository, DialogAppService dialogAppService) {
        this.taskScheduler = taskScheduler;
        this.calendarRepository = calendarRepository;
        this.dialogAppService = dialogAppService;
    }

    @EventListener
    public void onStart(AppStartedEvent appStartedEvent) {
        Set<Meeting> todayMeetings = calendarRepository.getTodayMeetings();
        for (Meeting todayMeeting : todayMeetings) {
            LocalDateTime notificationTime = todayMeeting.getEnd().minusMinutes(NOTIFY_MINUTES_BEFORE_MEETING_END);
            if (todayMeeting.getType() == MeetingType.CYCLIC_MENTORING) {
                continue;
            }
            if (notificationTime.isBefore(LocalDateTime.now())) {
                continue;
            }
            if (todayMeeting.getDetails().getEmail().isBlank()) {
                continue;
            }
            if (!todayMeeting.isMentoring()) {
                continue;
            }
            taskScheduler.schedule(() -> dialogAppService.startNextPropositionsScenario(
                            todayMeeting.getLengthMinutes(),
                            todayMeeting.getDetails().getEmail()),
                    notificationTime.atZone(params.values().getZoneId()).toInstant());
            System.out.println("Will send notification at " + notificationTime + " for event " + todayMeeting);
        }
    }
}
