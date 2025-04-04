package szlicht.daniel.calendar.dialog;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.common.java.JavaUtils;
import szlicht.daniel.calendar.common.java.NotImplementedException;
import szlicht.daniel.calendar.common.mail.EmailService;
import szlicht.daniel.calendar.common.spring.AppStartedEvent;
import szlicht.daniel.calendar.meeting.Logger;
import szlicht.daniel.calendar.meeting.*;
import szlicht.daniel.calendar.student.*;
import szlicht.daniel.calendar.workshop.WorkshopAppService;

import java.util.List;

import static szlicht.daniel.calendar.common.spring.ParamsProvider.params;

@Service
public class DialogAppService {
    private ApplicationEventPublisher publisher;
    private MeetingsSender meetingsSender;
    private CalendarAppService calendarAppService;
    private StudentRepository studentRepository;
    private EmailService emailService;
    private WorkshopAppService workshopAppService;
    private Logger logger;
    private DialogPresenter dialogPresenter;
    private List<DialogScenario> scenarios;
    private StudentAppService studentAppService;


    public DialogAppService(ApplicationEventPublisher publisher,
                            MeetingsSender meetingsSender,
                            CalendarAppService calendarAppService, StudentRepository studentRepository,
                            EmailService emailService, WorkshopAppService workshopAppService,
                            Logger logger, DialogPresenter dialogPresenter, List<DialogScenario> scenarios, StudentAppService studentAppService) {
        this.publisher = publisher;
        this.meetingsSender = meetingsSender;
        this.calendarAppService = calendarAppService;
        this.studentRepository = studentRepository;
        this.emailService = emailService;
        this.workshopAppService = workshopAppService;
        this.logger = logger;
        this.dialogPresenter = dialogPresenter;
        this.scenarios = scenarios;
        this.studentAppService = studentAppService;
    }

    @EventListener
    @Async
    public void sendTestEmailToOwner(AppStartedEvent e) {
        startNextPropositionsScenario(60, params.mail().owner());
       // startMentoringOfferScenario(new StudentStartMessageDto("Owner Name", params.mail().owner(), "test"));
    }

    @EventListener
    void arrangeManualMeeting(NextMonthMeetingsEvent event) {
        event.getMeetings().stream()
                .filter(Meeting::isManual)
                .forEach(meeting -> startArrangeScenario(meeting.toDto()));
    }

    public void processNewEmail(RawEmail rawEmail) {
        EmailParser emailParser = new EmailParser(rawEmail);
        studentAppService.addIfNotExists(emailParser.getEmail(),emailParser.getName(),emailParser.getMessage());
        DialogScenario foundScenario = scenarios.stream()
                .filter(scenario -> containsKeyword(rawEmail.subject(),scenario.keyword()))
                .findAny()
                .orElseThrow(); //todo for keyword not found
        foundScenario.runScenario(emailParser);

      /*  logger.notifyOwner("Mentoring offer sent to "+ message.getName(), "Mail: " +
                message.getEmail() + " story: " +message.getStory(), false);*/


       /* EmailData emailData = emailParser.parseEmail();
        switch (emailData.getScenarioType()) {
         *//*   case PROPOSITIONS:
                startNextPropositionsScenario(emailData.getMinutes(), emailData.getEmail());
                break;*//*
           *//* case ARRANGE:
                startArrangeScenario(emailData.getMeetingDto());
                break;*//*
           *//* case MENTORING:
                startMentoringOfferScenario(emailData.getStudentStartMessageDto());
                break;*//*
        *//*    case SOLO_MENTORING_OFFER:
                startSoloMentoringScenario(emailData);
                break;*//*
            case GROUP_MENTORING_OFFER:
                startGroupMentoringScenario(emailData);
            default:
                System.out.printf("(%s)%s don't mach to any patter so it's probably spam -> ignore\n%n",
                        rawEmail.email(), rawEmail.subject());
                //logger.notifyOwner("suspicious email: " + rawEmail.email() + " " + rawEmail.subject(), rawEmail.content(), false);
        }*/
    }

    private boolean containsKeyword(String subject, String keyword) {
        return subject.contains(keyword);
    }

    private void startGroupMentoringScenario(EmailData emailData) {
        dialogPresenter.showDialog(new WorkshopDialogView(emailData.getEmail(),workshopAppService.getWorkshops()), emailData);
        logger.notifyOwner("Group mentoring offer sent to "+ emailData.getName() + " mail:" + emailData.getEmail(),
                "response to decision: "+ emailData.getContent() , false);
    }

    private void startSoloMentoringScenario(EmailData emailData) {
        dialogPresenter.showDialog(new SoloMentoringDialogView(emailData.getEmail(),
                meetingsSender.getFormatedPropositions(calendarAppService.getPropositions(emailData.getMinutes()))),
                emailData);
        logger.notifyOwner("Solo mentoring offer sent to "+ emailData.getName() + " mail:" + emailData.getEmail(),
                "response to decision: "+ emailData.getContent() , false); //todo simplify and standardize
    }

    public void startNextPropositionsScenario(Integer minutes, String to) {
        try {
            Propositions propositions = calendarAppService.getPropositions(minutes);
            meetingsSender.sendPropositions(propositions, to);
        } catch (CalendarOfflineException e) {
            System.err.println("calendar offline");
        }
    }

    public void startFirstLessonEndPropositionsScenario(String email) {
        try {
            Propositions propositions = calendarAppService.getPropositions(params.values().minutes());
            String formatedPropositions = meetingsSender.getFormatedPropositions(propositions);
            String mailtoHours = meetingsSender.formatMailtoHours();
            EmailData emailData = EmailData.builder().email(email).build();
            dialogPresenter.showDialog(new AfterFirstMentoringDialogView(email, formatedPropositions, mailtoHours),
                    emailData);
        } catch (CalendarOfflineException e) {
            System.err.println("calendar offline");
        }
    }

    public void startArrangeScenario(MeetingDto meetingDto) {
        try {
            Meeting meeting = calendarAppService.arrangeMeeting(meetingDto);
            if (meeting.getType() == MeetingType.MENTORING) {
                meetingsSender.notifyArrangementComplete(meeting);
            } else {
                EmailData emailData = EmailData.builder().email(meetingDto.getEmail()).build();
                dialogPresenter.showDialog(new FirstMentoringDialogView(meetingDto.getEmail(), meeting),emailData);
            }
            System.err.println(meeting.getDetails().getEmail() + " meeting proposition at: " + meeting.when() + " approved");
        } catch (CalendarOfflineException | MeetingCollisionException e) {
            System.err.println(meetingDto.getEmail() + " meeting proposition at: " + meetingDto.getStart() + " declined");
            e.printStackTrace();
            meetingsSender.notifyArrangementFailed(meetingDto, e.getMessage());
        } catch (NotImplementedException e) {
            System.err.println(e.getMessage());
        } catch (IllegalArgumentException e){
            logger.notifyOwner("Arrangement failed: " +e.getMessage(),JavaUtils.getStackTrace(e),false);
        } catch (Exception e) {
            logger.notifyOwner("Unexpected error",
                    e.getMessage() + " " + JavaUtils.getStackTrace(e),
                    true);
            e.printStackTrace();
        }
    }

    public void startMentoringOfferScenario(StudentStartMessageDto message) {
        EmailData emailData = EmailData.builder().email(message.getEmail()).build();
        dialogPresenter.showDialog(new MentoringDialogView(message.getEmail()),emailData);
    }
}
