package szlicht.daniel.calendar.meeting;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v0/meetings")
public class MeetingController {
    private CalendarService calendarService;

    public MeetingController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping
    ResponseEntity<?> getMeetings(Double hours, Integer minutes) {
        if (hours != null) {
            minutes = (int) (hours * 60);
        }
        try {
            return ResponseEntity.ok(calendarService.getMeetingPropositions(minutes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        } catch (CalendarOfflineException e) {
            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }
}
