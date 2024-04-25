package edu.AF.UTMS.dto;

import edu.AF.UTMS.models.consts.Faculties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private String id;
    private String courseName;
    private String code;
    private String description;
    private int credits;
    private List<String> faculties;
}
