package szlicht.daniel.calendar.meeting;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.meeting.core.CalendarFacade;

import java.util.Scanner;

@Component
class CommandLineController implements CommandLineRunner {

    private CalendarFacade facade;

    CommandLineController(CalendarFacade facade) {
        this.facade = facade;
    }

    @Override
    public void run(String... args) {
        System.out.println("Wprowadź mail na który należy wysłać grafik ({mail} {godziny double}):");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String[] input = scanner.nextLine().split(" ");
            String mail = input[0];
            int minutes = (int) (Double.parseDouble(input[1]) * 60);
            facade.sendPropositions(minutes,mail);
            System.out.println("Wysłano!");
        }
    }
}