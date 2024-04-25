package edu.AF.UTMS.services.impl;

import edu.AF.UTMS.converters.TimeTableDTOConverter;
import edu.AF.UTMS.dto.TimetableDTO;
import edu.AF.UTMS.models.Course;
import edu.AF.UTMS.models.StudentEnrollment;
import edu.AF.UTMS.models.Timetable;
import edu.AF.UTMS.models.TimetableSession;
import edu.AF.UTMS.repositories.CourseRepository;
import edu.AF.UTMS.repositories.StudentEnrollmentRepository;
import edu.AF.UTMS.repositories.TimeTableRepository;
import edu.AF.UTMS.services.CommonDataService;
import edu.AF.UTMS.services.EmailService;
import edu.AF.UTMS.services.TimetableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimetableServiceImpl implements TimetableService {

    @Autowired
    private TimeTableRepository timeTableRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentEnrollmentRepository studentEnrollmentRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TimeTableDTOConverter timeTableDTOConverter;
    @Autowired
    private CommonDataService commonDataService;

    private Logger logger = LoggerFactory.getLogger(TimetableServiceImpl.class);

    @Override
    public TimetableDTO createTimetable(TimetableDTO timetableDTO) throws IllegalAccessException {
        Course course = courseRepository.findFirstById(timetableDTO.getCourseId());
        if (course!=null && course.getCode().equals(timetableDTO.getCourseCode()) && course.getCourseName().equals(timetableDTO.getCourse())) {
            List<TimetableSession> timetableSessionsToAdd = timetableDTO.getTimetableSessionsList();

            List<String> facultiesList = commonDataService.getFacultyList();

            List<TimetableSession> validSessions = new ArrayList<>();

            if (timetableSessionsToAdd.size()>0) {
                for (TimetableSession timetableSession: timetableSessionsToAdd) {
                    if (facultiesList.contains(timetableSession.getFaculty())){
                        validSessions.add(timetableSession);
                    } else {
                        logger.warn("Timetable Session Ignored : Invalid Faculty! - "+timetableSession.getFaculty());
                    }
                }
            }

            timetableDTO.setTimetableSessionsList(validSessions);

            Timetable newTimetable = timeTableRepository.save(timeTableDTOConverter.convertTimetableDTOToTimetable(timetableDTO));
            System.out.println("Saved Timetable Object: "+newTimetable);

            TimetableDTO newTimetableDTO = timeTableDTOConverter.convertTimetableToTimetableDTO(newTimetable);
            return newTimetableDTO;
        } else {
            throw new InputMismatchException(" Invalid data set!");
        }
    }

    @Override
    public List<TimetableDTO> getAllTimetables() {
        List<Timetable> timetableList = timeTableRepository.findAll();
        List<TimetableDTO> timetables = new ArrayList<>();
        for (Timetable timetable: timetableList) {
            timetables.add(timeTableDTOConverter.convertTimetableToTimetableDTO(timetable));
        }

        return timetables;
    }

    @Override
    public TimetableDTO getTimetable(String timetableId) {
        try {
            Optional<Timetable> timetable = timeTableRepository.findById(timetableId);
            TimetableDTO timetableDTO = timeTableDTOConverter.convertTimetableToTimetableDTO(timetable.get());
            return timetableDTO;
        } catch (NoSuchElementException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public TimetableDTO getCourseTimetable(String courseCode) {
        try {
            Timetable timetable = timeTableRepository.findByCourseCode(courseCode);
            return timeTableDTOConverter.convertTimetableToTimetableDTO(timetable);
        } catch (NoSuchElementException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public List<TimetableDTO> getFacultyTimetables(String faculty) {
        List<String> facultiesList;
        try {
            facultiesList = commonDataService.getFacultyList();

            System.out.println("Faculties List: "+facultiesList);

            if (facultiesList.contains(faculty)) {
                System.out.println("Check if faculty name is exactly same as in the Faculties : "+facultiesList.get(facultiesList.indexOf(faculty))+" --- "+faculty);
                List<Timetable> timetableList = timeTableRepository.findByTimetableSessionsListFaculty(faculty);
                List<TimetableDTO> foundTimetableDTOList = new ArrayList<>();
                if (!timetableList.isEmpty() && timetableList!=null && timetableList.size()>0) {
                    for (Timetable timetable : timetableList) {
                        foundTimetableDTOList.add(timeTableDTOConverter.convertTimetableToTimetableDTO(timetable));
                    }
                    return foundTimetableDTOList;
                }
                logger.warn("timetableList for *"+faculty+"* is null!");
            } else {
                System.out.println("Provided Faculty name does not include in Faculties!");
            }
        } catch (IllegalAccessException e) {
            logger.warn("Issue occurred with accessing CommonDataService's getFacultyList() : "+e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public TimetableDTO updateTimetable(TimetableDTO timetableDTO, String timetableId) {
        if (timeTableRepository.existsById(timetableId)) {
            Course course = courseRepository.findFirstById(timetableDTO.getCourseId());
            if (course!=null && course.getCode().equals(timetableDTO.getCourseCode()) && course.getCourseName().equals(timetableDTO.getCourse())) {
                Timetable existingTimetable = timeTableRepository.findById(timetableId).get();
                existingTimetable.setCourse(timetableDTO.getCourse());
                existingTimetable.setCourseId(timetableDTO.getCourseId());
                existingTimetable.setCourseCode(timetableDTO.getCourseCode());

                List<TimetableSession> timetableSessionsToAdd = timetableDTO.getTimetableSessionsList();
                // Filter out sessions with invalid faculties beforehand. A similar method has used in createTimetable(), using loops
                List<TimetableSession> validSessions = timetableSessionsToAdd.stream()
                        .filter(session -> {
                            try {
                                return commonDataService.getFacultyList().contains(session.getFaculty());
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList());

                existingTimetable.setTimetableSessionsList(validSessions);

                Timetable updatedTimetable = timeTableRepository.save(existingTimetable);

                // Notify Students regarding the Timetable changes
                List<StudentEnrollment> studentEnrollments = studentEnrollmentRepository.findAllByEnrollmentsCourseId(course.getId());
                if (studentEnrollments.size()>0) {
                    List<String> studentsToNotify = new ArrayList<>();
                    for (StudentEnrollment studentEnrollment: studentEnrollments) {
                        studentsToNotify.add(studentEnrollment.getEmail());
                    }
                    emailService.sendMultipleEmails(
                            studentsToNotify,
                            "Timetable Updated!",
                            "Timetable for "+course.getCourseName()+" has been updated. Please check your relevant timetables.");
                }

                return timeTableDTOConverter.convertTimetableToTimetableDTO(updatedTimetable);
            } else {
                throw new IllegalArgumentException("Probably, invalid Course information included...");
            }
        }
        return null;
    }

    @Override
    public TimetableDTO modifyTimetableSessions(List<TimetableSession> timetableSessions, String timetableId) {
        if (!timeTableRepository.existsById(timetableId)) {
            return null;
        }

        Timetable currentTimetable = timeTableRepository.findById(timetableId).get();

        List<String> facultiesList;
        try {
            facultiesList = commonDataService.getFacultyList();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("IllegalAccessException! Cannot fetch facultiesList. "+e);
        }

        List<TimetableSession> validSessions = new ArrayList<>();

        if (timetableSessions.size()>0) {
            for (TimetableSession timetableSession : timetableSessions) {
                if (facultiesList.contains(timetableSession.getFaculty())) {
                    validSessions.add(timetableSession);
                } else {
                    logger.warn("Timetable Session Ignored : Invalid Faculty! - " + timetableSession.getFaculty());
                }
            }
        }
        currentTimetable.setTimetableSessionsList(validSessions);
        Timetable modifiedTimetable = timeTableRepository.save(currentTimetable);

        // Notify Students via Emails regarding the Timetable Sessions changes
        List<StudentEnrollment> studentEnrollments = studentEnrollmentRepository.findAllByEnrollmentsCourseId(currentTimetable.getCourseId());
        if (studentEnrollments.size()>0) {
            List<String> studentsToNotify = new ArrayList<>();
            for (StudentEnrollment studentEnrollment: studentEnrollments) {
                studentsToNotify.add(studentEnrollment.getEmail());
            }
            emailService.sendMultipleEmails(
                    studentsToNotify,
                    "Timetable Sessions Updated!",
                    "Some sessions for the current timetable of "+currentTimetable.getCourse()+" has been updated. Please check your relevant sessions.");
        }

        TimetableDTO modifiedTimetableDTO = timeTableDTOConverter.convertTimetableToTimetableDTO(modifiedTimetable);
        return modifiedTimetableDTO;
    }

    @Override
    public boolean removeTimetable(String timetableId) {
        boolean timetableExists = timeTableRepository.existsById(timetableId);
        if (timetableExists) {
            // Get the timetable to retrieve some data to notify users, before deleting.
            Timetable timetable = timeTableRepository.findById(timetableId).get();

            timeTableRepository.deleteById(timetableId);

            // Notify Students via Emails regarding the Timetable removal.
            List<StudentEnrollment> studentEnrollments = studentEnrollmentRepository.findAllByEnrollmentsCourseId(timetable.getCourseId());
            if (studentEnrollments.size()>0) {
                List<String> studentsToNotify = new ArrayList<>();
                for (StudentEnrollment studentEnrollment: studentEnrollments) {
                    studentsToNotify.add(studentEnrollment.getEmail());
                }
                emailService.sendMultipleEmails(
                        studentsToNotify,
                        "Timetable Removed!",
                        timetable.getCourse()+" timetable has been deleted. Stay tuned for an update...");
            }

            return true;
        }
        return false;
    }
}
