package szlicht.daniel.calendar.repository;

import org.springframework.stereotype.Component;
import szlicht.daniel.calendar.workshop.Workshop;
import szlicht.daniel.calendar.workshop.WorkshopRepository;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Workshop> findWorkshopById(int workshopId) {
        return workshopJpaRepository.findById(workshopId)
                .map(WorkshopEntity::toWorkshop);
    }

    @Override
    public void save(Workshop workshop) {
        workshopJpaRepository.save(new WorkshopEntity(workshop));
    }
}
