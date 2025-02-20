package szlicht.daniel.calendar.dialog.app_core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentStartMessageDto {
    private String name = "";
    private String email = "";
    private String story = "";
}
