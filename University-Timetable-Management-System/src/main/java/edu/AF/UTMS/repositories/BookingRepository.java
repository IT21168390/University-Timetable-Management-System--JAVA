package edu.AF.UTMS.repositories;

import edu.AF.UTMS.models.bookings.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByRoomIdAndStartTimeBeforeAndEndTimeAfter(String roomId, Date endTime, Date startTime);
    List<Booking> findByResourceIdAndStartTimeBeforeAndEndTimeAfter(String resourceId, Date endTime, Date startTime);

    // Combination of both of the above
    List<Booking> findByRoomIdAndEndTimeAfterAndStartTimeBeforeOrResourceIdAndEndTimeAfterAndStartTimeBefore(String roomId, Date startTime1, Date endTime1, String resourceId, Date startTime2, Date endTime2);

    List<Booking> findByRoomIdAndStartTimeEqualsOrEndTimeEquals(String roomId, Date startTime, Date endTime);
    List<Booking> findByResourceIdAndStartTimeEqualsOrEndTimeEquals(String resourceId, Date startTime, Date endTime);
}
