package edu.AF.UTMS.models.bookings;

import edu.AF.UTMS.models.consts.DaysOfTheWeek;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
public class BookingSlots {
    private String bookingId;
    private DaysOfTheWeek day; //= null;
    @Indexed
    private Date startTime;
    @Indexed
    private Date endTime;
}
