package szlicht.daniel.calendar.student;

import java.util.Optional;

public interface StudentRepository {
    Optional<Student> getByEmail(String email);
    Optional<Student> getByName(String name);
    void saveOrUpdate(Student student);
    boolean save(Student student);
    boolean existsByEmail(String email);
}
