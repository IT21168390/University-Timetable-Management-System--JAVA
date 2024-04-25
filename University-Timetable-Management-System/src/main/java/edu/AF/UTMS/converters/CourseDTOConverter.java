package edu.AF.UTMS.converters;

import edu.AF.UTMS.dto.CourseDTO;
import edu.AF.UTMS.models.Course;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CourseDTOConverter {
    @Autowired
    private ModelMapper modelMapper;

    public CourseDTO convertCourseToCourseDTO(Course course) {
        CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);

        return courseDTO;
    }

    public Course convertCourseDTOToCourse(CourseDTO courseDTO) {
        Course course = modelMapper.map(courseDTO, Course.class);

        return course;
    }
}
