package szlicht.daniel.calendar.mail_dialog.app_core;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.student.app_core.NewStudentEvent;
import szlicht.daniel.calendar.student.app_core.Student;
import szlicht.daniel.calendar.student.app_core.StudentRang;

@Service
public class DialogAppService {
    private StartMessageRepository startMessageRepository;
    private ApplicationEventPublisher publisher;

    public DialogAppService(StartMessageRepository startMessageRepository, ApplicationEventPublisher publisher) {
        this.startMessageRepository = startMessageRepository;
        this.publisher = publisher;
    }

    public void newStudent(StudentStartMessageDto message) {
        publisher.publishEvent(new NewStudentEvent(
                new Student(message.getName(),message.getEmail(), StudentRang.ASKED)));
        startMessageRepository.save(message);
    }


}
