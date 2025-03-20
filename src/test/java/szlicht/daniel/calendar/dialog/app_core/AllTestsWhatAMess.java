package szlicht.daniel.calendar.dialog.app_core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import szlicht.daniel.calendar.common.mail.EmailService;
import szlicht.daniel.calendar.dialog.*;
import szlicht.daniel.calendar.meeting.Logger;
import szlicht.daniel.calendar.meeting.*;
import szlicht.daniel.calendar.presenter.EmailDialogPresenter;
import szlicht.daniel.calendar.student.*;
import szlicht.daniel.calendar.workshop.Workshop;
import szlicht.daniel.calendar.workshop.WorkshopAppService;
import szlicht.daniel.calendar.repository.WorkshopJpaRepository;
import szlicht.daniel.calendar.repository.WorkshopRepositoryImpl;
import szlicht.daniel.calendar.workshop.WorkshopRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

class AllTestsWhatAMess {
    private static final String EMAIL = "jan.kowalski@gmail.com";
    private static final String NAME = "Jan Kowalski";
    private static final String SHORT_NAME = "Jan Kow";

    @Mock
    private ApplicationEventPublisher publisher;
    private MeetingsSender meetingsSender;
    @Mock
    private EmailService emailService;
    @Mock
    private Logger logger;
    @Mock
    private CalendarRepository calendarRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private WorkshopRepository workshopRepository;
    private DialogAppService dialogAppService;
    private StudentAppService studentAppService;
    private WorkshopAppService workshopAppService;

    @BeforeEach
    void setup() {
        init();
        initParams();
    }

    private void init() {
        MockitoAnnotations.openMocks(this);
        meetingsSender = new MeetingsSender(emailService);
        PropositionsDomainService propositionsDomainService = new PropositionsDomainService(calendarRepository);
        ArrangeMeetingDomainService arrangeMeetingDomainService = new ArrangeMeetingDomainService(propositionsDomainService, calendarRepository, logger, publisher);
        CalendarAppService calendarAppService = new CalendarAppService(propositionsDomainService, arrangeMeetingDomainService, studentRepository, logger, publisher, calendarRepository);
        workshopAppService = new WorkshopAppService(workshopRepository);
        DialogPresenter dialogPresenter = new EmailDialogPresenter(emailService);
        dialogAppService = new DialogAppService(
                publisher,
                meetingsSender,
                calendarAppService,
                studentRepository,
                emailService,
                workshopAppService,
                logger,
                dialogPresenter
        );
        studentAppService = new StudentAppService(studentRepository,logger);

    }

    private void initParams() {
        MeetingParams meetingParams = new MeetingParams(new MeetingParams.Mail("", "me@gmail.com", "",""),
                new MeetingParams.Values("calendar id","Moje ceny wynoszą","Moje ceny wynoszą",90,
                        "Europe/Warsaw", List.of(1., 1.5, 2., 2.5, 3.), "Mentoring IT z ", "DS",
                        new MeetingParams.Values.WorkHours(LocalTime.of(11, 0), LocalTime.of(15, 45),
                                new HashMap<>())),
                new MeetingParams.Keywords("Moje uwagi:", "terminy", "mentoring", "spotkanie", "indywidualne lekcje","grupowe lekcje", "apply")
        );
        params = meetingParams;
    }

    @Test
    public void createCorrectPropositions() {
        dialogAppService.processNewEmail(new RawEmail(EMAIL, NAME, "terminy 1", ""));
        verify(emailService).sendHtmlEmail(
                Mockito.eq(EMAIL),
                any()
                , contains("pon.  24.02  14:45 - 15:45")
        );
    }

    @Test
    public void receiveOfferSendsStoryToOwner() {
        String story = "test";
        String name = "<NAME>";
        dialogAppService.startMentoringOfferScenario(StudentStartMessageDto.builder()
                .name(name)
                .email(EMAIL)
                .story(story)
                .build());
        verify(logger).notifyOwner(
                contains(name),
                argThat(arg -> arg.contains(story) && arg.contains(EMAIL)),
                any(boolean.class));
    }

