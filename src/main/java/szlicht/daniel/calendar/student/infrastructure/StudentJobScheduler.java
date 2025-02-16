package szlicht.daniel.calendar.student.infrastructure;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;
import szlicht.daniel.calendar.student.appCore.StudentAppService;

import java.time.ZoneId;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

@Service
public class StudentJobScheduler {
    private StudentAppService studentAppService;
    private TaskScheduler taskScheduler;

    public StudentJobScheduler(StudentAppService studentAppService, TaskScheduler taskScheduler) {
        this.studentAppService = studentAppService;
        this.taskScheduler = taskScheduler;
    }

    @EventListener
    void atStart(AppStartedEvent event) {
        studentAppService.collectNewStudents();
        taskScheduler.schedule(() -> studentAppService.collectNewStudents(),
                new CronTrigger("0 0 8 ? * MON", params.values().getZoneId()));
    }
}
