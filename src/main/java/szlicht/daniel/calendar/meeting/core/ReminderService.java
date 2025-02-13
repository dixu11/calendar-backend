package szlicht.daniel.calendar.meeting.core;

import com.google.api.services.calendar.Calendar;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.spring.MyTaskScheduler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
public class ReminderService implements ApplicationListener<ApplicationReadyEvent> {

    private TaskScheduler taskScheduler;
    private Calendar calendar;

    public ReminderService( TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        taskScheduler.schedule(() -> System.out.println("Zaczynamy zabawę ze springowym"),LocalDateTime.now().plusSeconds(30).atZone(ZoneId.systemDefault()).toInstant());

    }
}
