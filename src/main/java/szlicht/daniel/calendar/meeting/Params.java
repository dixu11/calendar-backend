package szlicht.daniel.calendar.meeting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class Params {
    @Value("${meeting.mail.bot}")
    public static String BOT_MAIL;
    @Value("${meeting.mail.owner}")
    public static String OWNER_MAIL;
    @Value("${meeting.mail.phone}")
    public static String PHONE_NUMBER;
    @Value("${meeting.params.default.minutes}")
    public static int DEFAULT_MEETING_LENGTH_MINUTES;
    @Value("${meeting.params.hours}")
    public static List<Double> ACCEPTABLE_LENGTH_HOURS;
    @Value("${meeting.keywords.prefix.description}")
    public static String DESCRIPTION_PREFIX;
    @Value("${meeting.keywords.propositions}")
    public static List<String> PROPOSITIONS_KEYWORDS;
    @Value("${meeting.keywords.arrange}")
    public static List<String> ARRANGE_KEYWORDS;
}
