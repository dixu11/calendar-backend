package szlicht.daniel.calendar.student.appCore;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;
import szlicht.daniel.calendar.meeting.infrastructure.GoogleCalendarRepository;
import szlicht.daniel.calendar.meeting.appCore.Meeting;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentAppService {

    private final GoogleCalendarRepository googleCalendarRepository;
    private final StudentRepository studentRepository;

    public StudentAppService(GoogleCalendarRepository googleCalendarRepository, StudentRepository studentRepository) {
        this.googleCalendarRepository = googleCalendarRepository;
        this.studentRepository = studentRepository;
    }

    public void collectNewStudents() {
        System.out.println("Collecting new students");
        Set<String> mails = getActiveStudentsMails();
        studentRepository.addIfNotExists(mails);
    }

    private Set<String> getActiveStudentsMails() {
       return googleCalendarRepository.getLastMonthAndCurrentMeetings()
                .stream()
                .filter(Meeting::isMentoring)
                .map(meeting -> meeting.getDetails().getMail())
                .filter(mail -> !mail.isEmpty())
                .collect(Collectors.toSet());
    }
}


//update Students
// -> get active students mails
// -> add new students