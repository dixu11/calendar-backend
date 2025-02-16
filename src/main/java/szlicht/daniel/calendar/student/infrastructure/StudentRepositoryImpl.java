package szlicht.daniel.calendar.student.infrastructure;

import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.student.appCore.Student;
import szlicht.daniel.calendar.student.appCore.StudentRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentRepositoryImpl implements StudentRepository {
    private StudentJpaRepository studentJpaRepository;

    public StudentRepositoryImpl(StudentJpaRepository studentJpaRepository) {
        this.studentJpaRepository = studentJpaRepository;
    }

    @Override
    public void addIfNotExists(Set<Student> students) {
        Set<String> existingMails = studentJpaRepository.findAllEmails();
        Set<StudentEntity> result = students.stream()
                .filter(student -> !existingMails.contains(student.getEmail()))
                .map(student -> new StudentEntity(student.getName(),student.getEmail()))
                .collect(Collectors.toSet());
        studentJpaRepository.saveAll(result);
    }
}
