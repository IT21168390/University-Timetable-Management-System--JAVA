package edu.AF.UTMS.repositories;

import edu.AF.UTMS.models.bookings.Resource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ResourcesRepository extends MongoRepository<Resource, String> {
    List<Resource> findByBookedSlotsEndTimeBefore(Date date);
}
