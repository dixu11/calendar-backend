package szlicht.daniel.calendar.student.infrastructure;

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
    @Column(unique = true)
    private String name;

    @PersistenceCreator
    StudentEntity() {
    }

    public StudentEntity(String name, String email) {
        this.name = name;
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
}
