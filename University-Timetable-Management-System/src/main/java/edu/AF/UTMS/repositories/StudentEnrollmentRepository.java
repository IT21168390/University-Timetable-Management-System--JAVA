package edu.AF.UTMS.repositories;

import edu.AF.UTMS.models.StudentEnrollment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentEnrollmentRepository extends MongoRepository<StudentEnrollment, String> {
    StudentEnrollment findByStudentId(String stdId);
    boolean existsByStudentId(String stdId);
    boolean existsByEnrollmentsCourseId(String courseId);
    boolean existsByIdAndEnrollmentsCourseId(String id, String courseId);
    List<StudentEnrollment> findAllByEnrollmentsCourseId(String courseId);
}
