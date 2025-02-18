package szlicht.daniel.calendar.mail_dialog.infrastructure;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import szlicht.daniel.calendar.common.spring.WarningLogger;
import szlicht.daniel.calendar.mail_dialog.app_core.DialogAppService;
import szlicht.daniel.calendar.mail_dialog.app_core.StudentStartMessageDto;

@RestController
public class DialogInitializeController {

    private WarningLogger warningLogger;
    private DialogAppService dialogAppService;

    public DialogInitializeController(WarningLogger warningLogger, DialogAppService dialogAppService) {
        this.warningLogger = warningLogger;
        this.dialogAppService = dialogAppService;
    }

    @PostMapping("api/v1/start-messages")
    public void initializeDialogWithNewStudent(@RequestBody StudentStartMessageDto studentStartMessageDto) {
        warningLogger.notifyOwner("Nowy student z tiktoka!",studentStartMessageDto.getStory(),false);
        dialogAppService.newStudent(studentStartMessageDto);
    }
}
