package szlicht.daniel.calendar.student.appCore;

import java.util.Set;

public interface StudentRepository {
    void addIfNotExists(Set<Student> students);
}
