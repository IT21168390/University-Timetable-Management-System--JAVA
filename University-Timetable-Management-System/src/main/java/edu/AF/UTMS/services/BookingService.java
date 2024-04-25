package edu.AF.UTMS.services;

import edu.AF.UTMS.dto.BookingDTO;

import java.util.List;

public interface BookingService {
    boolean hasBookingConflicts(BookingDTO bookingDTO);
    BookingDTO addNewBooking(BookingDTO newBookingDTO);
    List<BookingDTO> getAllBookings();
    BookingDTO getBooking(String bookingId);
    //BookingDTO updateBooking(BookingDTO bookingDTO);
    boolean deleteBooking(String bookingId);
}
