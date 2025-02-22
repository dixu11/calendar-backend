package szlicht.daniel.calendar.student.infrastructure;

import jakarta.persistence.*;
import org.springframework.data.annotation.PersistenceCreator;
import szlicht.daniel.calendar.student.app_core.Student;
import szlicht.daniel.calendar.student.app_core.StudentRang;

import java.util.Objects;

@Entity
@Table(name = "students")
class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private String email;
    private String name;
    private String nick;
    private StudentRang rang;

    @PersistenceCreator
    StudentEntity() {
    }

    public StudentEntity(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.nick = student.getNick();
        this.email = student.getEmail();
        this.rang = student.getRank();
    }

    public StudentEntity(String name, String email, StudentRang rang) {
        this.name = name;
        this.email = email;
        this.rang = rang;
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
        return new Student(id,name,email,rang);
    }
}
