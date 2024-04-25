package edu.AF.UTMS;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import edu.AF.UTMS.models.Course;
import edu.AF.UTMS.models.consts.Faculties;
import edu.AF.UTMS.repositories.CourseRepository;
import edu.AF.UTMS.services.CourseService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CourseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @LocalServerPort
    private int port;

    private static MongodExecutable mongodExecutable;

    @BeforeClass
    public static void setUp() throws Exception {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .build();
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }

    @Test
    public void testGetCourse() throws Exception {
        // Create a test course
        Course course = new Course();
        course.setCourseName("TEST_COURSE");
        course.setCode("TEST_CODE");
        course.setCredits(2);
        course.setDescription("TEST_DESCRIPTION");
        List<String> faculties = new ArrayList<>();
        faculties.add(Faculties.COMPUTING);
        course.setFaculties(faculties);

        courseRepository.save(course);

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/courses/{courseCode}", "TEST101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TEST_CODE"))
                .andExpect(jsonPath("$.courseName").value("Test TEST_COURSE"));

        // Clean up
        //courseRepository.delete(course);
    }

    @Test
    public void testGetAllCourses() throws Exception {
        // Create some test courses
        Course course1 = new Course();
        course1.setCode("COURSE1");
        course1.setCourseName("Course 1");
        course1.setCredits(2);
        course1.setDescription("TEST_DESCRIPTION");
        List<String> faculties = new ArrayList<>();

        faculties.add(Faculties.COMPUTING);
        course1.setFaculties(faculties);

        courseRepository.save(course1);

        Course course2 = new Course();
        course2.setCode("COURSE2");
        course2.setCourseName("Course 2");
        course2.setCredits(2);
        course2.setDescription("TEST_DESCRIPTION");
        course2.setFaculties(faculties);
        courseRepository.save(course2);

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/courses/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code").value("COURSE1"))
                .andExpect(jsonPath("$[0].courseName").value("Course 1"))
                .andExpect(jsonPath("$[1].code").value("COURSE2"))
                .andExpect(jsonPath("$[1].courseName").value("Course 2"));

        // Clean up
        //courseRepository.deleteAll();
    }
}
