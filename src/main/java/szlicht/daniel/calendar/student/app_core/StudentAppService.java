package szlicht.daniel.calendar.student.app_core;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.spring.Logger;
import szlicht.daniel.calendar.meeting.app_core.NewMeetingEvent;

import java.util.Set;

@Service
public class StudentAppService {

    private final StudentRepository studentRepository;
    private final Logger logger;

    public StudentAppService(StudentRepository studentRepository, Logger logger) {
        this.studentRepository = studentRepository;
        this.logger = logger;
    }

    @EventListener
    public void newStudentAdded(NewStudentEvent event) {
        boolean success = studentRepository.save(event.getStudent());
        if (success) {
            logger.notifyOwner("New student on app: " + event.getStudent().getName() + " "
                    + event.getStudent().getEmail(),"",false);
        }
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
}