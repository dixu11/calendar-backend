package szlicht.daniel.calendar.meeting;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;

@Component
public class CommandLineController implements CommandLineRunner {

    private CalendarService calendarService;
    private MeetingsSender meetingsSender;

    public CommandLineController(CalendarService calendarService, MeetingsSender meetingsSender) {
        this.calendarService = calendarService;
        this.meetingsSender = meetingsSender;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Wprowadź mail na który należy wysłać grafik:");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String[] input = scanner.nextLine().split(" ");
            String mail = input[0];
            int minutes = (int) (Double.parseDouble(input[1]) * 60);
            Map<LocalDate, Meeting> meetingPropositions = calendarService.getMeetingPropositions(minutes);
            meetingsSender.sendPropositions(meetingPropositions, mail);
            System.out.println("Wysłano!");
        }
    }
}
