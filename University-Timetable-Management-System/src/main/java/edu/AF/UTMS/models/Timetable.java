package edu.AF.UTMS.models;

import edu.AF.UTMS.models.consts.Locations;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document("Timetables")
public class Timetable {
    @Id
    private String id;
    private String course;
    @Indexed(unique = true)
    private String courseId;
    @Indexed(unique = true)
    private String courseCode;
    private List<TimetableSession> timetableSessionsList;
}
