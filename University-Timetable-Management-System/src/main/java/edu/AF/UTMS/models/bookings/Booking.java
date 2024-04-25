package edu.AF.UTMS.models.bookings;

import edu.AF.UTMS.models.consts.Locations;
import edu.AF.UTMS.models.consts.Resources;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("Bookings")
public class Booking {
    @Id
    private String id;
    @Indexed
    private String roomId;
    private Locations roomName;
    @Indexed
    private String resourceId;
    private Resources resourceName;
    private String courseOrEvent;
    //private String courseOrEventId;
    @Indexed
    private Date startTime;
    @Indexed
    private Date endTime;
}
