package szlicht.daniel.calendar.repository;

import jakarta.persistence.*;
import org.springframework.data.annotation.PersistenceCreator;
import szlicht.daniel.calendar.student.Student;
import szlicht.daniel.calendar.student.StudentRang;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "students")
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private String email;
    private String name;
    private String nick;
    private StudentRang rang;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String story;
    private LocalDateTime created;

    @PersistenceCreator
    StudentEntity() {
    }

    public StudentEntity(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.nick = student.getNick();
        this.email = student.getEmail();
        this.rang = student.getRank();
        this.story = student.getStory();
        created = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentEntity that = (StudentEntity) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

    public Student toStudent() {
        return new Student(id,name,email,rang,story);
    }
}
