package szlicht.daniel.calendar.workshop;

import java.util.List;
import java.util.Optional;

public interface WorkshopRepository {
    List<Workshop> getWorkshops();

    Optional<Workshop> findWorkshopById(int workshopId);

    void save(Workshop workshop);
}
