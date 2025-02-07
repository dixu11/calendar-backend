package szlicht.daniel.calendar.common;

import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

public class HtmlUtils {
    public static String mailto(String subject, String body,String label, String to) {
        return String.format("""
                <a href="mailto:%s
                ?subject=%s
                &body=%s
                ">%s</a>
                """,to,
                UriUtils.encode(subject, StandardCharsets.UTF_8),
                UriUtils.encode(body,StandardCharsets.UTF_8),
                label);
    }
}
