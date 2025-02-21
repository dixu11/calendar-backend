package szlicht.daniel.calendar.dialog.app_core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;
import org.springframework.context.ApplicationEventPublisher;
import szlicht.daniel.calendar.common.mail.EmailService;
import szlicht.daniel.calendar.common.spring.ParamsProvider;
import szlicht.daniel.calendar.common.spring.Logger;
import szlicht.daniel.calendar.meeting.app_core.*;
import szlicht.daniel.calendar.student.app_core.StudentRepository;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

class DialogAppServiceTest {
    private static final String EMAIL = "jan.kowalski@gmail.com";
    private static final String NAME = "Jan Kowalski";

    @Mock
    private StartMessageRepository startMessageRepository;
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
        ArrangeMeetingDomainService arrangeMeetingDomainService = new ArrangeMeetingDomainService(propositionsDomainService, calendarRepository, logger, publisher);
        CalendarAppService calendarAppService = new CalendarAppService(propositionsDomainService, arrangeMeetingDomainService, studentRepository, logger, publisher, calendarRepository);
        dialogAppService = new DialogAppService(
                startMessageRepository,
                publisher,
                meetingsSender,
                calendarAppService,
                emailService,
                logger
        );
    }

    private void initParams() {
        MeetingParams meetingParams = new MeetingParams(new MeetingParams.Mail("","me@gmail.com",""),
                new MeetingParams.Values(90,"Europe/Warsaw", List.of(1.,1.5,2.,2.5,3.),"Mentoring IT z ","DS",
                        new MeetingParams.Values.WorkHours(LocalTime.of(11,15),LocalTime.of(15,45),
                                new HashMap<>())),
                new MeetingParams.Keywords("Moje uwagi:","terminy","mentoring","spotkanie","terminy 1")
                );
        params = meetingParams;
    }

    @Test
    public void testInitiation() {
        System.out.println("Context loads");
    }

    @Test
    public void createCorrectPropositions() {
        dialogAppService.processNewEmail(new RawEmail(EMAIL,NAME,"terminy 1",""));
        verify(emailService).sendHtmlEmail(
                Mockito.eq(EMAIL),
                Mockito.any()
                , contains("pon.  24.02  14:45 - 15:45")
        );
    }

    @Test
    public void receiveOfferSendsStoryToOwner() {
        String story = "test";
        String name = "<NAME>";
        dialogAppService.startMentoringOfferScenario(new StudentStartMessageDto(name, EMAIL, story));
        verify(logger).notifyOwner(
                contains(name),
                argThat(arg -> arg.contains(story) && arg.contains(EMAIL)),
                Mockito.any(boolean.class));
    }

    @Test
    public void sendsOffer() {
        String story = "test";
        String name = "<NAME>";
        dialogAppService.startMentoringOfferScenario(new StudentStartMessageDto(name, EMAIL, story));
        verify(emailService).sendHtmlEmail(
                Mockito.eq(EMAIL),
                Mockito.any()
                , argThat(arg -> arg.contains("Uczę się absolutnie od zera, szukam kompleksowego " +
                        "wsparcia i pomocy przy wyznaczeniu ścieżki.") && arg.contains("Prawdopodobnie nikt w polsce nie" +
                        " ma tak dużego doświadczenia w nauczaniu programowania, " +
                        "od ponad 5 lat uczę w licznych szkołach i indywidualnie, "))
        );
    }

    @Test
    public void reactionToSpamEmailIsCorrect() {
        String subject = "hello";
        String content = "hello world";
        dialogAppService.processNewEmail(new RawEmail(EMAIL, "??", subject, content));
        verify(logger).notifyOwner(
                contains(EMAIL),
                contains(content),
                Mockito.any(boolean.class));
        verifyNoInteractions(emailService);
    }

}