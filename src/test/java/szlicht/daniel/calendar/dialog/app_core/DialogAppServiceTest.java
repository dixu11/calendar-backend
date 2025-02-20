package szlicht.daniel.calendar.dialog.app_core;

import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import szlicht.daniel.calendar.common.mail.EmailService;
import szlicht.daniel.calendar.common.spring.ParamsProvider;
import szlicht.daniel.calendar.common.spring.WarningLogger;
import szlicht.daniel.calendar.meeting.app_core.*;
import szlicht.daniel.calendar.student.app_core.StudentRepository;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

class DialogAppServiceTest {
    private static final String EMAIL = "jan.kowalski@gmail.com";

    @Mock
    private StartMessageRepository startMessageRepository;
    @Mock
    private ApplicationEventPublisher publisher;
    private MeetingsSender meetingsSender;
    @Mock
    private EmailService emailService;
    @Mock
    private WarningLogger warningLogger;
    @Mock
    private CalendarRepository calendarRepository;
    @Mock
    private StudentRepository studentRepository;
    private DialogAppService dialogAppService;

    @BeforeEach
    void setup() {
        init();
        initParams();
    }

    private void init() {
        MockitoAnnotations.openMocks(this);
        meetingsSender = new MeetingsSender(emailService);
        PropositionsDomainService propositionsDomainService = new PropositionsDomainService(calendarRepository);
        ArrangeMeetingDomainService arrangeMeetingDomainService = new ArrangeMeetingDomainService(propositionsDomainService, calendarRepository, warningLogger, publisher);
        CalendarAppService calendarAppService = new CalendarAppService(propositionsDomainService, arrangeMeetingDomainService, studentRepository, warningLogger, publisher, calendarRepository);
        dialogAppService = new DialogAppService(
                startMessageRepository,
                publisher,
                meetingsSender,
                calendarAppService,
                emailService,
                warningLogger
        );
    }

    private void initParams() {
        MeetingParams meetingParams = new MeetingParams(new MeetingParams.Mail("","",""),
                new MeetingParams.Values(90,"Europe/Warsaw", List.of(1.,1.5,2.,2.5,3.),"Mentoring IT z ","DS",
                        new MeetingParams.Values.WorkHours(LocalTime.of(11,15),LocalTime.of(15,45),
                                new HashMap<>())),
                new MeetingParams.Keywords("Moje uwagi:","terminy","spotkanie")
                );
        ParamsProvider.params = meetingParams;
    }



    @Test
    public void testInitiation() {
        System.out.println("Context loads");
    }

    @Test
    public void createCorrectPropositions() {
        dialogAppService.startNextPropositionsScenario(90, EMAIL);

        Mockito.verify(emailService).sendHtmlEmail(
                Mockito.eq(EMAIL),
                Mockito.any()
                , Mockito.contains("pon.  24.02  14:15 - 15:45")
        );
    }


}