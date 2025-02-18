package szlicht.daniel.calendar.mail_dialog.app_core;

public abstract class DialogMail {

    abstract String getSubject();
    abstract String getHtml();

    String section(String innerHtml) {
        return "<div style='padding: 10px;'>" + innerHtml + "</div>";
    }
}
