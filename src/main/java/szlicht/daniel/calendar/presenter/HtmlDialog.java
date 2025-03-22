package szlicht.daniel.calendar.presenter;

import szlicht.daniel.calendar.dialog.DialogView;

public class HtmlDialog {
    private DialogView dialogView;

    public HtmlDialog(DialogView dialogView) {
        this.dialogView = dialogView;
    }

    public String getSubject() {
        return dialogView.getSubject();
    }

    public String getHtml() {
        return dialogView.getHtml();
    }
}
