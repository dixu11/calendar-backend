package szlicht.daniel.calendar.workshop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkshopParticipation {
    private int studentId;
    private List<LocalDate> monthsPayed;

    public WorkshopParticipation(int studentId){
        this(studentId, new ArrayList<>());
    }

    public WorkshopParticipation(int studentId, List<LocalDate> monthsPayed) {
        this.studentId = studentId;
        this.monthsPayed = monthsPayed;
    }

    public boolean hasPaidNextMonth() {
        LocalDate nextMonth = LocalDate.now().plusMonths(1);
        return monthsPayed.stream()
                .anyMatch(date -> date.getMonth().equals(nextMonth.getMonth()) &&
                        date.getYear() == nextMonth.getYear());
    }

    public int getStudentId() {
        return studentId;
    }

    public List<LocalDate> getMonthsPayed() {
        return monthsPayed;
    }
}
