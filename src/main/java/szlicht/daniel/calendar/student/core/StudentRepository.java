package szlicht.daniel.calendar.student.core;

import org.springframework.data.jpa.repository.JpaRepository;

interface StudentRepository extends JpaRepository<StudentEntity,Integer> {
    boolean existsByEmail(String email);
}
