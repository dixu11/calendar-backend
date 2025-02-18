package szlicht.daniel.calendar.common.calendar;

import com.google.api.services.calendar.model.Event;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;
import szlicht.daniel.calendar.meeting.infrastructure.GoogleCalendarRepository;

import java.time.LocalDateTime;

import static szlicht.daniel.calendar.common.calendar.GoogleCalendarUtils.toEventDateTime;

//@Component
@Profile("dev")
public class GoogleColorDemo {
    private GoogleCalendarRepository googleCalendarRepository;

    public GoogleColorDemo(GoogleCalendarRepository googleCalendarRepository) {
        this.googleCalendarRepository = googleCalendarRepository;
    }

    @EventListener
    public void showColorsInCalendar(AppStartedEvent e) {
        GoogleCalendarColor[] values = GoogleCalendarColor.values();
        for (GoogleCalendarColor value : values) {
            Event event = new Event();
            event.setStart(toEventDateTime(LocalDateTime.now().plusHours(Long.parseLong(value.getColorId()))));
            event.setEnd(toEventDateTime(LocalDateTime.now().plusHours(Long.parseLong(value.getColorId())+1)));
            event.setColorId(value.getColorId());
            event.setSummary("Color: " + value.getColorId());
            googleCalendarRepository.save(event);
        }
    }
}
