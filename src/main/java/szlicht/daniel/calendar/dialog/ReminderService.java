package szlicht.daniel.calendar.dialog;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.meeting.CalendarRepository;
import szlicht.daniel.calendar.meeting.Meeting;
import szlicht.daniel.calendar.meeting.MeetingType;
import szlicht.daniel.calendar.meeting.TodayMeetingsEvent;

import java.time.Instant;
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
    public void sendLessonEndNextPropositions(TodayMeetingsEvent event) {
        Set<Meeting> todayMeetings = calendarRepository.getTodayMeetings();
        for (Meeting todayMeeting : todayMeetings) {
            LocalDateTime notificationTime = todayMeeting.getEnd().minusMinutes(NOTIFY_MINUTES_BEFORE_MEETING_END);
            Instant instant = notificationTime.atZone(params.values().getZoneId()).toInstant();
            if (todayMeeting.getType() == MeetingType.CYCLIC_MENTORING) {
                continue;
            }
            if (notificationTime.isBefore(LocalDateTime.now())) {
                continue;
            }
            if (todayMeeting.getDetails().getEmail().isBlank()) {
                continue;
            }
            if (todayMeeting.getType() == MeetingType.FIRST_MENTORING) {
                taskScheduler.schedule(() -> dialogAppService.startFirstLessonEndPropositionsScenario(
                        todayMeeting.getDetails().getEmail()),
                        instant
                );
                System.out.println("Will send notification (first lesson) at " + notificationTime + " for event " + todayMeeting);
                continue;
            }
            if (!todayMeeting.isMentoring()) {
                continue;
            }
            taskScheduler.schedule(() -> dialogAppService.startNextPropositionsScenario(
                            todayMeeting.getLengthMinutes(),
                            todayMeeting.getDetails().getEmail()),
                    instant);
            System.out.println("Will send notification at " + notificationTime + " for event " + todayMeeting);
        }
    }
}
