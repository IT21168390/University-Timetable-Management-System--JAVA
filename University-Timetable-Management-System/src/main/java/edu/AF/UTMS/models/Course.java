package edu.AF.UTMS.models;

import edu.AF.UTMS.models.consts.Faculties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Courses")
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    private String id;
    private String courseName;
    @Indexed(unique = true)  //@Indexed(unique = true, background = true)
    private String code;
    private String description;
    private int credits;
    private List<String> faculties;
}
