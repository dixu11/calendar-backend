package szlicht.daniel.calendar.dialog;

import org.springframework.stereotype.Component;

@Component
public class MentoringDialogScenario extends DialogScenario {

    public MentoringDialogScenario(DialogPresenter dialogPresenter) {
        super(dialogPresenter);
    }

    @Override
    public String keyword() {
        return "mentoring";
    }

    @Override
    public void runScenario(EmailParser emailParser) {
        dialogPresenter.showDialog(new MentoringDialogView(emailParser.getEmail()),emailParser);
    }
}
