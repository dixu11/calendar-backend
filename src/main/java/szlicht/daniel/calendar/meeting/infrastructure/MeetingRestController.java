package szlicht.daniel.calendar.meeting.infrastructure;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import szlicht.daniel.calendar.meeting.app_core.CalendarAppService;
import szlicht.daniel.calendar.meeting.app_core.CalendarOfflineException;


@RestController
@RequestMapping("/api/v0/meetings")
class MeetingRestController {
    private CalendarAppService facade;

    MeetingRestController(CalendarAppService facade) {
        this.facade = facade;
    }

    @GetMapping
    ResponseEntity<?> getMeetings(Double hours, Integer minutes) {
        if (hours != null) {
            minutes = (int) (hours * 60);
        }
        try {
            return ResponseEntity.ok(facade.getMeetingPropositions(minutes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        } catch (CalendarOfflineException e) {
            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }
}
