package edu.AF.UTMS.controllers;

import edu.AF.UTMS.dto.TimetableDTO;
import edu.AF.UTMS.models.StudentEnrollment;
import edu.AF.UTMS.services.StudentEnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
public class StudentEnrollmentController {
    @Autowired
    private StudentEnrollmentService studentEnrollmentService;

    // Students to enroll in a course
    @PostMapping("/student/enroll/{courseId}")
    public ResponseEntity<StudentEnrollment> enrollInCourse(@RequestBody String token, @PathVariable String courseId) {
        try {
            StudentEnrollment studentEnrollment = studentEnrollmentService.enrollStudentToCourse(token, courseId);
            if (studentEnrollment!=null)
                return new ResponseEntity<>(studentEnrollment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (RuntimeException re) {
            System.out.println(re.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // View enrolled course timetable
    @GetMapping("/student/timetables/{courseId}")
    public ResponseEntity<TimetableDTO> viewCourseTimetable(@RequestBody String token, @PathVariable String courseId) {
        TimetableDTO timetable = studentEnrollmentService.viewTimetablesForEnrolledCourse(token, courseId);
        if (timetable==null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(timetable, HttpStatus.OK);
    }

    // View All students enrollments
    @GetMapping("/faculty/view/all")
    public ResponseEntity<List<StudentEnrollment>> viewAllEnrollments() {
        List<StudentEnrollment> studentEnrollments = studentEnrollmentService.viewAllEnrollments();
        if (studentEnrollments == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(studentEnrollments, HttpStatus.OK);
    }

    // View enrollments of a specific student
    @GetMapping("/faculty/view/student/{studentId}")
    public ResponseEntity<StudentEnrollment> viewStudentEnrollments(@PathVariable String studentId) {
        StudentEnrollment studentEnrollment = studentEnrollmentService.viewSpecificStudentEnrollments(studentId);
        if (studentEnrollment == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(studentEnrollment, HttpStatus.OK);
    }

    // Enroll a student to new Courses
    @PutMapping("/faculty/update/addEnrollments/{enrollmentId}")
    public ResponseEntity<StudentEnrollment> updateStudentEnrollments(@RequestBody String[] newCoursesToEnroll, @PathVariable String enrollmentId) {
        StudentEnrollment updatedStudentEnrollment = studentEnrollmentService.updateByAddingNewEnrollments(newCoursesToEnroll, enrollmentId);
        if (updatedStudentEnrollment==null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(updatedStudentEnrollment, HttpStatus.CREATED);
    }

    // Remove a specific course from a student's enrollments
    @PutMapping("/faculty/remove/course/{courseId}")
    public ResponseEntity<StudentEnrollment> removeSpecificCourseEnrollment(@RequestBody String studentId, @PathVariable String courseId) {
        try {
            StudentEnrollment modifiedStudentEnrollment = studentEnrollmentService.removeSpecificCourseEnrollment(studentId, courseId);
            if (modifiedStudentEnrollment==null)
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(modifiedStudentEnrollment, HttpStatus.OK);
        } catch (NullPointerException npe) {
            System.out.println("Invalid Student Id or Course Id! --- "+npe.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Remove all enrollments of a student
    @DeleteMapping("/faculty/removeEnrollments/{enrollmentId}")
    public ResponseEntity removeStudentEnrollments(@PathVariable String enrollmentId) {
        boolean areRemovedEnrollments = studentEnrollmentService.removeStudentEnrollments(enrollmentId);
        if (!areRemovedEnrollments)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
