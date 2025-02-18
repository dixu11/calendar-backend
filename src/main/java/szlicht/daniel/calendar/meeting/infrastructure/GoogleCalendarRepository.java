package szlicht.daniel.calendar.meeting.infrastructure;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.Events;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.meeting.app_core.CalendarOfflineException;
import szlicht.daniel.calendar.meeting.app_core.CalendarRepository;
import szlicht.daniel.calendar.meeting.app_core.Meeting;
import szlicht.daniel.calendar.meeting.app_core.MeetingType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static szlicht.daniel.calendar.common.calendar.GoogleCalendarUtils.*;
import static szlicht.daniel.calendar.common.calendar.GoogleCalendarUtils.toLocalDateTime;
import static szlicht.daniel.calendar.common.java.LocalDateUtils.nextMonthEnd;
import static szlicht.daniel.calendar.common.java.LocalDateUtils.tomorrowStart;

@Service
public class GoogleCalendarRepository implements CalendarRepository {

    private static final String CALENDAR_OTHER_ID = "primary";
    private static final String CALENDAR_MEETINGS_ID = "8jl5qj89qrqreh2ir4k24ole94@group.calendar.google.com";

    private Calendar calendar;

    public GoogleCalendarRepository(Calendar calendar) {
        this.calendar = calendar;
    }

    public void save(Meeting meeting) {
        Event event = toEvent(meeting);
        Event created = save(event);
        meeting.setId(created.getId());
    }

    public Event save(Event event) {
        try {
            return calendar.events().insert(CALENDAR_MEETINGS_ID, event).execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Set<Meeting> getMonthFromNowMeetings() {
        LocalDateTime from = tomorrowStart();
        LocalDateTime to = nextMonthEnd();
        return getMeetings(from, to,List.of(CALENDAR_MEETINGS_ID));
    }

    @Override
    public Set<Meeting> getMonthFromNowEvents() {
        LocalDateTime from = tomorrowStart();
        LocalDateTime to = nextMonthEnd();
        return getMeetings(from, to,List.of(CALENDAR_MEETINGS_ID,CALENDAR_OTHER_ID));
    }

    public Set<Meeting> getTodayMeetings() {
        LocalDateTime from = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime to = LocalDateTime.now().with(LocalTime.MAX);
        return new TreeSet<>(getMeetings(from, to, List.of(CALENDAR_MEETINGS_ID)));
    }

    @Override
    public void updateMeeting(Meeting meeting) { //currently updates only summary
        Event event = findEventById(meeting.getId());
        event.setSummary(meeting.getDetails().getSummary());
        try {
            calendar.events().update(CALENDAR_MEETINGS_ID, event.getId(), event).execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeMeetingById(String id) {
        try {
            calendar.events().delete(CALENDAR_MEETINGS_ID, id).execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Set<Meeting> getMeetings(LocalDateTime from, LocalDateTime to, List<String> calendarIds) {
        Set<Meeting> meetings = new TreeSet<>();
        calendarIds.forEach(calendarId -> meetings.addAll(getOneCalendarMeetings(calendarId, from, to)));
        return meetings;
    }

    private List<Meeting> getOneCalendarMeetings(String calendarId, LocalDateTime from, LocalDateTime to) {
        return getTimedEvents(getEvents(calendarId, from, to))
                .stream()
                .map(this::toMeeting)
                .toList();

    }

    private List<Event> getTimedEvents(Events newEvents) {
        return newEvents.getItems().stream()
                .filter(event -> event.getStart().getDateTime() != null)
                .toList();
    }

    private Events getEvents(String calendarId, LocalDateTime from, LocalDateTime to) {
        try {
            return calendar.events().list(calendarId)
                    .setMaxResults(100)
                    .setTimeMin(toDateTime(from))
                    .setTimeMax(toDateTime(to))
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CalendarOfflineException(e.getMessage());
        }
    }

    private Event toEvent(Meeting meeting) {
        Event event = new Event();
        event.setId(meeting.getId());
        event.setStart(toEventDateTime(meeting.getStart()));
        event.setEnd(toEventDateTime(meeting.getEnd()));
        event.setColorId(meeting.getType().getColor().getColorId());
        EventAttendee attendee = new EventAttendee().setEmail(meeting.getDetails().getEmail());
        event.setAttendees(Collections.singletonList(attendee));
        String summary = meeting.getDetails().getSummary();
        String description = meeting.getDetails().getOwnerDescription();
        if (!meeting.getDetails().getProvidedDescription().isBlank()) {
            description += "\n\n" + meeting.getDetails().getProvidedDescription();
            summary = "*" + summary;
        }
        event.setDescription(description);
        event.setSummary(summary);
        return event;
    }

    private Meeting toMeeting(Event event) {
        LocalDateTime start = toLocalDateTime(event.getStart().getDateTime());
        LocalDateTime end = toLocalDateTime(event.getEnd().getDateTime());
        String summary = "";
        String description = "";
        String email = "";
        String prividedDescription = "";

        if (event.getSummary() != null) {
            summary = event.getSummary();
        }
        if (event.getDescription() != null) {
            description = event.getDescription();
            if (description.startsWith("*")) {
                description = description.substring(1);
                String[] split = description.split("\n\n");
                if (split.length == 2) {
                    description = split[0];
                    prividedDescription = split[1];
                }
            }
        }
        if (event.getAttendees() != null) {
            email = event.getAttendees().get(0).getEmail();
        }
        return new Meeting(start, end)
                .setId(event.getId())
                .setType(MeetingType.fromColorId(event.getColorId()))
                .setDetails(new Meeting.Details(summary,description, prividedDescription, email));
    }

    public Event findEventById(String id) {
        try {
            return calendar.events().get(CALENDAR_MEETINGS_ID, id).execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
