package szlicht.daniel.calendar.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import szlicht.daniel.calendar.workshop.WorkshopParticipation;

import java.time.LocalDate;
import java.util.List;

@Entity
public class WorkshopParticipationEntity {
    @Id
    @GeneratedValue
    private int id;
    private int studentId;
    private List<LocalDate> monthsPayed;

    public WorkshopParticipationEntity(WorkshopParticipation participation) {
        this.studentId = participation.getStudentId();
        this.monthsPayed = participation.getMonthsPayed();
    }

    public WorkshopParticipation toBean() {
        return new WorkshopParticipation(studentId,monthsPayed);
    }
}
