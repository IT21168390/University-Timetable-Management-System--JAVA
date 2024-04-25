package edu.AF.UTMS.services.impl;

import edu.AF.UTMS.models.consts.DaysOfTheWeek;
import edu.AF.UTMS.models.consts.Faculties;
import edu.AF.UTMS.models.consts.Locations;
import edu.AF.UTMS.models.consts.TimetableSessionTypes;
import edu.AF.UTMS.services.CommonDataService;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommonDataServiceImpl implements CommonDataService {
    private Faculties faculties;
    // Get all values of the Locations enum.
    private Locations[] locations = Locations.values();

    public List<String> getFacultyList() throws IllegalAccessException {
        ArrayList<String> facultiesArrayList = new ArrayList<>();
        Field[] fields = Faculties.class.getDeclaredFields();

        for (Field field : fields) {
            if (field.getType().equals(String.class)) {
                facultiesArrayList.add((String) field.get(null));
            }
        }
        return facultiesArrayList;
    }

    public List<Locations> getAllLocations() {
        List<Locations> locationsList = new ArrayList<>();
        for (int i=0; i< locations.length; i++) {
            locationsList.add(locations[i]);
        }
        return locationsList;
    }

    @Override
    public List<DaysOfTheWeek> getDaysOfTheWeek() {
        List<DaysOfTheWeek> daysOfTheWeekList = new ArrayList<>();
        for (DaysOfTheWeek day: DaysOfTheWeek.values()) {
            daysOfTheWeekList.add(day);
        }
        return daysOfTheWeekList;
    }

    @Override
    public List<TimetableSessionTypes> getTimetableSessionTypes() {
        List<TimetableSessionTypes> timetableSessionTypesList = new ArrayList<>();
        for (TimetableSessionTypes type: TimetableSessionTypes.values()) {
            timetableSessionTypesList.add(type);
        }
        return timetableSessionTypesList;
    }
}
