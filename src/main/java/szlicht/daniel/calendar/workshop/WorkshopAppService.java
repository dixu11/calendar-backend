package szlicht.daniel.calendar.workshop;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkshopAppService {
    private WorkshopRepository workshopRepository;

    public WorkshopAppService(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }

    public List<Workshop> getWorkshops() {
        return workshopRepository.getWorkshops();
    }
}
