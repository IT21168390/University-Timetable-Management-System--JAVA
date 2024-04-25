package edu.AF.UTMS.models;

import lombok.Data;

@Data
public class StdEnrollment {
    private String courseId;
    private String courseCode;
    private String courseName;
    private String courseDescription;
    private int credits;
}
