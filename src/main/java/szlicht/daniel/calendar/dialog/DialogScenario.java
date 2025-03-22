package szlicht.daniel.calendar.dialog;

public abstract class DialogScenario {
    final DialogPresenter dialogPresenter;

    public DialogScenario(DialogPresenter dialogPresenter) {
        this.dialogPresenter = dialogPresenter;
    }

    public abstract String keyword();

    public abstract void runScenario(EmailParser emailParser);
}
