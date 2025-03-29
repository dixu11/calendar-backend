package szlicht.daniel.calendar.dialog;

import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.common.java.JavaUtils;
import szlicht.daniel.calendar.common.java.NotImplementedException;
import szlicht.daniel.calendar.meeting.*;

@Component
public class ArrangeScenario extends DialogScenario{
    private CalendarAppService calendarAppService;
    private MeetingsSender meetingsSender;
    private Logger logger;

    public ArrangeScenario(DialogPresenter dialogPresenter, CalendarAppService calendarAppService, MeetingsSender meetingsSender, Logger logger) {
        super(dialogPresenter);
        this.calendarAppService = calendarAppService;
        this.meetingsSender = meetingsSender;
        this.logger = logger;
    }

    @Override
    public String keyword() {
        return "spotkanie";
    }

    @Override
    public void runScenario(EmailParser emailParser) {
        MeetingDto meetingDto;
        try {
            meetingDto = emailParser.getArrangeData();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            logger.notifyOwner("Illegal argument exception in arrangement try",e.getMessage(),false);
            return;
        }

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
            logger.notifyOwner("Arrangement failed: " +e.getMessage(), JavaUtils.getStackTrace(e),false);
        } catch (Exception e) {
            logger.notifyOwner("Unexpected error",
                    e.getMessage() + " " + JavaUtils.getStackTrace(e),
                    true);
            e.printStackTrace();
        }
    }
}
