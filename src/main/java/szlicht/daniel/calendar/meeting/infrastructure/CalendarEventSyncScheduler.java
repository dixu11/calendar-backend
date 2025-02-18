package szlicht.daniel.calendar.meeting.infrastructure;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;
import szlicht.daniel.calendar.meeting.app_core.CalendarRepository;
import szlicht.daniel.calendar.meeting.app_core.NextMonthMeetingsEvent;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

@Service
public class CalendarEventSyncScheduler {
    private TaskScheduler taskScheduler;
    private CalendarRepository calendarRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CalendarEventSyncScheduler(TaskScheduler taskScheduler, CalendarRepository calendarRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.taskScheduler = taskScheduler;
        this.calendarRepository = calendarRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @EventListener
    void atStart(AppStartedEvent e) {
        Runnable runnable = () -> applicationEventPublisher.publishEvent(
                new NextMonthMeetingsEvent(calendarRepository.getMonthFromNowMeetings()));
        runnable.run();
        taskScheduler.schedule(runnable,
                new CronTrigger("0 45 15 * * ?", params.values().getZoneId()));
    }
}
