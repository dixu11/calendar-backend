package szlicht.daniel.calendar.dialog;

import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.meeting.Logger;
import szlicht.daniel.calendar.workshop.WorkshopAppService;

@Component
public class GroupMentoringScenario extends DialogScenario{
    private WorkshopAppService workshopAppService;
    private Logger logger;

    public GroupMentoringScenario(DialogPresenter dialogPresenter, WorkshopAppService workshopAppService, Logger logger) {
        super(dialogPresenter);
        this.workshopAppService = workshopAppService;
        this.logger = logger;
    }

    @Override
    public String keyword() {
        return "grupowe lekcje";
    }

    @Override
    public void runScenario(EmailParser emailParser) {
        dialogPresenter.showDialog(new WorkshopDialogView(emailParser.getEmail(),workshopAppService.getWorkshops()), emailParser);
        logger.notifyOwner("Group mentoring offer sent to "+ emailParser.getName() + " mail:" + emailParser.getEmail(),
                "response to decision: "+ emailParser.getContent() , false);
    }
}
