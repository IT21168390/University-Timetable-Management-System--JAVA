package edu.AF.UTMS.tasks;

import edu.AF.UTMS.models.bookings.BookingSlots;
import edu.AF.UTMS.models.bookings.Resource;
import edu.AF.UTMS.models.bookings.Room;
import edu.AF.UTMS.repositories.ResourcesRepository;
import edu.AF.UTMS.repositories.RoomsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;

@Component
public class BookingsCleanup {
    @Autowired
    private RoomsRepository roomsRepository;
    @Autowired
    private ResourcesRepository resourcesRepository;

    /* If Timetables linked with Bookings, add a method to avoid cleanup for Rooms by checking whether each one's Day field is 'null' or not.
    *  If Day field is not null, it means it is a Timetable slot. */

    // Runs the Cleanup Task every day
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupExpiredRoomBookings() {
        Date now = new Date();

        /* This method might be a bit different from the second cleanup method below, due to trying some modifications. But so far both are functional... */

        /*TimeZone utc = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(utc);
        Date nowUTC = new Date(sdf.format(now));*/

        Instant nowUTC = Instant.now();

        // Find all the rooms with any expired/passed booking slots in the database.
        /*If there is an issue that the data not fetching even when there are expired timeslots exist, probably a Timezone mismatch*/
        List<Room> roomsWithExpiredBookingSlots = roomsRepository.findByBookedSlotsEndTimeBefore(Date.from(nowUTC));

        // Update each room with active(non-outdated) booking slots
        if (!roomsWithExpiredBookingSlots.isEmpty() && roomsWithExpiredBookingSlots!=null) {
            roomsWithExpiredBookingSlots.forEach(room -> {
                List<BookingSlots> activeBookingSlots = room.getBookedSlots().stream().filter(bookedSlot -> bookedSlot.getEndTime().after(Date.from(nowUTC)))
                        .collect(Collectors.toList());
                room.setBookedSlots(activeBookingSlots);
            });
            // Save the rooms data without expired bookings, back to the database.
            roomsRepository.saveAll(roomsWithExpiredBookingSlots);
            System.out.println(now+"  Rooms_Booking_Cleanup_Scheduler: 'Rooms' collection cleaned up by removing old bookings.");
        }
        System.out.println(now+"  Rooms_Booking_Cleanup_Scheduler: No expired booking slots to clean-up in 'Rooms' collection.");
    }

    // Runs the Cleanup Task every hour
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredResourceBookings() {
        Date now = new Date();
        // Find all the resources with any expired/passed booking slots in the database.
        List<Resource> resourcesWithExpiredBookingSlots = resourcesRepository.findByBookedSlotsEndTimeBefore(now);

        // Update each resource with active(non-outdated) booking slots
        if (!resourcesWithExpiredBookingSlots.isEmpty() && resourcesWithExpiredBookingSlots!=null) {
            resourcesWithExpiredBookingSlots.forEach(room -> {
                List<BookingSlots> activeBookingSlots = room.getBookedSlots().stream().filter(bookedSlot -> bookedSlot.getEndTime().after(now))
                        .collect(Collectors.toList());
                room.setBookedSlots(activeBookingSlots);
            });
            // Save the resources data without expired bookings, back to the database.
            resourcesRepository.saveAll(resourcesWithExpiredBookingSlots);
            System.out.println(now+"  Resources_Booking_Cleanup_Scheduler: 'Resources' collection cleaned up by removing old bookings.");
        }
        System.out.println(now+"  Resources_Booking_Cleanup_Scheduler: No expired booking slots to clean-up in 'Resources' collection.");
    }
}
