package szlicht.daniel.calendar.student.appCore;

import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.meeting.infrastructure.GoogleCalendarRepository;
import szlicht.daniel.calendar.meeting.appCore.Meeting;

import java.util.Set;
import java.util.stream.Collectors;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

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
        Set<Student> activeStudents = getActiveStudentsByEmails();
        studentRepository.addIfNotExists(activeStudents);
    }

    private Set<Student> getActiveStudentsByEmails() {
       return googleCalendarRepository.getMonthFromNowMeetings()
                .stream()
                .filter(Meeting::isMentoring)
                .filter(meeting -> !meeting.getDetails().getMail().isEmpty())
                .map(meeting -> new Student(extractStudentName(meeting.getDetails().getSummary()),
                        meeting.getDetails().getMail()))
                .collect(Collectors.toSet());
    }

    private String extractStudentName(String summary) {
        String result = summary.replace(params.values().summaryPrefix(), "")
                .replace("*", "");
        int emailStart = result.indexOf("<");
        if (emailStart != -1) {
            result = result.substring(0, emailStart);
        }
        String[] split = result.split(" ");
        if (split.length == 2 && split[1].length() > 3) {
            result = split[0] + " " + split[1].substring(0, 3);
        }
        return result;
    }
}


//update Students
// -> get active students mails
// -> add new students