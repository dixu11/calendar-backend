package szlicht.daniel.calendar.student.infrastructure;

import org.springframework.stereotype.Service;
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
    public void addIfNotExists(Set<String> mails) {
        Set<String> existingMails = studentJpaRepository.findAllEmails();
        Set<StudentEntity> result = mails.stream()
                .filter(mail -> !existingMails.contains(mail))
                .map(StudentEntity::new)
                .collect(Collectors.toSet());
        System.out.println(result + " added");
        studentJpaRepository.saveAll(result);
    }
}
