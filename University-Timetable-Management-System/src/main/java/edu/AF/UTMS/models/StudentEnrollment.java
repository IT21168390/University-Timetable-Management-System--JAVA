package edu.AF.UTMS.models;

import com.mongodb.lang.NonNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("StudentsEnrollments")
public class StudentEnrollment {
    @Id
    private String id;
    @Indexed(unique = true)
    private String studentId;
    private String email;
    private String studentFirstName;
    private String studentLastName;
    private List<StdEnrollment> enrollments;
    //@DBRef
    //private List<Course> enrollments;
}
