package edu.AF.UTMS.controllers;

import edu.AF.UTMS.dto.TimetableDTO;
import edu.AF.UTMS.models.TimetableSession;
import edu.AF.UTMS.services.impl.TimetableServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/timetables")
public class TimetableController {

    @Autowired
    public TimetableServiceImpl timetableService;

    // A Logger object to Log critical information for audit and diagnostic purposes.
    private Logger logger = LoggerFactory.getLogger(TimetableController.class);

    // Timetable creation API endpoint
    @PostMapping("/faculty/create_timetable")
    public ResponseEntity<TimetableDTO> createTimetable(@RequestBody TimetableDTO timetableDTO) {
        logger.info(timetableDTO.toString());

        try {
            TimetableDTO timetable = timetableService.createTimetable(timetableDTO);
            logger.info("Created Timetable: "+timetable.toString());
            return new ResponseEntity<>(timetable, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // API method to retrieve all Timetables
    @GetMapping("/common/view/all")
    public ResponseEntity<List<TimetableDTO>> viewAllTimetables() {
        List<TimetableDTO> timetableDTOs = timetableService.getAllTimetables();
        //Check whether Timetables exist or not
        if (timetableDTOs.size()>0)
            return new ResponseEntity<>(timetableDTOs, HttpStatus.OK);
        else
            return new ResponseEntity<>(timetableDTOs, HttpStatus.NO_CONTENT);
    }

    // API method to retrieve Timetable using Timetable ID
    @GetMapping("/common/view/{timetableId}")
    public ResponseEntity<TimetableDTO> viewTimetable(@PathVariable String timetableId) {
        System.out.println("Requested Timetable ID: "+timetableId);
        try {
            TimetableDTO timetableDTO = timetableService.getTimetable(timetableId);
            // Verify that a Timetable is available for the given ID, or not
            if (timetableDTO==null){
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(timetableDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid ID for a Timetable! ");
            logger.warn(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    // API method to retrieve Timetable using relevant Course Code
    @GetMapping("/common/course/{courseCode}")
    public ResponseEntity<TimetableDTO> viewTimetableForCourse(@PathVariable String courseCode) {
        System.out.println("Requested Course Code related to Timetable: "+courseCode);
        try {
            TimetableDTO timetableDTO = timetableService.getCourseTimetable(courseCode);
            // Verify that a Timetable is available for the given Course Code, or not
            if (timetableDTO==null){
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(timetableDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Course Code! ");
            logger.warn(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    // API method to retrieve Timetables by Faculty
    @GetMapping("/common/faculty/{faculty}")
    public ResponseEntity<List<TimetableDTO>> viewTimetablesByFaculty(@PathVariable String faculty) {
        System.out.println("Timetable Requested for Faculty: "+faculty);
        try {
            List<TimetableDTO> timetableDTOs = timetableService.getFacultyTimetables(faculty);
            // Verify that Timetables exist for the given Faculty, or not
            if (timetableDTOs == null || timetableDTOs.size()==0){
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(timetableDTOs, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Faculty Name! ");
            logger.warn(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    //Update a Timetable
    @PutMapping("/faculty/update/{timetableId}")
    public ResponseEntity<TimetableDTO> updateTimetable(@RequestBody TimetableDTO timetableDTO, @PathVariable String timetableId) {
        try {
            TimetableDTO updatedTimetable = timetableService.updateTimetable(timetableDTO, timetableId);
            if (updatedTimetable!=null) {
                return new ResponseEntity<>(updatedTimetable, HttpStatus.CREATED);
            }
        } catch (IllegalArgumentException iae) {
            logger.warn("Exception caught : "+iae.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    //Change Sessions of a Timetable
    @PutMapping("/faculty/update_sessions/{timetableId}")
    public ResponseEntity<TimetableDTO> modifyTimetableSessions(@RequestBody List<TimetableSession> timetableSessions, @PathVariable String timetableId) {
        try {
            TimetableDTO modifiedTimetable = timetableService.modifyTimetableSessions(timetableSessions, timetableId);
            if (modifiedTimetable!=null)
                return new ResponseEntity<>(modifiedTimetable, HttpStatus.CREATED);
            System.out.println("No match for timetableId...");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException re) {
            logger.warn("Exception caught : "+re.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Delete a Timetable from the Database
    @DeleteMapping("/faculty/remove/{timetableId}")
    public ResponseEntity removeTimetable(@PathVariable String timetableId) {
        try {
            boolean isRemoved = timetableService.removeTimetable(timetableId);
            if (isRemoved){
                System.out.println("Timetable "+timetableId+" is Successfully deleted.");
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage()+" : Invalid Timetable ID!");
        } catch (NoSuchElementException e2) {
            logger.warn(e2.getMessage());
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
