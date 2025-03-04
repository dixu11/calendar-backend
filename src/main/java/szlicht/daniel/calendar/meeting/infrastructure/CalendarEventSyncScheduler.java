package szlicht.daniel.calendar.meeting.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;
import szlicht.daniel.calendar.common.spring.Logger;
import szlicht.daniel.calendar.meeting.app_core.CalendarRepository;
import szlicht.daniel.calendar.meeting.app_core.Meeting;
import szlicht.daniel.calendar.meeting.app_core.NextMonthMeetingsEvent;
import szlicht.daniel.calendar.meeting.app_core.TodayMeetingsEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

@Slf4j
@Service
public class CalendarEventSyncScheduler {
    private TaskScheduler taskScheduler;
    private CalendarRepository calendarRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private Logger logger;

    public CalendarEventSyncScheduler(TaskScheduler taskScheduler, CalendarRepository calendarRepository, ApplicationEventPublisher applicationEventPublisher, Logger logger) {
        this.taskScheduler = taskScheduler;
        this.calendarRepository = calendarRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.logger = logger;
    }

    @EventListener
    void startDailyUpdates(AppStartedEvent e) {
        Runnable runnable = () -> {
            Set<Meeting> monthFromNowMeetings = calendarRepository.getMonthFromNowMeetings();
            List<Meeting> todayMeetings = monthFromNowMeetings.stream()
                    .filter(meeting -> meeting.getStart().toLocalDate().equals(LocalDate.now()))
                    .toList();

            applicationEventPublisher.publishEvent(
                    new NextMonthMeetingsEvent(monthFromNowMeetings));
            applicationEventPublisher.publishEvent(
                    new TodayMeetingsEvent(todayMeetings)
            );
        };
        taskScheduler.schedule(runnable,
                new CronTrigger("0 0 8 * * ?", params.values().getZoneId()));
        runnable.run();
    }
}
