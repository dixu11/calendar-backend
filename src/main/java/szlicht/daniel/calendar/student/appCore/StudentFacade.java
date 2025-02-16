package szlicht.daniel.calendar.student.appCore;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;
import szlicht.daniel.calendar.meeting.infrastructure.GoogleCalendarRepository;
import szlicht.daniel.calendar.meeting.appCore.Meeting;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentFacade {

    private final GoogleCalendarRepository googleCalendarRepository;
    private final StudentRepository studentRepository;

    public StudentFacade(GoogleCalendarRepository googleCalendarRepository, StudentRepository studentRepository) {
        this.googleCalendarRepository = googleCalendarRepository;
        this.studentRepository = studentRepository;
    }

    @EventListener
    public void collectNewStudents(AppStartedEvent e) {
        System.out.println("Collecting new students");
        Set<String> mails = getActiveStudentsMails();
        addNewStudents(mails);
    }

    private Set<String> getActiveStudentsMails() {
       return googleCalendarRepository.getLastMonthAndCurrentMeetings()
                .stream()
                .filter(Meeting::isMentoring)
                .map(meeting -> meeting.getDetails().getMail())
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