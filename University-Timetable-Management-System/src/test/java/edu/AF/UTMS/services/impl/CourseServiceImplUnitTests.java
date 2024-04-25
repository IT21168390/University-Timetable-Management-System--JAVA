package edu.AF.UTMS.services.impl;

import edu.AF.UTMS.converters.CourseDTOConverter;
import edu.AF.UTMS.dto.CourseDTO;
import edu.AF.UTMS.models.Course;
import edu.AF.UTMS.repositories.CourseRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class CourseServiceImplUnitTests {
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseDTOConverter courseDTOConverter;
    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    public void testGetAllCourses() {
        // Mocking data
        List<Course> courses = Arrays.asList(
                new Course("1", "Course 1", "C1", "Description 1", 3, Arrays.asList("Faculty 1", "Faculty 2")),
                new Course("2", "Course 2", "C2", "Description 2", 4, Arrays.asList("Faculty 3", "Faculty 4"))
        );
        when(courseRepository.findAll()).thenReturn(courses);

        // Mocking conversion
        CourseDTO[] expectedCourseDTOs = courses.stream()
                .map(courseDTOConverter::convertCourseToCourseDTO)
                .toArray(CourseDTO[]::new);
        when(courseDTOConverter.convertCourseToCourseDTO(any(Course.class)))
                .thenReturn(new CourseDTO());

        // Testing
        CourseDTO[] result = courseService.getAllCourses();

        assertEquals(expectedCourseDTOs.length, result.length);
    }

    @Test
    public void testGetCourse() {
        // Mocking data
        String courseCode = "C1";
        Course course = new Course("1", "Course 1", courseCode, "Description 1", 3, Arrays.asList("Faculty of Computing", "Faculty of Business"));
        when(courseRepository.findByCode(courseCode)).thenReturn(course);

        // Mocking conversion
        CourseDTO expectedCourseDTO = new CourseDTO("1", "Course 1", courseCode, "Description 1", 3, Arrays.asList("Faculty of Computing", "Faculty of Business"));

        when(courseDTOConverter.convertCourseToCourseDTO(any(Course.class))).thenReturn(expectedCourseDTO);

        // Testing
        CourseDTO result = courseService.getCourse(courseCode);

        assertEquals(expectedCourseDTO, result);
    }
}
