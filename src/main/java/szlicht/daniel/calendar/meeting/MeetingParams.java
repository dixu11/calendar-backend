package szlicht.daniel.calendar.meeting;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "meeting")
public record MeetingParams(
        Mail mail,
        Values values,
        Keywords keywords
) {
    @ConstructorBinding
    public MeetingParams {
    }

    public record Mail(String bot, String owner, String phone) {
    }

    public record Values(
            int minutes,
            List<Double> hours,
            WorkHours workHours
    ) {

        public record WorkHours(
                LocalTime start,
                LocalTime end,
                Map<String, WorkHoursOverride> workHoursOverrides
        ) {
            public record WorkHoursOverride(LocalTime start, LocalTime end) {
            }

            public WorkHours.WorkHoursOverride forDay(DayOfWeek dayOfWeek) {
                return forDay(dayOfWeek.name().toLowerCase());
            }

            private WorkHoursOverride forDay(String day) {
                WorkHoursOverride standardHours = new WorkHoursOverride(start, end);
                if (workHoursOverrides == null) {
                    return standardHours;
                }
                WorkHoursOverride override = workHoursOverrides.get(day.toLowerCase());
                if (override == null) {
                    return standardHours;
                }
                LocalTime effectiveStart = (override.start() != null) ? override.start() : start;
                LocalTime effectiveEnd = (override.end() != null) ? override.end() : end;
                return new WorkHoursOverride(effectiveStart, effectiveEnd);
            }
        }
    }

    public record Keywords(
            String description,
            List<String> propositions,
            List<String> arrange
    ) {
    }
}