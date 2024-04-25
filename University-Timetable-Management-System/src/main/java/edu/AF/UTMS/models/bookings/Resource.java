package edu.AF.UTMS.models.bookings;

import edu.AF.UTMS.models.consts.Resources;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
@Document("Resources")
public class Resource {
    @Id
    private String id;
    @NonNull
    private Resources name;
    private List<BookingSlots> bookedSlots = new ArrayList<>();
}
