package szlicht.daniel.calendar.meeting;

import szlicht.daniel.calendar.student.Student;

public class NewMeetingEvent {
    private Meeting meeting;
    private Student student;

    public NewMeetingEvent(Meeting meeting, Student student) {
        this.meeting = meeting;
        this.student = student;
    }

    public Meeting getMeeting() {
        return meeting;
    }
    public Student getStudent() {
        return student;
    }

}
