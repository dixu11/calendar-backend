package szlicht.daniel.calendar.student.app_core;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class StudentAppService {

    private final StudentRepository studentRepository;

    public StudentAppService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @EventListener
    public void newStudentAdded(NewStudentEvent event) {
        studentRepository.addIfNotExists(Set.of(event.getStudent()));
    }
}