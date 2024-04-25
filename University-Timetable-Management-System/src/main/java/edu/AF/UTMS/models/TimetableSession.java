package edu.AF.UTMS.models;

import edu.AF.UTMS.models.consts.DaysOfTheWeek;
import edu.AF.UTMS.models.consts.Locations;
import edu.AF.UTMS.models.consts.TimetableSessionTypes;
import lombok.Data;

import java.time.LocalTime;

@Data
public class TimetableSession {
    private String faculty;
    private Locations location;
    private DaysOfTheWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private TimetableSessionTypes sessionType;
}
