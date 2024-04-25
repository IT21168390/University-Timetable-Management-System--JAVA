package edu.AF.UTMS.services.impl;

import edu.AF.UTMS.converters.TimeTableDTOConverter;
import edu.AF.UTMS.dto.TimetableDTO;
import edu.AF.UTMS.models.Timetable;
import edu.AF.UTMS.models.consts.Faculties;
import edu.AF.UTMS.repositories.TimeTableRepository;
import edu.AF.UTMS.services.CommonDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class TimeTableServiceImplUnitTest {
    @Mock
    private TimeTableRepository timeTableRepository;

    @Mock
    private TimeTableDTOConverter timeTableDTOConverter;

    @Mock
    private CommonDataService commonDataService;

    @InjectMocks
    private TimetableServiceImpl timetableService;

    public TimeTableServiceImplUnitTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getCourseTimetable_ExistingCourse_ReturnsTimetableDTO() {
        // Arrange
        String courseCode = "CSE101";
        Timetable timetable = new Timetable();
        when(timeTableRepository.findByCourseCode(courseCode)).thenReturn(timetable);
        TimetableDTO expectedDTO = new TimetableDTO();
        when(timeTableDTOConverter.convertTimetableToTimetableDTO(timetable)).thenReturn(expectedDTO);

        // Act
        TimetableDTO result = timetableService.getCourseTimetable(courseCode);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);
    }

    @Test
    public void getCourseTimetable_NonExistingCourse_ReturnsNull() {
        // Arrange
        String courseCode = "NonExistingCourse";
        when(timeTableRepository.findByCourseCode(courseCode)).thenThrow(new NoSuchElementException());

        // Act
        TimetableDTO result = timetableService.getCourseTimetable(courseCode);

        // Assert
        assertNull(result);
    }

    @Test
    public void getFacultyTimetables_ValidFaculty_ReturnsTimetableDTOList() throws IllegalAccessException {
        // Arrange
        String faculty = Faculties.ENGINEERING;
        List<String> facultiesList = new ArrayList<>();
        facultiesList.add(Faculties.ENGINEERING);
        when(commonDataService.getFacultyList()).thenReturn(facultiesList);

        Timetable timetable = new Timetable();
        List<Timetable> timetableList = new ArrayList<>();
        timetableList.add(timetable);
        when(timeTableRepository.findByTimetableSessionsListFaculty(faculty)).thenReturn(timetableList);

        TimetableDTO timetableDTO = new TimetableDTO();
        when(timeTableDTOConverter.convertTimetableToTimetableDTO(timetable)).thenReturn(timetableDTO);

        // Act
        List<TimetableDTO> result = timetableService.getFacultyTimetables(faculty);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(timetableDTO, result.get(0));
    }

    @Test
    public void getFacultyTimetables_InvalidFaculty_ReturnsNull() throws IllegalAccessException {
        // Arrange
        String faculty = "InvalidFaculty";
        List<String> facultiesList = new ArrayList<>();
        facultiesList.add(Faculties.ENGINEERING);
        when(commonDataService.getFacultyList()).thenReturn(facultiesList);

        // Act
        List<TimetableDTO> result = timetableService.getFacultyTimetables(faculty);

        // Assert
        assertNull(result);
    }
}
