package edu.AF.UTMS.services.impl;

import com.mongodb.MongoWriteException;
import edu.AF.UTMS.converters.CourseDTOConverter;
import edu.AF.UTMS.dto.CourseDTO;
import edu.AF.UTMS.models.Course;
import edu.AF.UTMS.models.consts.Faculties;
import edu.AF.UTMS.repositories.CourseRepository;
import edu.AF.UTMS.services.CommonDataService;
import edu.AF.UTMS.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    CourseDTOConverter courseDTOConverter;

    @Override
    public CourseDTO addNewCourse(CourseDTO courseDTO) {
        if (courseDTO.getCode()==null || courseDTO.getCourseName()==null || courseDTO.getDescription()==null || courseDTO.getCredits()==0){
            return null;
        }

        Course course = new Course();
        course.setCourseName(courseDTO.getCourseName());
        course.setCode(courseDTO.getCode());
        course.setCredits(courseDTO.getCredits());
        course.setDescription(courseDTO.getDescription());
        course.setFaculties(courseDTO.getFaculties());

        try {
            Course createdCourse = new Course();
            createdCourse = courseRepository.save(course);

            CourseDTO returnCourseDTO = new CourseDTO();
            returnCourseDTO.setId(createdCourse.getId());
            returnCourseDTO.setCourseName(createdCourse.getCourseName());
            returnCourseDTO.setCode(createdCourse.getCode());
            returnCourseDTO.setCredits(createdCourse.getCredits());
            returnCourseDTO.setDescription(createdCourse.getDescription());
            returnCourseDTO.setFaculties(createdCourse.getFaculties());
            return returnCourseDTO;

        } catch (MongoWriteException mre) {
            System.out.println(mre.getMessage());
            return null;
        }
    }

    @Override
    public CourseDTO[] getAllCourses() {
        List<Course> courses = new ArrayList<>();
        courses = courseRepository.findAll();

        CourseDTO[] courseDTOs = new CourseDTO[courses.size()];
        for (int i=0; i<courseDTOs.length; i++) {
            courseDTOs[i] = courseDTOConverter.convertCourseToCourseDTO(courses.get(i));
        }
        return courseDTOs;
    }

    @Override
    public CourseDTO getCourse(String courseCode) {
        Course course = courseRepository.findByCode(courseCode);
        CourseDTO courseDTO = courseDTOConverter.convertCourseToCourseDTO(course);
        return courseDTO;
    }

    @Override
    public CourseDTO updateCourse(CourseDTO courseDTO) {
        Course existingCourse = courseRepository.findById(courseDTO.getId()).get();
        existingCourse.setCourseName(courseDTO.getCourseName());
        existingCourse.setCode(courseDTO.getCode());
        existingCourse.setCredits(courseDTO.getCredits());
        existingCourse.setDescription(courseDTO.getDescription());

        List<String> facultiesToSet = courseDTO.getFaculties();
        List<String> validFacultiesToSet = new ArrayList<>();
        try {
            CommonDataService commonDataService = new CommonDataServiceImpl();
            List<String> availableFaculties = commonDataService.getFacultyList();
            for (String faculty: facultiesToSet) {
                if (availableFaculties.contains(faculty))
                    validFacultiesToSet.add(faculty);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        existingCourse.setFaculties(validFacultiesToSet);

        Course updatedCourse = courseRepository.save(existingCourse);

        CourseDTO returnCourseDTO = courseDTOConverter.convertCourseToCourseDTO(updatedCourse);

        return returnCourseDTO;
    }

    @Override
    public boolean removeCourse(String courseId) {
        courseRepository.deleteById(courseId);

        if (courseRepository.findById(courseId).isPresent())
            return false;
        else
            return true;
    }

    @Override
    public CourseDTO assignFacultiesToCourse(List<String> faculties, String courseId) {
        try {
            Course existingCourse = courseRepository.findById(courseId).get();
            List<String> currentFaculties = existingCourse.getFaculties();
            for (String faculty: faculties) {
                if (currentFaculties != null) {
                    if (!currentFaculties.contains(faculty)) {
                        currentFaculties.add(faculty);
                    }
                }
                else {
                    currentFaculties = new ArrayList<>();
                    currentFaculties.add(faculty);
                }
            }
            existingCourse.setFaculties(currentFaculties);
            Course updatedCourse = courseRepository.save(existingCourse);
            CourseDTO courseDTO = courseDTOConverter.convertCourseToCourseDTO(updatedCourse);
            return courseDTO;
        } catch (NoSuchElementException e1) {
            System.out.println("Invalid Course! (Check the id) : "+e1.getMessage());
            return null;
        }  catch (Exception e2) {
            System.out.println("Exception! "+e2.getMessage());
            return null;
        }
    }

    @Override
    public CourseDTO removeFacultiesFromCourse(List<String> faculties, String courseId) {
        Course existingCourse = courseRepository.findById(courseId).get();
        List<String> currentFaculties = existingCourse.getFaculties();

        for (String faculty: faculties) {
            if (currentFaculties != null) {
                if (currentFaculties.contains(faculty)) {
                    currentFaculties.remove(faculty);
                }
            }
            else {
                return courseDTOConverter.convertCourseToCourseDTO(existingCourse);
            }
        }

        existingCourse.setFaculties(currentFaculties);
        Course updatedCourse = courseRepository.save(existingCourse);
        CourseDTO courseDTO = courseDTOConverter.convertCourseToCourseDTO(updatedCourse);
        return courseDTO;
    }
}
