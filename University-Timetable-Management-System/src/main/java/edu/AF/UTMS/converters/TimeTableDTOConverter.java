package edu.AF.UTMS.converters;

import edu.AF.UTMS.dto.TimetableDTO;
import edu.AF.UTMS.models.Timetable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TimeTableDTOConverter {
    @Autowired
    private ModelMapper modelMapper;

    public TimetableDTO convertTimetableToTimetableDTO(Timetable course) {
        TimetableDTO courseDTO = modelMapper.map(course, TimetableDTO.class);

        return courseDTO;
    }

    public Timetable convertTimetableDTOToTimetable(TimetableDTO courseDTO) {
        Timetable course = modelMapper.map(courseDTO, Timetable.class);

        return course;
    }
}
