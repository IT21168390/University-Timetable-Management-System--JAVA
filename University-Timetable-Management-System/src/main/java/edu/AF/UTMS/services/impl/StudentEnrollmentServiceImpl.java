package edu.AF.UTMS.services.impl;

import edu.AF.UTMS.converters.TimeTableDTOConverter;
import edu.AF.UTMS.dto.TimetableDTO;
import edu.AF.UTMS.models.*;
import edu.AF.UTMS.models.consts.UserRoles;
import edu.AF.UTMS.repositories.CourseRepository;
import edu.AF.UTMS.repositories.StudentEnrollmentRepository;
import edu.AF.UTMS.repositories.TimeTableRepository;
import edu.AF.UTMS.repositories.UserRepository;
import edu.AF.UTMS.services.JWTService;
import edu.AF.UTMS.services.StudentEnrollmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StudentEnrollmentServiceImpl implements StudentEnrollmentService {
    @Autowired
    private StudentEnrollmentRepository studentEnrollmentRepository;
    @Autowired
    private TimeTableRepository timeTableRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTService jwtService;

    private Logger logger = LoggerFactory.getLogger(StudentEnrollmentServiceImpl.class);

    @Autowired
    private TimeTableDTOConverter timeTableDTOConverter;

    // Enroll Student to a Course
    @Override
    public StudentEnrollment enrollStudentToCourse(String token, String courseId) {
        // Identify the logged-in user
        String userEmail = jwtService.extractUserName(token);
        User user = userRepository.findFirstByEmail(userEmail);

        if (user.getUserRole().equals(UserRoles.STUDENT)) {
            // Check whether if already there are any enrollments for this student, or not...
            if (studentEnrollmentRepository.existsByStudentId(user.getId())) {
                StudentEnrollment existingStudentEnrollments = studentEnrollmentRepository.findByStudentId(user.getId());

                try {
                    // Verify the selected Course validity
                    Course courseToEnroll = courseRepository.findFirstById(courseId);
                    // Verify that the Course trying to enroll in assigned for the student's faculty
                    if (!courseToEnroll.getFaculties().contains(user.getFaculty())){
                        logger.warn("The Course is not assigned for this Student's faculty!");
                        throw new RuntimeException("Cannot Enroll - The Course is not assigned for the faculty!");
                    }

                    else if (studentEnrollmentRepository.existsByIdAndEnrollmentsCourseId(existingStudentEnrollments.getId(), courseToEnroll.getId())) {
                        logger.warn("This user has been already enrolled to the selected Course!");
                        return null;
                    } else {
                        // Add the new enrollment since student has not already enrolled to it
                        StdEnrollment stdEnrollment = new StdEnrollment();
                        stdEnrollment.setCourseId(courseToEnroll.getId());
                        stdEnrollment.setCourseCode(courseToEnroll.getCode());
                        stdEnrollment.setCredits(courseToEnroll.getCredits());
                        stdEnrollment.setCourseName(courseToEnroll.getCourseName());
                        stdEnrollment.setCourseDescription(courseToEnroll.getDescription());

                        List<StdEnrollment> currentEnrollments = existingStudentEnrollments.getEnrollments();
                        currentEnrollments.add(stdEnrollment);
                        StudentEnrollment newEnrollment = studentEnrollmentRepository.save(existingStudentEnrollments);
                        return newEnrollment;
                    }
                } catch (NoSuchElementException e) {
                    logger.warn("Course not found for the given CourseID! : " + e.getMessage());
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                }
            }
            else {
                // Creating a new data entry for the Student, in StudentsEnrollments
                StudentEnrollment studentEnrollment = new StudentEnrollment();
                studentEnrollment.setStudentId(user.getId());
                studentEnrollment.setEmail(user.getEmail());
                studentEnrollment.setStudentFirstName(user.getFirstName());
                studentEnrollment.setStudentLastName(user.getLastName());

                // Inserting Courses to the Student's Enrollments list
                List<StdEnrollment> stdEnrollments = new ArrayList<>();
                try {
                    // Verify the selected Course validity
                    Course courseToEnroll = courseRepository.findFirstById(courseId);
                    // Verify that the Course trying to enroll in assigned for the student's faculty
                    if (!courseToEnroll.getFaculties().contains(user.getFaculty())){
                        logger.warn("The Course is not assigned for this Student's faculty!");
                        throw new RuntimeException("Cannot Enroll - The Course is not assigned for the faculty!");
                    }
                    StdEnrollment stdEnrollment = new StdEnrollment();
                    stdEnrollment.setCourseId(courseToEnroll.getId());
                    stdEnrollment.setCourseCode(courseToEnroll.getCode());
                    stdEnrollment.setCredits(courseToEnroll.getCredits());
                    stdEnrollment.setCourseName(courseToEnroll.getCourseName());
                    stdEnrollment.setCourseDescription(courseToEnroll.getDescription());

                    stdEnrollments.add(stdEnrollment);
                    studentEnrollment.setEnrollments(stdEnrollments);

                    StudentEnrollment newEnrollment = studentEnrollmentRepository.save(studentEnrollment);
                    return newEnrollment;
                } catch (NoSuchElementException e) {
                    logger.warn("Course not found for the given CourseID! : " + e.getMessage());
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                }
            }
        } else {
            throw new IllegalArgumentException("User is not a Student - Enrollment aborted!");
        }
        return null;
    }

    // Retrieve the faculty Timetable for enrolled Course
    public TimetableDTO viewTimetablesForEnrolledCourse(String token, String courseId) {
        try {
            // Identify the logged-in user
            String userEmail = jwtService.extractUserName(token);
            User user = userRepository.findFirstByEmail(userEmail);

            // Check whether the student has any matching Course enrollment
            if (studentEnrollmentRepository.existsByStudentId(user.getId()) && studentEnrollmentRepository.existsByEnrollmentsCourseId(courseId)) {
                Timetable courseTimetableForRelevantFaculty = timeTableRepository.findByCourseIdAndTimetableSessionsListFaculty(courseId, user.getFaculty());

                TimetableDTO timetableDTOForFaculty = new TimetableDTO();
                timetableDTOForFaculty.setId(courseTimetableForRelevantFaculty.getId());
                timetableDTOForFaculty.setCourseId(courseTimetableForRelevantFaculty.getCourseId());
                timetableDTOForFaculty.setCourse(courseTimetableForRelevantFaculty.getCourse());
                timetableDTOForFaculty.setCourseCode(courseTimetableForRelevantFaculty.getCourseCode());

                List<TimetableSession> relevantTimetableSessions = new ArrayList<>();
                // Filter out timetable sessions for the faculty student is in
                for (TimetableSession foundSession : courseTimetableForRelevantFaculty.getTimetableSessionsList()) {
                    if (foundSession.getFaculty().equals(user.getFaculty()))
                        relevantTimetableSessions.add(foundSession);
                }
                timetableDTOForFaculty.setTimetableSessionsList(relevantTimetableSessions);

                return timetableDTOForFaculty;
            }
        } catch (InvalidCsrfTokenException e) {
            logger.warn("Invalid Token! : " + e.getMessage());
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        }
        return null;
    }

    // Getting all the Enrollments data to view by Admins/Faculty
    @Override
    public List<StudentEnrollment> viewAllEnrollments() {
        List<StudentEnrollment> studentEnrollmentList = studentEnrollmentRepository.findAll();
        if (studentEnrollmentList==null)
            return null;
        return studentEnrollmentList;
    }

    // Getting Enrollments data of a specific student, to view by Admins/Faculty
    @Override
    public StudentEnrollment viewSpecificStudentEnrollments(String studentId) {
        try {
            StudentEnrollment studentEnrollment = studentEnrollmentRepository.findByStudentId(studentId);
            return studentEnrollment;
        } catch (NoSuchElementException nse) {
            logger.warn("Invalid studentId! --- "+nse.getMessage());
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    // Update a Student's Enrollments, by Admin/Faculty
    @Override
    public StudentEnrollment updateByAddingNewEnrollments(String[] newStudentEnrollmentCourses, String enrollmentId) {
        try {
            StudentEnrollment existingEnrollment = studentEnrollmentRepository.findById(enrollmentId).get();
            User user = userRepository.findById(existingEnrollment.getStudentId()).get();

            List<StdEnrollment> currentStdEnrollmentList = existingEnrollment.getEnrollments();
            List<String> newCourseEnrollmentsToAdd = new ArrayList<>(Arrays.asList(newStudentEnrollmentCourses));
            // Filter the Courses only which are not yet enrolled by the Student
            for (StdEnrollment stdEnrollment : currentStdEnrollmentList) {
                if (newCourseEnrollmentsToAdd.contains(stdEnrollment.getCourseId())) {
                    newCourseEnrollmentsToAdd.remove(stdEnrollment.getCourseId());
                }
            }
            for (String courseId: newCourseEnrollmentsToAdd) {
                try {
                    // Verify the selected Course validity
                    Course courseToEnroll = courseRepository.findFirstById(courseId);
                    // Verify that the Course trying to enroll in assigned for the student's faculty
                    if (!courseToEnroll.getFaculties().contains(user.getFaculty())){
                        logger.warn("The Course is not assigned for this Student's faculty!");
                        throw new RuntimeException("Cannot Enroll - The Course is not assigned for the faculty!");
                    }
                    else {
                        // Add the new enrollment
                        StdEnrollment stdEnrollment = new StdEnrollment();
                        stdEnrollment.setCourseId(courseToEnroll.getId());
                        stdEnrollment.setCourseCode(courseToEnroll.getCode());
                        stdEnrollment.setCredits(courseToEnroll.getCredits());
                        stdEnrollment.setCourseName(courseToEnroll.getCourseName());
                        stdEnrollment.setCourseDescription(courseToEnroll.getDescription());

                        List<StdEnrollment> currentEnrollments = existingEnrollment.getEnrollments();
                        currentEnrollments.add(stdEnrollment);
                        StudentEnrollment updatedEnrollment = studentEnrollmentRepository.save(existingEnrollment);
                        return updatedEnrollment;
                    }
                } catch (NoSuchElementException e) {
                    logger.warn("Course not found for the given CourseID! : " + e.getMessage());
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                }
            }

        } catch (NoSuchElementException nse) {
            logger.warn("Invalid Enrollment(Id)! --- "+nse.getMessage());
        }
        return null;
    }

    // Remove a Student's Enrollments, by Admin/Faculty
    @Override
    public boolean removeStudentEnrollments(String enrollmentId) {
        try {
            studentEnrollmentRepository.deleteById(enrollmentId);
            if (!studentEnrollmentRepository.existsById(enrollmentId))
                return true;
        } catch (NoSuchElementException nse) {
            logger.warn("Invalid enrollmentId to delete! : " + nse.getMessage());
        }
        return false;
    }

    // Remove a Student's specific Course Enrollment only, by Admin/Faculty
    @Override
    public StudentEnrollment removeSpecificCourseEnrollment(String studentId, String courseId) {
        StudentEnrollment currentEnrollments = studentEnrollmentRepository.findByStudentId(studentId);
        List<StdEnrollment> currentCourseEnrollmentList = currentEnrollments.getEnrollments();

        int stdEnrollmentIndexToRemove = -1;
        // Find the Enrollment for the given Course ID
        for (StdEnrollment stdEnrollment: currentCourseEnrollmentList) {
            if (stdEnrollment.getCourseId().equals(courseId))
                stdEnrollmentIndexToRemove = currentCourseEnrollmentList.indexOf(stdEnrollment);
        }
        // Remove the Course enrollment if it exists
        if (stdEnrollmentIndexToRemove>=0) {
            currentCourseEnrollmentList.remove(stdEnrollmentIndexToRemove);
            currentEnrollments.setEnrollments(currentCourseEnrollmentList);

            StudentEnrollment studentEnrollmentAfterCourseRemoval = studentEnrollmentRepository.save(currentEnrollments);
            return studentEnrollmentAfterCourseRemoval;
        }
        return null;
    }

}
