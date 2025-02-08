package szlicht.daniel.calendar.meeting;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@ConfigurationProperties(prefix = "meeting")
public record MeetingParams(
        Mail mail,
        Values values,
        Keywords keywords
) {

    @ConstructorBinding
    public MeetingParams {
    }

    public record Mail(String bot, String owner, String phone) {}

    public record Values(int minutes, List<Double> hours) {}

    public record Keywords(String description, List<String> propositions, List<String> arrange) {}
}
