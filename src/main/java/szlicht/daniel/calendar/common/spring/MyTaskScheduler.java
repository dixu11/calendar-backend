package szlicht.daniel.calendar.common.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.OneTimeTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Duration;
import java.time.LocalDateTime;

@Configuration
public class MyTaskScheduler implements SchedulingConfigurer {

    private ScheduledTaskRegistrar taskRegistrar;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
    }

    public void scheduleOneTimeTask(Runnable task, LocalDateTime time) {
        taskRegistrar.scheduleOneTimeTask(new OneTimeTask(task, Duration.between(LocalDateTime.now(), time)));
    }

}
