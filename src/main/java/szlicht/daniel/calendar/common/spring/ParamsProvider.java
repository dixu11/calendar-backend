package szlicht.daniel.calendar.common.spring;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import szlicht.daniel.calendar.meeting.app_core.MeetingParams;

@Configuration
public class ParamsProvider implements ApplicationListener<ApplicationReadyEvent> {

    public static MeetingParams params;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ParamsProvider(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        initParams();
        applicationEventPublisher.publishEvent(new AppStartedEvent());
    }

    private static void initParams() {
        params = ApplicationContextProvider.getBean(MeetingParams.class);
    }
}
