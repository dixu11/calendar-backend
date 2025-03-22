package szlicht.daniel.calendar.dialog;

public interface DialogPresenter {
    void showDialog(DialogView dialogView, EmailData emailData); //old version
    void showDialog(DialogView dialogView, EmailParser emailParser);
}
