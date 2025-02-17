package szlicht.daniel.calendar.student.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import szlicht.daniel.calendar.student.appCore.Student;

import java.util.Optional;
import java.util.Set;

interface StudentJpaRepository extends JpaRepository<StudentEntity,Integer> {
    @Query("select s.email from StudentEntity s")
    Set<String> findAllEmails();

    Optional<Student> getByEmail(String email);
}
