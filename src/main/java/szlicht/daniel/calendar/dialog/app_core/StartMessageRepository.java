package szlicht.daniel.calendar.dialog.app_core;

import java.util.Optional;

public interface StartMessageRepository {
    void save(StudentStartMessageDto studentStartMessageDto);
    boolean existsByEmail(String email);
}
