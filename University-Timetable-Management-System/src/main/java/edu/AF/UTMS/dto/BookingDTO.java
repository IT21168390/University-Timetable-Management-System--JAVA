package edu.AF.UTMS.dto;

import edu.AF.UTMS.models.consts.Locations;
import edu.AF.UTMS.models.consts.Resources;
import lombok.Data;

import java.util.Date;

@Data
public class BookingDTO {
    private String id;
    private String roomId;
    private Locations roomName;
    private String resourceId;
    private Resources resourceName;
    private String courseOrEvent;
    //private String courseOrEventId;
    private Date startTime;
    private Date endTime;
}
