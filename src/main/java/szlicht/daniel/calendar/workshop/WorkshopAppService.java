package szlicht.daniel.calendar.workshop;

import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.student.Student;

import java.util.List;

@Service
public class WorkshopAppService {
    private WorkshopRepository workshopRepository;

    public WorkshopAppService(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }

    public void apply(int studentId, int workshopId){
        Workshop workshop = workshopRepository.findWorkshopById(workshopId).orElseThrow();
        workshop.newParticipation(studentId);
        workshopRepository.save(workshop);
    }

    public List<Workshop> getWorkshops() {
        return workshopRepository.getWorkshops();
    }
}
