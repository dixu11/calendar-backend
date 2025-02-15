package szlicht.daniel.calendar.student.appCore;

import jakarta.persistence.*;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.Objects;

@Entity
@Table(name = "students")
class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private String email;

    @PersistenceCreator
    StudentEntity() {
    }

    public StudentEntity(String email) {
        this.email = email;
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

    @Override
    public String toString() {
        return "StudentEntity{" +
                "id=" + id +
                ", email='" + email + '\'' +
                '}';
    }
}
