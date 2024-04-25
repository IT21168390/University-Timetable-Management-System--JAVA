package edu.AF.UTMS.models.bookings;

import edu.AF.UTMS.models.consts.Locations;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document("Rooms")
public class Room {
    @Id
    private String id;
    @Indexed(unique = true)
    private Locations room;
    private List<BookingSlots> bookedSlots = new ArrayList<>();

    public Room(Locations location) {
        this.room = location;
    }
}
