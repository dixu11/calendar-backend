package szlicht.daniel.calendar.student.appCore;

public class NewStudentEvent {
    private Student student;

    public NewStudentEvent(Student student) {
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }
}
