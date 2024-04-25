package edu.AF.UTMS.controllers;

import edu.AF.UTMS.dto.CourseDTO;
import edu.AF.UTMS.models.consts.Faculties;
import edu.AF.UTMS.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/admin/courses/addCourse")
    private ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        CourseDTO course = courseService.addNewCourse(courseDTO);
        if (course == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(course, HttpStatus.CREATED);
    }

    @PutMapping("/admin/courses/updateCourse")
    private ResponseEntity<CourseDTO> updateCourse(@RequestBody CourseDTO courseDTO) {
        try {
            CourseDTO course = courseService.updateCourse(courseDTO);
            if (course == null){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity(course, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/courses/{courseCode}")
    public ResponseEntity<?> getCourse(@PathVariable String courseCode) {
        CourseDTO courseDTO = courseService.getCourse(courseCode);
        if (courseDTO == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        return new ResponseEntity(courseDTO, HttpStatus.OK);
    }

    @GetMapping("/courses/all")
    public ResponseEntity<?> getAllCourses() {
        CourseDTO[] courseDTOs = courseService.getAllCourses();
        return new ResponseEntity(courseDTOs, HttpStatus.OK);
    }

    @DeleteMapping("/admin/courses/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable String courseId) {
        boolean deletionStatus = courseService.removeCourse(courseId);
        if (deletionStatus) {
            return new ResponseEntity("Course Deleted.", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Unable to Delete!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/faculty/courses/assignFaculty/{courseId}")
    public ResponseEntity<CourseDTO> assignFacultiesToCourse(@RequestBody List<String> faculties, @PathVariable String courseId) {
        List<String> validatedFaculties = new ArrayList<>();

        Class<Faculties> facultiesClass = Faculties.class;
        Field[] fields = facultiesClass.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                try {
                    String value = (String) field.get(null);
                    if (faculties.contains(value)) {
                        validatedFaculties.add(faculties.get(faculties.indexOf(value)));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        CourseDTO courseDTO = courseService.assignFacultiesToCourse(validatedFaculties, courseId);
        if (courseDTO == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(courseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/faculty/courses/removeFaculty/{courseId}")
    public ResponseEntity<CourseDTO> removeFacultiesFromCourse(@RequestBody List<String> faculties, @PathVariable String courseId) {
        if (!faculties.isEmpty() && faculties!=null ) {
            CourseDTO courseDTO = courseService.removeFacultiesFromCourse(faculties, courseId);
            if (courseDTO == null){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity(courseDTO, HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }
}
