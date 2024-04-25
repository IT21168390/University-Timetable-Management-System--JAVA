package edu.AF.UTMS.controllers;

import edu.AF.UTMS.dto.BookingDTO;
import edu.AF.UTMS.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    @Autowired
    BookingService bookingService;

    @PostMapping("/faculty/new")
    public ResponseEntity createNewBooking(@RequestBody BookingDTO bookingDTO) {
        try {
            BookingDTO bookedDTO = bookingService.addNewBooking(bookingDTO);
            if (bookedDTO!=null)
                return new ResponseEntity<>(bookedDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/common/view/all")
    public ResponseEntity<List<BookingDTO>> viewAllBookings() {
        List<BookingDTO> bookingList = bookingService.getAllBookings();
        return new ResponseEntity<>(bookingList, HttpStatus.OK);
    }

    @GetMapping("/common/view/{bookingId}")
    public ResponseEntity<BookingDTO> viewBooking(@PathVariable String bookingId) {
        BookingDTO booking = bookingService.getBooking(bookingId);
        if (booking==null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @DeleteMapping("/faculty/delete/{bookingId}")
    public ResponseEntity deleteBooking(@PathVariable String bookingId) {
        boolean isDeleted = bookingService.deleteBooking(bookingId);
        if (isDeleted)
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
