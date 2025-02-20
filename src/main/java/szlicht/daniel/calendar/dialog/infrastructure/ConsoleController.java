package szlicht.daniel.calendar.dialog.infrastructure;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.dialog.app_core.DialogAppService;
import szlicht.daniel.calendar.meeting.app_core.MeetingDto;

import java.time.LocalDateTime;
import java.util.Scanner;

@Component
@Profile("dev")
class ConsoleController implements ApplicationListener<ApplicationReadyEvent> {

    private final DialogAppService dialogAppService;

    ConsoleController(DialogAppService dialogAppService) {
        this.dialogAppService = dialogAppService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        new Thread(this::startConsoleController)
                .start();
    }

    private void startConsoleController() {
        System.out.println("Wprowadź mail na który należy wysłać grafik ({mail} {godziny double}):");
        System.out.println("Lub podaj meeting(arrange {start} {length minutes} {mail}):");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                String[] input = scanner.nextLine().split(" ");
                if (input[0].equals("arrange")) {
                    arrangeMeeting(input);
                } else {
                    sendPropositions(input);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void arrangeMeeting(String[] input) {
        MeetingDto meetingDto = MeetingDto.builder()
                .start(LocalDateTime.parse(input[1]))
                .end(LocalDateTime.parse(input[1]).plusMinutes(Integer.parseInt(input[2])))
                .providedDescription("")
                .email(input[3])
                .build();
        dialogAppService.startArrangeScenario(meetingDto);
    }

    private void sendPropositions(String[] input) {
        String mail = input[0];
        int minutes = (int) (Double.parseDouble(input[1]) * 60);
        dialogAppService.startNextPropositionsScenario(minutes, mail);
        System.out.println("Wysłano!");
    }
}