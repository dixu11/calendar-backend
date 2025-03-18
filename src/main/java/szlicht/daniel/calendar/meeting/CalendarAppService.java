package szlicht.daniel.calendar.meeting;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.java.NotImplementedException;
import szlicht.daniel.calendar.student.NewStudentEvent;
import szlicht.daniel.calendar.student.Student;
import szlicht.daniel.calendar.student.StudentRang;
import szlicht.daniel.calendar.student.StudentRepository;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;


@Service
public class CalendarAppService {
    private final PropositionsDomainService propositionsDomainService;
    private final ArrangeMeetingDomainService arrangeMeetingDomainService;
    private final StudentRepository studentRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CalendarRepository calendarRepository;
    private final Logger logger;

    public CalendarAppService(PropositionsDomainService propositionsDomainService,
                              ArrangeMeetingDomainService arrangeMeetingDomainService,
                              StudentRepository studentRepository,
                              Logger logger,
                              ApplicationEventPublisher eventPublisher,
                              CalendarRepository calendarRepository) {
        this.propositionsDomainService = propositionsDomainService;
        this.arrangeMeetingDomainService = arrangeMeetingDomainService;
        this.studentRepository = studentRepository;
        this.logger = logger;
        this.eventPublisher = eventPublisher;
        this.calendarRepository = calendarRepository;
    }

    public Propositions getPropositions(Integer minutes) {
        return propositionsDomainService.createMeetingPropositions(minutes);
    }

    public Meeting arrangeMeeting(MeetingDto meetingDto) {
        if (meetingDto.getId() != null) {
            return arrangeManualMeeting(meetingDto);
        }
        return arrange(meetingDto);
    }

    private Meeting arrangeManualMeeting(MeetingDto meetingDto) {
        String oldVersionId = meetingDto.getId();
        prepareManualMeeting(meetingDto);
        Meeting meeting = arrange(meetingDto);
        calendarRepository.removeMeetingById(oldVersionId);
        logger.notifyOwner("Event deleted at cleanup: " + meeting.getDetails().getSummary() +
                " at " + meeting.when(), "", false);
        return meeting;
    }

    private void prepareManualMeeting(MeetingDto meetingDto) {
        if (meetingDto.getType() == MeetingType.CYCLIC_MENTORING) {
            throw new NotImplementedException("Cyclic meeting not supported " + meetingDto.getSummary() + meetingDto.getStart() );
        }
        if (meetingDto.getEmail() == null || meetingDto.getEmail().isBlank()) {
            meetingDto.setEmail(studentRepository.getByName(meetingDto.getSummary()).map(Student::getEmail)
                    .orElseThrow(()-> new NotImplementedException("Manual meeting without email not supported for name " + meetingDto.getStudentName())));
        }
        meetingDto.setId(null);
        meetingDto.setProvidedDescription("Manual meeting corrected by app");
        meetingDto.setNoCollisions(true);
        meetingDto.setType(MeetingType.MENTORING);
    }

    private Meeting arrange(MeetingDto meetingDto) {
       //todo for future when first lessons finished
       /* Student student = studentRepository.getByEmail(meetingDto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("Student not found: " + meetingDto.getStudentName() + " " + meetingDto.getEmail()));*/
        Student student = studentRepository.getByEmail(meetingDto.getEmail()).orElse(null);
        if (student == null) {
            student = new Student(0,meetingDto.getStudentName(), meetingDto.getEmail(), StudentRang.ASKED,"");
            eventPublisher.publishEvent(new NewStudentEvent(student));
        }

        Meeting meeting = new Meeting(meetingDto.getStart(), meetingDto.getEnd());
        meeting.setDetails(new Meeting.Details(formatSummary(student.getName()),
                "Spotkanie umówione automatycznie",
                meetingDto.getProvidedDescription(), meetingDto.getEmail())
        ).setNoCollisions(meetingDto.isNoCollisions());
        if (student.getRank() == StudentRang.ASKED) {
            meeting.setType(MeetingType.FIRST_MENTORING);
        }
        arrangeMeetingDomainService.arrange(meeting);
        eventPublisher.publishEvent(new NewMeetingEvent(meeting, student));
        return meeting;
    }

    private String formatSummary(String studentName) {
        return studentName + " " + params.values().summaryPrefix() + params.values().ownerName();
    }
}
