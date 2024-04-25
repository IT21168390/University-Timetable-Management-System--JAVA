package edu.AF.UTMS.repositories;

import edu.AF.UTMS.models.Timetable;
import edu.AF.UTMS.models.consts.DaysOfTheWeek;
import edu.AF.UTMS.models.consts.Faculties;
import edu.AF.UTMS.models.consts.Locations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeTableRepository extends MongoRepository<Timetable, String> {
    Optional<Timetable> findById(String id);
    Timetable findByCourse(String course);
    Timetable findByCourseCode(String courseCode);
    Timetable findByCourseId(String courseId);
    List<Timetable> findByTimetableSessionsListFaculty(String faculty);
    Timetable findByCourseIdAndTimetableSessionsListFaculty(String courseId, String faculty);
    List<Timetable> findByTimetableSessionsListLocation(Locations location);
    List<Timetable> findByTimetableSessionsListDay(DaysOfTheWeek day);
}
