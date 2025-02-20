package szlicht.daniel.calendar.dialog.app_core;

public abstract class HtmlDialog {

    abstract String getSubject();
    abstract String getHtml();

    String section(String innerHtml) {
        return "<div style='padding: 10px;'>" + innerHtml + "</div>";
    }
}
