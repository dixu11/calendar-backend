package szlicht.daniel.calendar.presenter;

import szlicht.daniel.calendar.dialog.Dialog;

public class HtmlDialog {
    private Dialog dialog;

    public HtmlDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public String getSubject() {
        return dialog.getSubject();
    }

    public String getHtml() {
        return dialog.getHtml();
    }
}
