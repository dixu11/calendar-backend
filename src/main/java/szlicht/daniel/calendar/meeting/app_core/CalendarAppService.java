package szlicht.daniel.calendar.meeting.app_core;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.spring.WarningLogger;
import szlicht.daniel.calendar.student.app_core.NewStudentEvent;
import szlicht.daniel.calendar.student.app_core.Student;
import szlicht.daniel.calendar.student.app_core.StudentRang;
import szlicht.daniel.calendar.student.app_core.StudentRepository;

import java.util.Collections;
import java.util.Optional;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;


@Service
public class CalendarAppService {
    private PropositionsDomainService propositionsDomainService;
    private ArrangeMeetingDomainService arrangeMeetingDomainService;

    private StudentRepository studentRepository;
    private WarningLogger warningLogger;
    private final ApplicationEventPublisher eventPublisher;
    private CalendarRepository calendarRepository;

    public CalendarAppService(PropositionsDomainService propositionsDomainService,
                              ArrangeMeetingDomainService arrangeMeetingDomainService,
                              StudentRepository studentRepository,
                              WarningLogger warningLogger,
                              ApplicationEventPublisher eventPublisher,
                              CalendarRepository calendarRepository) {
        this.propositionsDomainService = propositionsDomainService;
        this.arrangeMeetingDomainService = arrangeMeetingDomainService;
        this.studentRepository = studentRepository;
        this.warningLogger = warningLogger;
        this.eventPublisher = eventPublisher;
        this.calendarRepository = calendarRepository;
    }

    public Propositions getPropositions(Integer minutes) {
        return propositionsDomainService.createMeetingPropositions(minutes);
    }

    public boolean arrangeManualMeeting(Meeting meeting) {
        String studentEmail = "";
        if (meeting.getType() == MeetingType.CYCLIC_MENTORING) {
            return false;
        }
        if (!meeting.getDetails().getEmail().isBlank()) {
            studentEmail = meeting.getDetails().getEmail();
        } else {
            studentEmail = studentRepository.getByName(meeting.getDetails().getSummary()).map(Student::getEmail).orElse("");
        }
        String studentName = meeting.getDetails().getSummary();
        Student student = new Student(studentName, studentEmail, StudentRang.HAD_MENTORING);
        if (studentEmail.isBlank()) {
            return false;
        }
        meeting.getDetails().setEmail(student.getEmail());
        meeting.getDetails().setOwnerDescription("Manual meeting corrected by app");
        meeting.getDetails().setSummary(formatSummary(student.getName()));
        meeting.setType(MeetingType.MENTORING);
        System.out.println("Manual meeting corrected by app: " + meeting.getDetails().getSummary() + " at " + meeting.when());
        calendarRepository.removeMeetingById(meeting.getId());
        warningLogger.notifyOwner("Event deleted at cleanup: " + meeting.getDetails().getSummary() + " at " + meeting.when(), "", false);
        MeetingDto meetingDto = meeting.toDto();
        meetingDto.setStudentName(student.getName());
        meeting.setId(null);
        calendarRepository.save(meeting);
        studentRepository.addIfNotExists(Collections.singleton(student));
        return true;
    }

    public Meeting arrangeMeeting(MeetingDto meetingDto) {
        String studentName = Student.formatStudentName(meetingDto.getStudentName());
        meetingDto.setStudentName(studentName);
        Optional<Student> studentOptional = studentRepository.getByEmail(meetingDto.getEmail());
        Meeting meeting;
        if (studentOptional.isPresent()) {
           meeting = arrangeNextMeeting(meetingDto);
        } else {
           meeting = arrangeFirstMeeting(meetingDto);
        }
        return meeting;
    }

    private Meeting arrangeFirstMeeting(MeetingDto meetingDto) {
        Meeting meeting = arrange(meetingDto);
        eventPublisher.publishEvent(new NewStudentEvent(
                new Student(meetingDto.getStudentName(), meetingDto.getEmail(), StudentRang.HAD_MENTORING)));
        return meeting;
    }

    private Meeting arrangeNextMeeting(MeetingDto meetingDto) {
       return arrange(meetingDto);
    }

    private Meeting arrange(MeetingDto meetingDto) {
        Meeting meeting = new Meeting(meetingDto.getStart(), meetingDto.getEnd());
        meeting.setDetails(new Meeting.Details(formatSummary(meetingDto.getStudentName()),
                "Spotkanie umówione automatycznie",
                meetingDto.getProvidedDescription(), meetingDto.getEmail())
        );
        arrangeMeetingDomainService.arrange(meeting);
        return meeting;
    }

    private String formatSummary(String studentName) {
        return studentName + " " + params.values().summaryPrefix() + params.values().ownerName();
    }
}
