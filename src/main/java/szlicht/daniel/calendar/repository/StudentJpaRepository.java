package szlicht.daniel.calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

interface StudentJpaRepository extends JpaRepository<StudentEntity,Integer> {
    @Query("select s.email from StudentEntity s")
    Set<String> findAllEmails();

    Optional<StudentEntity> getByEmail(String email);

    Optional<StudentEntity> getByName(String name);

    boolean existsByEmail(String email);
}
