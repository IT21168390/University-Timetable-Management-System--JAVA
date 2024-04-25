package edu.AF.UTMS.services;

import edu.AF.UTMS.dto.TimetableDTO;
import edu.AF.UTMS.models.TimetableSession;

import java.util.List;

public interface TimetableService {
    TimetableDTO createTimetable(TimetableDTO timetableDTO) throws IllegalAccessException;
    List<TimetableDTO> getAllTimetables();
    TimetableDTO getTimetable(String timetableId);
    TimetableDTO getCourseTimetable(String courseCode);
    List<TimetableDTO> getFacultyTimetables(String faculty);
    TimetableDTO updateTimetable(TimetableDTO timetableDTO, String timetableId);
    TimetableDTO modifyTimetableSessions(List<TimetableSession> timetableSession, String timetableId);
    boolean removeTimetable(String timetableId);
}
