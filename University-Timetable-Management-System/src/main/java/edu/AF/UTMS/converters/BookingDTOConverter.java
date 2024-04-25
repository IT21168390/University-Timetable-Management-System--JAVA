package edu.AF.UTMS.converters;

import edu.AF.UTMS.dto.BookingDTO;
import edu.AF.UTMS.models.bookings.Booking;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookingDTOConverter {
    @Autowired
    private ModelMapper modelMapper;

    public Booking convertBookingDTOToBooking(BookingDTO bookingDTO) {
        Booking booking = modelMapper.map(bookingDTO, Booking.class);
        return booking;
    }

    public BookingDTO convertBookingToBookingDTO(Booking booking) {
        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
        return bookingDTO;
    }
}
