package szlicht.daniel.calendar.dialog.app_core;

public class StartMentoringHtmlDialog extends HtmlDialog {


    @Override
    String getSubject() {
        return "Startujemy mentoring z programowania!";
    }

    String getHead(){
        String result = "";
        result += " <meta charset=\"UTF-8\">";
        result += " <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">";
        result += tag("title", getSubject());
        result += getStyle();
        return tag("head", result);
    }

    @Override
    String getHtml() {
        String result = "<!DOCTYPE html>\n";
        String head = getHead();
        String body = getBody();
        return result + tag("html", head + body);
    }

    String getBody(){
        String title = tag("h1", getSubject());
        String intro = tag("p", "Dziękujemy za kontakt. Poniżej znajdziesz ważne informacje.");
        String container = tag("div", title + intro, "container");
        return tag("body", container + getSections());
    }

    String getSections() {
        String pointsList = asList(
                "Pierwszy punkt ważnej informacji",
                "Drugi punkt z kolejną istotną kwestią",
                "Trzeci punkt, który warto zapamiętać"
        );
        String importantPoints = section("Najważniejsze punkty:", pointsList);
        String additionalInfo = section("Dodatkowe informacje", "<p>Jeśli masz jakiekolwiek pytania, śmiało do nas pisz. Jesteśmy tu, aby pomóc!</p>");
        return importantPoints + additionalInfo;
    }


    String section(String title, String content) {
        return tag("div", section(tag("h2", title) + content), "section");
    }

    String asList(String... items) {
        return "<ul><li>" + String.join("</li><li>", items) + "</li></ul>";
    }


    String tag(String tag, String content, String... classes) {
        String classAttr = (classes.length > 0) ? " class=\"" + String.join(" ", classes) + "\"" : "";
        return "<" + tag + classAttr + ">" + content + "</" + tag + ">";
    }



    String getStyle() {
        return """
                <style>
                                body {
                                    font-family: Arial, sans-serif;
                                    background-color: #f4f4f4;
                                    margin: 0;
                                    padding: 0;
                                    text-align: center;
                                }
                                .container {
                                    max-width: 600px;
                                    background-color: #ffffff;
                                    padding: 20px;
                                    margin: 20px auto;
                                    border-radius: 8px;
                                    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                                }
                                h1 {
                                    color: #333;
                                }
                                p {
                                    color: #555;
                                    line-height: 1.5;
                                }
                                .section {
                                    margin: 20px 0;
                                    padding: 15px;
                                    background-color: #f9f9f9;
                                    border-radius: 5px;
                                }
                                ul {
                                    text-align: left;
                                    margin: 10px auto;
                                    max-width: 500px;
                                    padding: 0;
                                }
                                ul li {
                                    background-color: #e3e3e3;
                                    margin: 5px 0;
                                    padding: 10px;
                                    border-radius: 4px;
                                    list-style-type: none;
                                }
                            </style>
                """;
    }
}
