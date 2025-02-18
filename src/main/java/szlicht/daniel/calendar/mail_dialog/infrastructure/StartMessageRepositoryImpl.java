package szlicht.daniel.calendar.mail_dialog.infrastructure;

import org.springframework.stereotype.Repository;
import szlicht.daniel.calendar.mail_dialog.app_core.StartMessageRepository;
import szlicht.daniel.calendar.mail_dialog.app_core.StudentStartMessageDto;

import java.time.LocalDateTime;

@Repository
public class StartMessageRepositoryImpl implements StartMessageRepository {
    private StartMessageJpaRepository startMessageJpaRepository;

    public StartMessageRepositoryImpl(StartMessageJpaRepository startMessageJpaRepository) {
        this.startMessageJpaRepository = startMessageJpaRepository;
    }
    @Override
    public void save(StudentStartMessageDto message) {
        startMessageJpaRepository.save(new StartMessageEntity(message.getNick(), message.getName(),
                message.getEmail(), message.getStory(), LocalDateTime.now()));
    }
}
