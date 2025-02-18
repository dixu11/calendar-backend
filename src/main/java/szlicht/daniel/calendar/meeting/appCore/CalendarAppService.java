package szlicht.daniel.calendar.meeting.appCore;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.java.JavaUtils;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;
import szlicht.daniel.calendar.student.appCore.NewStudentEvent;
import szlicht.daniel.calendar.student.appCore.Student;
import szlicht.daniel.calendar.student.appCore.StudentRepository;

import java.util.Optional;
import java.util.Set;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;


@Service
public class CalendarAppService {
    private PropositionsDomainService propositionsDomainService;
    private ArrangeMeetingDomainService arrangeMeetingDomainService;
    private StudentRepository studentRepository;
    private MeetingsSender meetingsSender;
    private WarningLogger warningLogger;
    private final ApplicationEventPublisher eventPublisher;
    private CalendarRepository calendarRepository;

    public CalendarAppService(PropositionsDomainService propositionsDomainService,
                              ArrangeMeetingDomainService arrangeMeetingDomainService, StudentRepository studentRepository,
                              MeetingsSender meetingsSender,
                              WarningLogger warningLogger, ApplicationEventPublisher eventPublisher, CalendarRepository calendarRepository) {
        this.propositionsDomainService = propositionsDomainService;
        this.arrangeMeetingDomainService = arrangeMeetingDomainService;
        this.studentRepository = studentRepository;
        this.meetingsSender = meetingsSender;
        this.warningLogger = warningLogger;
        this.eventPublisher = eventPublisher;
        this.calendarRepository = calendarRepository;
    }

    @EventListener
    @Async
    public void handle(AppStartedEvent e) {
        sendPropositions(60, params.mail().owner());
    }

    public void sendPropositions(Integer minutes, String to) {
        try {
            Propositions meetingPropositions = getMeetingPropositions(minutes);
            meetingsSender.sendPropositions(meetingPropositions, to);
        } catch (CalendarOfflineException e) {
            System.err.println("calendar offline");
        }
    }

    public Propositions getMeetingPropositions(Integer minutes) {
        return propositionsDomainService.createMeetingPropositions(minutes);
    }
    @EventListener
    void arrangeManualMeeting(NextMonthMeetingsEvent event){
        event.getMeetings().stream()
                .filter(Meeting::isManual)
                .forEach(this::arrangeManualMeeting);
    }

    private void arrangeManualMeeting(Meeting meeting) {
        String studentEmail = "";
        if (!meeting.getDetails().getEmail().isBlank()) {
            studentEmail = meeting.getDetails().getEmail();
        } else {
            studentEmail = studentRepository.getByName(meeting.getDetails().getSummary()).map(Student::getEmail).orElse("");
        }
        String studentName = formatStudentName(meeting.getDetails().getSummary());
        if (studentEmail.isBlank()) {
            return;
        }
        meeting.getDetails().setEmail(studentEmail);
        meeting.getDetails().setOwnerDescription("Manual meeting corrected by app");
        meeting.getDetails().setSummary(formatSummary(studentName));
        meeting.setType(MeetingType.MENTORING);
        System.out.println("Manual meeting corrected by app: "+ meeting.getDetails().getSummary() + " at " + meeting.when());
        calendarRepository.removeMeetingById(meeting.getId());
        warningLogger.notifyOwner("Event deleted at cleanup: " +meeting.getDetails().getSummary() + " at " + meeting.when(), "", false);
        MeetingDto meetingDto = meeting.toDto();
        meetingDto.setStudentName(studentName);
        meeting.setId(null);
        calendarRepository.save(meeting);
        meetingsSender.notifyArrangementComplete(meeting);
        studentRepository.addIfNotExists(Set.of(new Student(studentName, studentEmail)));
    }

    public void arrangeMeeting(MeetingDto meetingDto){
        String studentName = formatStudentName(meetingDto.getStudentName());
        meetingDto.setStudentName(studentName);
        Optional<Student> studentOptional = studentRepository.getByEmail(meetingDto.getEmail());
        if (studentOptional.isPresent()) {
            arrangeNextMeeting(meetingDto);
        }else{
            arrangeFirstMeeting(meetingDto);
        }
    }

    private void arrangeFirstMeeting(MeetingDto meetingDto) {
        boolean success = arrange(meetingDto);
        if (success) {
            eventPublisher.publishEvent(new NewStudentEvent(new Student(meetingDto.getStudentName(), meetingDto.getEmail())));
        }
    }

    private void arrangeNextMeeting(MeetingDto meetingDto) {
        arrange(meetingDto);
    }

    private boolean arrange(MeetingDto meetingDto) {
        Meeting meeting = new Meeting(meetingDto.getStart(),meetingDto.getEnd());
        meeting.setDetails(new Meeting.Details(formatSummary(meetingDto.getStudentName()),
                "Spotkanie umówione automatycznie",
                meetingDto.getProvidedDescription(), meetingDto.getEmail())
        );
        try {
            arrangeMeetingDomainService.arrange(meeting);
            meetingsSender.notifyArrangementComplete(meeting);
            System.err.println(meeting.getDetails().getEmail() + " meeting proposition at: " + meeting.when() + " approved");
            return true;
        } catch (CalendarOfflineException | MeetingCollisionException e) {
            System.err.println(meetingDto.getEmail() + " meeting proposition at: " + meeting.when() + " declined");
            e.printStackTrace();
            meetingsSender.notifyArrangementFailed(meeting, e.getMessage());
        } catch (Exception e) {
            warningLogger.notifyOwner("Unexpected error",
                    e.getMessage() + " " + JavaUtils.getStackTrace(e),
                    true);
            e.printStackTrace();
        }
        return false;
    }

    private String formatStudentName(String name) {
        String[] split = name.split(" ");
        if (split.length == 2 && split[1].length() > 3) {
            name = split[0] + " " + split[1].substring(0, 3);
        }
        return name;
    }

    private String formatSummary(String studentName) {
        return studentName + " " + params.values().summaryPrefix() + params.values().ownerName();
    }
}
