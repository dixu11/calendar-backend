package szlicht.daniel.calendar.dialog.app_core;

import static szlicht.daniel.calendar.common.mail.MailUtils.mailto;

public abstract class HtmlDialog {

    abstract String getSubject();

    String getHtml() {
        String result = "<!DOCTYPE html>\n";
        String head = getHead();
        String body = getBody();
        return result + tag("html", head + body);
    }

    String getHead(){
        String result = "";
        result += " <meta charset=\"UTF-8\">";
        result += " <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">";
        result += tag("title", getSubject());
        result += getStyle();
        return tag("head", result);
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
                                a {
                                    text-decoration: none;
                                }
                            </style>
                """;
    }

    String getBody(){
        String title = tag("h1", getTitle());
        String intro = tag("p", getIntro());
        String container = tag("div", title + intro, "container");
        return tag("body", container + getSections());
    }

    abstract String getTitle();

    abstract String getIntro();

  abstract   String getSections();

    String section(String title, String content) {
        return tag("div", section(tag("h2", title) + content), "section");
    }

    String section(String innerHtml) {
        return "<div style='padding: 10px;'>" + innerHtml + "</div>";
    }

    String asList(String... items) {
        return "<ul><li>" + String.join("</li><li>", items) + "</li></ul>";
    }

    String tag(String tag, String content, String... classes) {
        String classAttr = (classes.length > 0) ? " class=\"" + String.join(" ", classes) + "\"" : "";
        return "<" + tag + classAttr + ">" + content + "</" + tag + ">";
    }
}
