package szlicht.daniel.calendar.student.app_core;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.meeting.app_core.NewMeetingEvent;

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

    @EventListener
    public void newMeeting(NewMeetingEvent event) {
        if (event.getStudent().getRank() != StudentRang.ASKED) {
            return;
        }
        Student student = event.getStudent();
        student.setRang(StudentRang.HAD_MENTORING);
        //todo temporarly needed
        Student oldFromDb = studentRepository.getByEmail(student.getEmail()).orElseThrow();
        student.setId(oldFromDb.getId());
        studentRepository.save(student);
    }
}