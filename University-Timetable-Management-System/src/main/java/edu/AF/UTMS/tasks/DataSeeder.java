package edu.AF.UTMS.tasks;

import edu.AF.UTMS.models.bookings.Resource;
import edu.AF.UTMS.models.bookings.Room;
import edu.AF.UTMS.models.consts.Locations;
import edu.AF.UTMS.models.consts.Resources;
import edu.AF.UTMS.repositories.ResourcesRepository;
import edu.AF.UTMS.repositories.RoomsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final RoomsRepository roomsRepository;
    private final ResourcesRepository resourcesRepository;

    public DataSeeder(RoomsRepository roomsRepository, ResourcesRepository resourcesRepository) {
        this.roomsRepository = roomsRepository;
        this.resourcesRepository = resourcesRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("...............Rooms & Resources DATA SEEDER Started...............");
        if (roomsRepository.count() == 0) {
            for (Locations location: Locations.values()) {
                roomsRepository.save(new Room(location));
            }
            System.out.println("Rooms inserted to the DB...");
        }
        if (resourcesRepository.count() == 0) {
            for (Resources resource : Resources.values()) {
                resourcesRepository.save(new Resource(resource));
            }
            System.out.println("Resources inserted to the DB...");
        }
        System.out.println("...............DATA SEEDER Done!...............");
    }
}
