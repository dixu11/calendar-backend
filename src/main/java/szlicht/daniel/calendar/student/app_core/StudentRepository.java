package szlicht.daniel.calendar.student.app_core;

import java.util.Optional;
import java.util.Set;

public interface StudentRepository {
    void addIfNotExists(Set<Student> students);
    Optional<Student> getByEmail(String email);
    Optional<Student> getByName(String name);
    void save(Student student);
}
