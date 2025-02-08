package szlicht.daniel.calendar.meeting;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;


@ConfigurationProperties(prefix = "meeting")
public record MeetingParams(
        Mail mail,
        Params params,
        Keywords keywords
) {

    @ConstructorBinding
    public MeetingParams {
    }


    public record Mail(String bot, String owner, String phone) {}

    public record Params(int defaultMinutes, List<Double> hours) {}

    public record Keywords(String descriptionPrefix, List<String> propositions, List<String> arrange) {}
}
