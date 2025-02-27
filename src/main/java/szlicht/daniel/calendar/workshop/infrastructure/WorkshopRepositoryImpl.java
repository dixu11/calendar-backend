package szlicht.daniel.calendar.workshop.infrastructure;

import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.workshop.app_core.Workshop;
import szlicht.daniel.calendar.workshop.app_core.WorkshopRepository;

import java.util.List;

@Component
public class WorkshopRepositoryImpl implements WorkshopRepository {
    private WorkshopJpaRepository workshopJpaRepository;

    public WorkshopRepositoryImpl(WorkshopJpaRepository workshopJpaRepository) {
        this.workshopJpaRepository = workshopJpaRepository;
    }

    @Override
    public List<Workshop> getWorkshops() {
        return workshopJpaRepository.findAll()
                .stream()
                .map(WorkshopEntity::toWorkshop)
                .toList();
    }
}
