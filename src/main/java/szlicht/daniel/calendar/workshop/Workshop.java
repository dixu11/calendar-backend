package szlicht.daniel.calendar.workshop;

import szlicht.daniel.calendar.student.Student;

import java.time.LocalDateTime;
import java.util.List;

public class Workshop {
    private int id;
    private LocalDateTime start;
    private String title;
    private List<Student> students;

    public Workshop(int id, LocalDateTime start,String title, List<Student> students) {
        this.id = id;
        this.start = start;
        this.title = title;
        this.students = students;
    }


    public LocalDateTime getStart() {
        return start;
    }

    public String getTitle() {
        return title;
    }

    public int size() {
        return students.size();
    }

    public int getId() {
        return id;
    }
}
