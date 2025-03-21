package szlicht.daniel.calendar.repository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import szlicht.daniel.calendar.student.Student;
import szlicht.daniel.calendar.student.StudentRepository;

import java.util.Optional;

@Service
public class StudentRepositoryImpl implements StudentRepository {
    private StudentJpaRepository studentJpaRepository;

    public StudentRepositoryImpl(StudentJpaRepository studentJpaRepository) {
        this.studentJpaRepository = studentJpaRepository;
    }

    @Override
    @Transactional
    public Optional<Student> getByEmail(String email) {
        return studentJpaRepository.getByEmail(email)
                .map(StudentEntity::toStudent);
    }

    @Override
    @Transactional
    public Optional<Student> getByName(String name) {
        return studentJpaRepository.getByName(name)
                .map(StudentEntity::toStudent);
    }

    @Override
    @Transactional
    public void saveOrUpdate(Student student) {
        StudentEntity studentEntity = new StudentEntity(student);
        Optional<StudentEntity> existingEntity = studentJpaRepository.getByEmail(student.getEmail());
        existingEntity.ifPresent(entity -> studentEntity.setId(entity.getId()));
        studentJpaRepository.save(studentEntity);
    }

    @Transactional
    public boolean save(Student student) {
        if (studentJpaRepository.existsByEmail(student.getEmail())) {
            return false;
        }
        studentJpaRepository.save(new StudentEntity(student));
        return true;
    }

    @Override
    public boolean existsByEmail(String email) {
        return studentJpaRepository.existsByEmail(email);
    }
}
