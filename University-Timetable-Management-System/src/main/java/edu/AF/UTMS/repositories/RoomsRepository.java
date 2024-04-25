package edu.AF.UTMS.repositories;

import edu.AF.UTMS.models.bookings.Room;
import edu.AF.UTMS.models.consts.Locations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RoomsRepository extends MongoRepository<Room, String> {
    List<Room> findByBookedSlotsEndTimeBefore(Date date);
    Room findByRoom(Locations location);
}
