package szlicht.daniel.calendar.student.core;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;
import szlicht.daniel.calendar.meeting.core.CalendarRepository;
import szlicht.daniel.calendar.meeting.core.Meeting;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentFacade {

    private final CalendarRepository calendarRepository;
    private final StudentRepository studentRepository;

    public StudentFacade(CalendarRepository calendarRepository, StudentRepository studentRepository) {
        this.calendarRepository = calendarRepository;
        this.studentRepository = studentRepository;
    }

    @EventListener
    public void collectNewStudents(AppStartedEvent e) {
        System.out.println("Collecting new students");
        Set<String> mails = getActiveStudentsMails();
        addNewStudents(mails);
    }

    private Set<String> getActiveStudentsMails() {
       return calendarRepository.getLastMonthAndCurrentMeetings()
                .stream()
                .filter(Meeting::isMentoring)
                .map(Meeting::getMail)
                .filter(mail -> !mail.isEmpty())
                .collect(Collectors.toSet());
    }

    private void addNewStudents(Set<String> mails) {
        for (String mail : mails) {
            if (!studentRepository.existsByEmail(mail)) {
                studentRepository.save(new StudentEntity(mail));
            }
        }
    }

}


//update Students
// -> get active students mails
// -> add new students