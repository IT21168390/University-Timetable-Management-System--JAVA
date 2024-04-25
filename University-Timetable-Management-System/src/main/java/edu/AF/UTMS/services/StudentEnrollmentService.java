package edu.AF.UTMS.services;

import edu.AF.UTMS.dto.TimetableDTO;
import edu.AF.UTMS.models.StudentEnrollment;

import java.util.List;

public interface StudentEnrollmentService {
    StudentEnrollment enrollStudentToCourse(String token, String courseId);
    TimetableDTO viewTimetablesForEnrolledCourse(String token, String courseId);

    // Features for the Admins/Faculty
    List<StudentEnrollment> viewAllEnrollments();
    boolean removeStudentEnrollments(String studentId);
    StudentEnrollment viewSpecificStudentEnrollments(String studentId);
    StudentEnrollment removeSpecificCourseEnrollment(String studentId, String courseId);
    StudentEnrollment updateByAddingNewEnrollments(String[] coursesToEnroll, String enrollmentId);
}
