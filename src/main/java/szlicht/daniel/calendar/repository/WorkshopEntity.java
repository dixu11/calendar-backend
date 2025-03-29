package szlicht.daniel.calendar.repository;

import jakarta.persistence.*;
import szlicht.daniel.calendar.workshop.Workshop;
import szlicht.daniel.calendar.workshop.WorkshopParticipation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name ="workshops")
public class WorkshopEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDate start;
    private String title;
    @OneToMany
    private List<WorkshopParticipationEntity> participations;

    public WorkshopEntity() {
    }

    public WorkshopEntity(Workshop workshop) {
        id = workshop.getId();
        start = workshop.getStartDate();
        title = workshop.getTitle();
        participations = workshop.getParticipations()
                .stream().map(WorkshopParticipationEntity::new)
                .toList();
    }

    public Workshop toWorkshop() {
        return new Workshop(id, start,title,
            participations.stream()
                    .map(WorkshopParticipationEntity::toBean)
                    .toList());
    }
}
