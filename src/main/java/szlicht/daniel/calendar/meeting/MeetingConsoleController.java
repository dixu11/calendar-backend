package szlicht.daniel.calendar.meeting;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.meeting.core.CalendarFacade;
import szlicht.daniel.calendar.meeting.core.Meeting;

import java.time.LocalDateTime;
import java.util.Scanner;

@Component
class MeetingConsoleController implements ApplicationListener<ApplicationReadyEvent> {

    private CalendarFacade facade;

    MeetingConsoleController(CalendarFacade facade) {
        this.facade = facade;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        new Thread(this::startConsoleController)
                .start();
    }

    private void startConsoleController() {
        System.out.println("Wprowadź mail na który należy wysłać grafik ({mail} {godziny double}):");
        System.out.println("Lub podaj meeting(meeting {start} {length minutes}):");
        Scanner scanner = new Scanner(System.in);
       /* while (true) {
            try {
                String[] input = scanner.nextLine().split(" ");
                if (input[0].equals("meeting")) {
                    arrangeMeeting(input);
                } else {
                    sendPropositions(input);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    private void arrangeMeeting(String[] input) {
        Meeting meeting = new Meeting(LocalDateTime.parse(input[1]), Integer.parseInt(input[2]));
        facade.arrangeMeeting(meeting);
    }

    private void sendPropositions(String[] input) {
        String mail = input[0];
        int minutes = (int) (Double.parseDouble(input[1]) * 60);
        facade.sendPropositions(minutes, mail);
        System.out.println("Wysłano!");
    }
}