package szlicht.daniel.calendar.workshop;

import szlicht.daniel.calendar.repository.WorkshopParticipationEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Workshop {
    private int id;
    private LocalDate startDate;
    private String title;
    private List<WorkshopParticipation> participations;

    public Workshop(int id, LocalDate startDate, String titles) {
        this(id, startDate, titles, new ArrayList<>());
    }

    public Workshop(int id, LocalDate startDate, String title,List<WorkshopParticipation> participations) {
        this.id = id;
        this.startDate = startDate;
        this.title = title;
        this.participations = participations;
    }


    public void newParticipation(int studentId) {
        participations.add(new WorkshopParticipation(studentId));
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getTitle() {
        return title;
    }

    public int appliedCount() {
        return participations.size();
    }

    public int paidCountNextThisMonth() {
       return (int) participations.stream()
                .filter(WorkshopParticipation::hasPaidNextMonth)
                .count();
    }

    public int getId() {
        return id;
    }

    public List<WorkshopParticipation> getParticipations() {
        return participations;
    }
}
