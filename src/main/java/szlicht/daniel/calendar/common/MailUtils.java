package szlicht.daniel.calendar.common;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;
import org.eclipse.angus.mail.imap.IMAPMessage;
import java.io.IOException;

public class MailUtils {

    public static String extractTextFromMessage(IMAPMessage message) throws IOException, MessagingException {
        Object content = message.getContent();
        if (content instanceof String text) {
            return text;
        } else if (content instanceof MimeMultipart multipart) {
            return getTextFromMimeMultipart(multipart);
        }
        return null;
    }

    private static String getTextFromMimeMultipart(MimeMultipart multipart) throws MessagingException, IOException {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            if (part.isMimeType("text/plain")) {
                return (String) part.getContent();
            } else if (part.isMimeType("text/html")) {
                String html = (String) part.getContent();
                return htmlToPlainText(html);
            }
        }
        return null;
    }

    private static String htmlToPlainText(String html) {
        return html.replaceAll("<[^>]+>", "").replaceAll("&nbsp;", " ").trim();
    }
}
