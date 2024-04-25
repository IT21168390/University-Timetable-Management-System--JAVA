package edu.AF.UTMS.services;

import edu.AF.UTMS.dto.CourseDTO;
import edu.AF.UTMS.models.consts.Faculties;

import java.util.List;

public interface CourseService {
    public CourseDTO addNewCourse(CourseDTO courseDTO);
    //public List<CourseDTO> getAllCourses();
    public CourseDTO[] getAllCourses();
    public CourseDTO getCourse(String courseCode);
    public CourseDTO updateCourse(CourseDTO courseDTO);
    public boolean removeCourse(String courseId);

    public CourseDTO assignFacultiesToCourse(List<String> faculties, String courseId);
    public CourseDTO removeFacultiesFromCourse(List<String> faculties, String courseId);
}
