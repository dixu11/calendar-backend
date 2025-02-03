package szlicht.daniel.calendar.meeting;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v0/meetings")
public class MeetingController {
    @GetMapping
    String getMeetings() {
        return "hello";
    }
}
