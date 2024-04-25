package edu.AF.UTMS.services.impl;

import edu.AF.UTMS.converters.BookingDTOConverter;
import edu.AF.UTMS.dto.BookingDTO;
import edu.AF.UTMS.models.bookings.Booking;
import edu.AF.UTMS.repositories.BookingRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class BookingServiceImplUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingDTOConverter bookingDTOConverter;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    private BookingServiceImpl service;

    @Test
    public void testHasBookingConflicts_RoomIdWithConflict() {
        BookingDTO newBookingDTO = new BookingDTO();
        newBookingDTO.setRoomId("room123");
        newBookingDTO.setStartTime(new Date());
        newBookingDTO.setEndTime(new Date(System.currentTimeMillis() + 1000 * 60 * 60)); // One hour later

        List<Booking> conflictingBookings = new ArrayList<>();
        conflictingBookings.add(createMockBooking("room123", newBookingDTO.getStartTime(), new Date(System.currentTimeMillis() + 1000 * 60 * 30))); // Overlaps partially

        Mockito.when(bookingRepository.findByRoomIdAndStartTimeBeforeAndEndTimeAfter(Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenReturn(conflictingBookings);

        boolean hasConflict = service.hasBookingConflicts(newBookingDTO);

        assertTrue(hasConflict);
    }

    private Booking createMockBooking(String roomId, Date startTime, Date endTime) {
        Booking booking = new Booking();
        booking.setRoomId(roomId);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        return booking;
    }


    @Test
    public void testHasBookingConflicts_NoConflicts() {
        BookingDTO newBookingDTO = createBookingDTO();

        when(bookingRepository.findByRoomIdAndStartTimeBeforeAndEndTimeAfter(anyString(), any(Date.class), any(Date.class)))
                .thenReturn(new ArrayList<>());

        boolean result = bookingService.hasBookingConflicts(newBookingDTO);

        assertFalse(result);
    }

    @Test
    public void testHasBookingConflicts_Conflicts() {
        BookingDTO newBookingDTO = createBookingDTO();
        List<Booking> conflictingBookings = new ArrayList<>();
        conflictingBookings.add(new Booking());

        when(bookingRepository.findByRoomIdAndStartTimeBeforeAndEndTimeAfter(anyString(), any(Date.class), any(Date.class)))
                .thenReturn(conflictingBookings);

        boolean result = bookingService.hasBookingConflicts(newBookingDTO);

        assertTrue(result);
    }

    @Test
    public void testGetBooking_ValidId() {
        String bookingId = "validId";
        Booking booking = new Booking();
        BookingDTO bookingDTO = new BookingDTO();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingDTOConverter.convertBookingToBookingDTO(booking)).thenReturn(bookingDTO);

        BookingDTO result = bookingService.getBooking(bookingId);

        assertNotNull(result);
        assertEquals(bookingDTO, result);
    }

    @Test
    public void testGetBooking_InvalidId() {
        String bookingId = "invalidId";

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        BookingDTO result = bookingService.getBooking(bookingId);

        assertNull(result);
    }

    private BookingDTO createBookingDTO() {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setRoomId("roomId");
        bookingDTO.setStartTime(new Date(System.currentTimeMillis() + 1000)); // Current time + 1 second
        bookingDTO.setEndTime(new Date(System.currentTimeMillis() + 2000)); // Current time + 2 seconds
        return bookingDTO;
    }


}