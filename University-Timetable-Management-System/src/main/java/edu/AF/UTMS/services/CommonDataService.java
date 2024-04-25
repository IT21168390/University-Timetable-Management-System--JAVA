package edu.AF.UTMS.services;

import edu.AF.UTMS.models.consts.DaysOfTheWeek;
import edu.AF.UTMS.models.consts.Locations;
import edu.AF.UTMS.models.consts.TimetableSessionTypes;

import java.util.List;

public interface CommonDataService {
    List<String> getFacultyList()  throws IllegalAccessException;
    //String[] getAllLocations();
    List<Locations> getAllLocations();
    List<DaysOfTheWeek> getDaysOfTheWeek();
    List<TimetableSessionTypes> getTimetableSessionTypes();
}
