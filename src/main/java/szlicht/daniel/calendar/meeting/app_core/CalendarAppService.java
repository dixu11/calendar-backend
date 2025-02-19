package szlicht.daniel.calendar.meeting.app_core;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.java.JavaUtils;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;
import szlicht.daniel.calendar.common.spring.WarningLogger;
import szlicht.daniel.calendar.meeting.infrastructure.GoogleCalendarRepository;
import szlicht.daniel.calendar.student.app_core.NewStudentEvent;
import szlicht.daniel.calendar.student.app_core.Student;
import szlicht.daniel.calendar.student.app_core.StudentRang;
import szlicht.daniel.calendar.student.app_core.StudentRepository;

import java.util.Collections;
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
    private GoogleCalendarRepository googleCalendarRepository; //todo remove

    public CalendarAppService(PropositionsDomainService propositionsDomainService,
                              ArrangeMeetingDomainService arrangeMeetingDomainService, StudentRepository studentRepository,
                              MeetingsSender meetingsSender,
                              WarningLogger warningLogger, ApplicationEventPublisher eventPublisher, CalendarRepository calendarRepository, GoogleCalendarRepository googleCalendarRepository) {
        this.propositionsDomainService = propositionsDomainService;
        this.arrangeMeetingDomainService = arrangeMeetingDomainService;
        this.studentRepository = studentRepository;
        this.meetingsSender = meetingsSender;
        this.warningLogger = warningLogger;
        this.eventPublisher = eventPublisher;
        this.calendarRepository = calendarRepository;
        this.googleCalendarRepository = googleCalendarRepository;
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
        if (meeting.getType() == MeetingType.CYCLIC_MENTORING) {
            return;
        }
        if (!meeting.getDetails().getEmail().isBlank()) {
            studentEmail = meeting.getDetails().getEmail();
        } else {
            studentEmail = studentRepository.getByName(meeting.getDetails().getSummary()).map(Student::getEmail).orElse("");
        }
        String studentName = meeting.getDetails().getSummary();
        Student student = new Student(studentName, studentEmail, StudentRang.HAD_MENTORING);
        if (studentEmail.isBlank()) {
            return;
        }
        meeting.getDetails().setEmail(student.getEmail());
        meeting.getDetails().setOwnerDescription("Manual meeting corrected by app");
        meeting.getDetails().setSummary(formatSummary(student.getName()));
        meeting.setType(MeetingType.MENTORING);
        System.out.println("Manual meeting corrected by app: "+ meeting.getDetails().getSummary() + " at " + meeting.when());
        calendarRepository.removeMeetingById(meeting.getId());
        warningLogger.notifyOwner("Event deleted at cleanup: " +meeting.getDetails().getSummary() + " at " + meeting.when(), "", false);
        MeetingDto meetingDto = meeting.toDto();
        meetingDto.setStudentName(student.getName());
        meeting.setId(null);
        calendarRepository.save(meeting);
        meetingsSender.notifyArrangementComplete(meeting);
        studentRepository.addIfNotExists(Collections.singleton(student));
    }

    public void arrangeMeeting(MeetingDto meetingDto){
        String studentName = Student.formatStudentName(meetingDto.getStudentName());
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
            eventPublisher.publishEvent(new NewStudentEvent(
                    new Student(meetingDto.getStudentName(), meetingDto.getEmail(),StudentRang.HAD_MENTORING)));
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

    private String formatSummary(String studentName) {
        return studentName + " " + params.values().summaryPrefix() + params.values().ownerName();
    }
}
