package edu.AF.UTMS.dto;

import edu.AF.UTMS.models.TimetableSession;
import edu.AF.UTMS.models.consts.Locations;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TimetableDTO {
    private String id;
    private String course;
    private String courseId;
    private String courseCode;
    private List<TimetableSession> timetableSessionsList;
    /*private Date time;
    private String faculty;
    private Locations location;*/
}
