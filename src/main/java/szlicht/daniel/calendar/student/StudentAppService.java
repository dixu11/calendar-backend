package szlicht.daniel.calendar.student;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.meeting.Logger;
import szlicht.daniel.calendar.meeting.NewMeetingEvent;

@Service
public class StudentAppService {

    private final StudentRepository studentRepository;
    private final Logger logger;

    public StudentAppService(StudentRepository studentRepository, Logger logger) {
        this.studentRepository = studentRepository;
        this.logger = logger;
    }

    @EventListener
    public boolean newStudentAdded(NewStudentEvent event) {
        if (event.getStudent().hasOnlyNameFilled()) {
            logger.notifyOwner("New student with name to fix in database: " + event.getStudent().getName(), "", false);
        }
        boolean success = studentRepository.save(event.getStudent());
        if (success) {
            logger.notifyOwner("New student on app: " + event.getStudent().getName() + " "
                    + event.getStudent().getEmail(), "", false);
        }
        return success;
    }

    @EventListener
    public void newMeeting(NewMeetingEvent event) {
        if (event.getStudent().getRank() != StudentRang.ASKED) {
            return;
        }
        Student student = event.getStudent();
        student.setRang(StudentRang.HAD_MENTORING);
        studentRepository.saveOrUpdate(student);
    }

    public boolean addIfNotExists(String email, String name, String message) {
        if (studentRepository.existsByEmail(email)) {
            return false;
        }
        studentRepository.save(new Student(0, name, email, StudentRang.ASKED, message));
        return true;
    }
}