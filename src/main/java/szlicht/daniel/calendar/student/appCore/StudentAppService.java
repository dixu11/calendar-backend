package szlicht.daniel.calendar.student.appCore;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.meeting.appCore.NextMonthMeetingsEvent;
import szlicht.daniel.calendar.meeting.infrastructure.GoogleCalendarRepository;
import szlicht.daniel.calendar.meeting.appCore.Meeting;

import java.util.Set;
import java.util.stream.Collectors;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

@Service
public class StudentAppService {

    private final StudentRepository studentRepository;

    public StudentAppService( StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @EventListener
    public void collectNewStudents(NextMonthMeetingsEvent event) {
        Set<Student> activeStudents = getActiveStudentsByEmails(event.getMeetings());
        studentRepository.addIfNotExists(activeStudents);
    }

    private Set<Student> getActiveStudentsByEmails(Set<Meeting> meetings) {
       return meetings.stream()
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