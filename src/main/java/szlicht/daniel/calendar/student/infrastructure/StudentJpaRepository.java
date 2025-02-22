package szlicht.daniel.calendar.student.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import szlicht.daniel.calendar.student.app_core.Student;

import java.util.Optional;
import java.util.Set;

interface StudentJpaRepository extends JpaRepository<StudentEntity,Integer> {
    @Query("select s.email from StudentEntity s")
    Set<String> findAllEmails();

    Optional<StudentEntity> getByEmail(String email);

    Optional<StudentEntity> getByName(String name);

    boolean existsByEmail(String email);
}
