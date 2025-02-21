package szlicht.daniel.calendar.dialog.app_core;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.java.JavaUtils;
import szlicht.daniel.calendar.common.mail.EmailService;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;
import szlicht.daniel.calendar.common.spring.Logger;
import szlicht.daniel.calendar.meeting.app_core.*;
import szlicht.daniel.calendar.student.app_core.NewStudentEvent;
import szlicht.daniel.calendar.student.app_core.Student;
import szlicht.daniel.calendar.student.app_core.StudentRang;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

@Service
public class DialogAppService {
    private StartMessageRepository startMessageRepository;
    private ApplicationEventPublisher publisher;
    private MeetingsSender meetingsSender;
    private CalendarAppService calendarAppService;
    private EmailService emailService;
    private Logger logger;


    public DialogAppService(StartMessageRepository startMessageRepository,
                            ApplicationEventPublisher publisher,
                            MeetingsSender meetingsSender,
                            CalendarAppService calendarAppService,
                            EmailService emailService,
                            Logger logger) {
        this.startMessageRepository = startMessageRepository;
        this.publisher = publisher;
        this.meetingsSender = meetingsSender;
        this.calendarAppService = calendarAppService;
        this.emailService = emailService;
        this.logger = logger;
    }

    @EventListener
    @Async
    public void sendTestEmailToOwner(AppStartedEvent e) {
//        startNextPropositionsScenario(60, params.mail().owner());
        startMentoringOfferScenario(new StudentStartMessageDto("<STUDENT NAME>", params.mail().owner(), "test"));
    }

    @EventListener
    void arrangeManualMeeting(NextMonthMeetingsEvent event) {
        event.getMeetings().stream()
                .filter(Meeting::isManual)
                .forEach(meeting -> {
                    boolean success = calendarAppService.arrangeManualMeeting(meeting);
                    if (success) {
                        meetingsSender.notifyArrangementComplete(meeting);
                    }
                });
    }

    public void processNewEmail(RawEmail rawEmail) {
        EmailData emailData = new EmailParser(rawEmail).parseEmail();
        switch (emailData.getDialogType()) {
            case PROPOSITIONS:
                startNextPropositionsScenario(emailData.getMinutes(), emailData.getEmail());
                break;
            case ARRANGE:
                startArrangeScenario(emailData.getMeetingDto());
                break;
            case OFFER:
                startMentoringOfferScenario(emailData.getStudentStartMessageDto());
                break;
            default:
                System.out.printf("(%s)%s don't mach to any patter so it's probably spam -> ignore\n%n",
                        rawEmail.email(), rawEmail.subject());
                logger.notifyOwner("suspicious email: " + rawEmail.email() + " " + rawEmail.subject(), rawEmail.content(), false);
        }
    }

    public void startArrangeScenario(MeetingDto meetingDto) {
        try {
            Meeting meeting = calendarAppService.arrangeMeeting(meetingDto);
            meetingsSender.notifyArrangementComplete(meeting);
            System.err.println(meeting.getDetails().getEmail() + " meeting proposition at: " + meeting.when() + " approved");
        } catch (CalendarOfflineException | MeetingCollisionException e) {
            System.err.println(meetingDto.getEmail() + " meeting proposition at: " + meetingDto.getStart() + " declined");
            e.printStackTrace();
            meetingsSender.notifyArrangementFailed(meetingDto, e.getMessage());
        } catch (Exception e) {
            logger.notifyOwner("Unexpected error",
                    e.getMessage() + " " + JavaUtils.getStackTrace(e),
                    true);
            e.printStackTrace();
        }
    }

    public void startNextPropositionsScenario(Integer minutes, String to) {
        try {
            Propositions propositions = calendarAppService.getPropositions(minutes);
            meetingsSender.sendPropositions(propositions, to);
        } catch (CalendarOfflineException e) {
            System.err.println("calendar offline");
        }
    }

    public void startMentoringOfferScenario(StudentStartMessageDto message) {
        HtmlDialog dialog = new StartMentoringHtmlDialog();
        emailService.sendHtmlEmail(message.getEmail(), dialog.getSubject(), dialog.getHtml());
        if (!startMessageRepository.existsByEmail(message.getEmail())) {
            startMessageRepository.save(message);
            publisher.publishEvent(new NewStudentEvent(
                    new Student(message.getName(), message.getEmail(), StudentRang.ASKED)));
        }
        logger.notifyOwner("Mentoring offer sent to "+ message.getName(), "Mail: " +
                message.getEmail() + " story: " +message.getStory(), false);
    }


}
