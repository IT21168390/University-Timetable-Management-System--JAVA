package edu.AF.UTMS.services.impl;

import edu.AF.UTMS.dto.TimetableDTO;
import edu.AF.UTMS.models.*;
import edu.AF.UTMS.models.consts.DaysOfTheWeek;
import edu.AF.UTMS.models.consts.Locations;
import edu.AF.UTMS.models.consts.TimetableSessionTypes;
import edu.AF.UTMS.models.consts.UserRoles;
import edu.AF.UTMS.repositories.CourseRepository;
import edu.AF.UTMS.repositories.StudentEnrollmentRepository;
import edu.AF.UTMS.repositories.TimeTableRepository;
import edu.AF.UTMS.repositories.UserRepository;
import edu.AF.UTMS.services.JWTService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class StudentEnrollmentServiceImplUnitTest {
    @Mock
    private StudentEnrollmentRepository studentEnrollmentRepository;

    @Mock
    private TimeTableRepository timeTableRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private StudentEnrollmentServiceImpl studentEnrollmentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testEnrollStudentToCourse_NewEnrollment() {
        // Mocking necessary objects
        String token = "dummyToken";
        String courseId = "dummyCourseId";

        User student = new User();
        student.setUserRole(UserRoles.STUDENT);
        student.setId("studentId");
        student.setEmail("student@example.com");
        student.setFaculty("Computer Science");

        Course course = new Course();
        course.setId(courseId);
        course.setFaculties(List.of("Computer Science"));

        StudentEnrollment studentEnrollment = new StudentEnrollment();
        studentEnrollment.setStudentId(student.getId());
        studentEnrollment.setEmail(student.getEmail());
        studentEnrollment.setStudentFirstName(student.getFirstName());
        studentEnrollment.setStudentLastName(student.getLastName());
        studentEnrollment.setEnrollments(new ArrayList<>());

        when(jwtService.extractUserName(token)).thenReturn(student.getEmail());
        when(userRepository.findFirstByEmail(student.getEmail())).thenReturn(student);
        when(courseRepository.findFirstById(courseId)).thenReturn(course);
        when(studentEnrollmentRepository.save(any())).thenReturn(studentEnrollment);
        when(studentEnrollmentRepository.existsByStudentId(student.getId())).thenReturn(false);

        // Testing the enrollment
        StudentEnrollment result = studentEnrollmentService.enrollStudentToCourse(token, courseId);

        assertNotNull(result);
        assertEquals(student.getId(), result.getStudentId());

        // Check if enrollments list is not null before accessing its first element
        assertNotNull(result.getEnrollments());
        assertTrue(result.getEnrollments().isEmpty()); // Ensure that enrollments list is empty
    }

    @Test
    public void testViewTimetablesForEnrolledCourse_ValidCourse() {
        String token = "dummyToken";
        String courseId = "dummyCourseId";

        User student = new User();
        student.setId("studentId");
        student.setEmail("student@example.com");
        student.setFaculty("Computer Science");

        Timetable timetable = new Timetable();
        timetable.setId("timetableId");
        timetable.setCourseId(courseId);
        timetable.setCourse("Dummy Course");
        timetable.setCourseCode("DUMMY101");

        List<TimetableSession> timetableSessions = new ArrayList<>();
        TimetableSession session = new TimetableSession();
        session.setFaculty("Computer Science");
        session.setLocation(Locations.A100); // A100 is a location enum constant
        session.setDay(DaysOfTheWeek.Monday); // MONDAY is a day enum constant
        session.setStartTime(LocalTime.of(9, 0)); // Setting start time to 9:00 AM
        session.setEndTime(LocalTime.of(10, 30)); // Setting end time to 10:30 AM
        session.setSessionType(TimetableSessionTypes.Lecture); // LECTURE is a session type enum constant
        timetableSessions.add(session);

        timetable.setTimetableSessionsList(timetableSessions);

        when(jwtService.extractUserName(token)).thenReturn(student.getEmail());
        when(userRepository.findFirstByEmail(student.getEmail())).thenReturn(student);
        when(studentEnrollmentRepository.existsByStudentId(student.getId())).thenReturn(true);
        when(studentEnrollmentRepository.existsByEnrollmentsCourseId(courseId)).thenReturn(true);
        when(timeTableRepository.findByCourseIdAndTimetableSessionsListFaculty(courseId, student.getFaculty())).thenReturn(timetable);

        TimetableDTO result = studentEnrollmentService.viewTimetablesForEnrolledCourse(token, courseId);

        assertNotNull(result);
        assertEquals(timetable.getId(), result.getId());
        assertEquals(timetable.getCourseId(), result.getCourseId());
        assertEquals(timetable.getCourse(), result.getCourse());
        assertEquals(timetable.getCourseCode(), result.getCourseCode());
        assertEquals(1, result.getTimetableSessionsList().size());

        // Validate the first session details
        TimetableSession session_n = result.getTimetableSessionsList().get(0);
        assertEquals(session.getLocation(), session_n.getLocation());
        assertEquals(session.getDay(), session_n.getDay());
        assertEquals(session.getStartTime(), session_n.getStartTime());
        assertEquals(session.getEndTime(), session_n.getEndTime());
        assertEquals(session.getSessionType(), session_n.getSessionType());
    }

    @Test
    public void testViewSpecificStudentEnrollments_ValidStudentId() {
        String studentId = "dummyStudentId";

        StudentEnrollment studentEnrollment = new StudentEnrollment();
        studentEnrollment.setId("enrollmentId");
        studentEnrollment.setStudentId(studentId);

        when(studentEnrollmentRepository.findByStudentId(studentId)).thenReturn(studentEnrollment);

        StudentEnrollment result = studentEnrollmentService.viewSpecificStudentEnrollments(studentId);

        assertNotNull(result);
        assertEquals(studentEnrollment.getId(), result.getId());
        assertEquals(studentId, result.getStudentId());
    }

}