    @Test
    public void sendsOffer() {
        doAnswer(invocation -> {
            Object event = invocation.getArgument(0);
            studentAppService.newStudentAdded((NewStudentEvent) event);
            return null;
        }).when(publisher).publishEvent(any(NewStudentEvent.class));

        String story = "test";
        String name = "<NAME>";
        dialogAppService.startMentoringOfferScenario(StudentStartMessageDto.builder()
                .name(name)
                .email(EMAIL)
                .story(story)
                .build());
        verify(emailService).sendHtmlEmail(
                eq(EMAIL),
                any()
                , argThat(arg -> arg.contains("Uczę się absolutnie od zera, szukam kompleksowego " +
                        "wsparcia i pomocy przy wyznaczeniu ścieżki.") && arg.contains("Prawdopodobnie nikt w polsce nie" +
                        " ma tak dużego doświadczenia w nauczaniu programowania, " +
                        "od ponad 5 lat uczę w licznych szkołach i indywidualnie, "))
        );
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentCaptor.capture());
        Student passedStudent = studentCaptor.getValue();
        assertEquals(name, passedStudent.getName());
        assertEquals(EMAIL, passedStudent.getEmail());
        assertEquals(story, passedStudent.getStory());
        assertEquals(StudentRang.ASKED, passedStudent.getRank());
    }

    @Test
    public void reactionToSpamEmailIsCorrect() {
        String subject = "hello";
        String content = "hello world";
        dialogAppService.processNewEmail(new RawEmail(EMAIL, "??", subject, content));
        verify(logger).notifyOwner(
                contains(EMAIL),
                contains(content),
                any(boolean.class));
        verifyNoInteractions(emailService);
    }

    @Test
    public void reactToSoloMentoringRequest() {
        String subject = params.keywords().soloMentoring();
        String content = "2";
        dialogAppService.processNewEmail(new RawEmail(EMAIL, NAME, subject, content));
        verify(logger).notifyOwner(
                argThat(arg -> arg.contains(EMAIL) && arg.contains(NAME)),
                contains(content),
                any(boolean.class));
        verify(emailService).sendHtmlEmail(
                eq(EMAIL),
                any()
                , argThat(arg -> arg.contains("pon.  24.02  14:45 - 15:45")
                        && arg.contains("200zł")
                        && arg.contains("55")
                        && arg.contains("sprawdź tańsze opcje"))
        );
    }

    //arrangements
    @Test
    public void arrangeFirstMeeting() {
        putThisStudentToDb(StudentRang.ASKED);
        MeetingDto firstMeeting = MeetingDto.builder()
                .email(EMAIL)
                .studentName(NAME)
                .start(LocalDateTime.parse("2025-02-24T14:45"))
                .end(LocalDateTime.parse("2025-02-24T15:45"))
                .build();

        dialogAppService.startArrangeScenario(firstMeeting);
        verify(logger).notifyOwner(
                contains("Umówił się: jan.kowalski@gmail.com at 24.02 14:45-15:45"),
                any(),
                any(boolean.class)
        );
        verify(emailService).sendHtmlEmail(
                eq(EMAIL),
                any(),
                argThat(arg -> arg.contains("AnyDesk")
                        && arg.contains("14:45")
                        && arg.contains("55")
                        && arg.contains("Skype")
                )
        );
    }

    private void putThisStudentToDb(StudentRang studentRang) {
        when(studentRepository.getByEmail(EMAIL))
                .thenReturn(Optional.of(new Student(0, NAME, EMAIL, studentRang, "")));
    }

    @Test
    public void arrangeNextMeeting() {
        putThisStudentToDb(StudentRang.HAD_MENTORING);
        MeetingDto meeting = MeetingDto.builder()
                .email(EMAIL)
                .start(LocalDateTime.parse("2025-02-24T14:45"))
                .end(LocalDateTime.parse("2025-02-24T15:45"))
                .build();

        dialogAppService.startArrangeScenario(meeting);
        verify(logger).notifyOwner(
                contains("Umówił się: jan.kowalski@gmail.com at 24.02 14:45-15:45"),
                any(),
                any(boolean.class)
        );
    }

    @Test
    public void arrangeManualFirstMeetingWithMail() {
        String id = "123";
        putThisStudentToDb(StudentRang.ASKED);

        MeetingDto meeting = MeetingDto.builder()
                .id(id)
                .email(EMAIL)
                .start(LocalDateTime.parse("2025-02-24T14:45"))
                .end(LocalDateTime.parse("2025-02-24T15:45"))
                .build();
        Meeting colidingMeeting = new Meeting(LocalDateTime.parse("2025-02-24T14:15"), LocalDateTime.parse("2025-02-24T15:15"));
        when(calendarRepository.getMonthFromNowEvents()).thenReturn(Set.of(colidingMeeting));

        dialogAppService.startArrangeScenario(meeting);
        verify(logger).notifyOwner(
                contains("Umówił się: jan.kowalski@gmail.com at 24.02 14:45-15:45"),
                any(),
                any(boolean.class)
        );
        verify(calendarRepository).removeMeetingById(id);
        ArgumentCaptor<Meeting> meetingCaptor = ArgumentCaptor.forClass(Meeting.class);
        verify(calendarRepository).save(meetingCaptor.capture());

        Meeting capturedMeeting = meetingCaptor.getValue();
        assertEquals("2025-02-24T14:45", capturedMeeting.getStart().toString());
        assertEquals("2025-02-24T15:45", capturedMeeting.getEnd().toString());
        assertEquals(EMAIL, capturedMeeting.getDetails().getEmail());
        assertTrue(capturedMeeting.getDetails().getSummary().contains(SHORT_NAME));
    }

    @Test
    public void testForWeronika() {
        putThisStudentToDb(StudentRang.HAD_MENTORING);
        Meeting otherMeeting = new Meeting(
                LocalDateTime.parse("2025-03-01T13:15"),
                LocalDateTime.parse("2025-03-01T14:45"));
        when(calendarRepository.getMonthFromNowEvents())
                .thenReturn(Set.of(otherMeeting));

        MeetingDto weronikaMeeting = MeetingDto.builder()
                .email(EMAIL)
                .start(LocalDateTime.parse("2025-03-01T11:00"))
                .end(LocalDateTime.parse("2025-03-01T13:00"))
                .build();

        dialogAppService.startArrangeScenario(weronikaMeeting);

        verify(logger).notifyOwner(
                contains(String.format("Umówił się: %s at 01.03 11:00-13:00", EMAIL)),
                any(),
                any(boolean.class)
        );
    }

    @Test
    public void applyForWorkshop(){
        Workshop workshop = createWorkshop();
        assertEquals(0,workshop.appliedCount());
        workshopAppService.apply(1, 1);
        assertEquals(1,workshop.appliedCount());
    }

    @Test
    public void payForWorkshop() {
        Workshop workshop = createWorkshop();
        assertEquals(0,workshop.paidCountNextThisMonth());
        workshopAppService.apply(1, 1);
        assertEquals(0,workshop.paidCountNextThisMonth());
    }

    private Workshop createWorkshop() {
        LocalDate startNextMonth = LocalDate.now()
                .withDayOfMonth(1).plusMonths(1);
        Workshop workshop = new Workshop(1, startNextMonth,"test workshop");
        when(workshopRepository.findWorkshopById(1)).thenReturn(Optional.of(workshop));
        return workshop;
    }

}