package szlicht.daniel.calendar.repository;

import jakarta.persistence.*;
import szlicht.daniel.calendar.workshop.Workshop;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name ="workshops")
public class WorkshopEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "workshops_students",
            joinColumns = @JoinColumn(name = "workshop_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<StudentEntity> students;
    private LocalDateTime start;
    private String title;

    public Workshop toWorkshop() {
        return new Workshop(id, start,title,
                students.stream()
                        .map(StudentEntity::toStudent)
                        .toList());
    }
}
