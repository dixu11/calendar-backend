package szlicht.daniel.calendar.workshop.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkshopJpaRepository extends JpaRepository<WorkshopEntity,Integer> {
}
