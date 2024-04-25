package edu.AF.UTMS.repositories;

import edu.AF.UTMS.models.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    Course findFirstById(String id);
    Course findByCode(String courseCode);
}
