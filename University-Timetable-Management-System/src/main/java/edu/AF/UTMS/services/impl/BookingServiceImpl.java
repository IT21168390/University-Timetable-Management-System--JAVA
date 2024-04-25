package edu.AF.UTMS.services.impl;

import edu.AF.UTMS.converters.BookingDTOConverter;
import edu.AF.UTMS.dto.BookingDTO;
import edu.AF.UTMS.models.StudentEnrollment;
import edu.AF.UTMS.models.User;
import edu.AF.UTMS.models.bookings.Booking;
import edu.AF.UTMS.models.bookings.BookingSlots;
import edu.AF.UTMS.models.bookings.Resource;
import edu.AF.UTMS.models.bookings.Room;
import edu.AF.UTMS.models.consts.UserRoles;
import edu.AF.UTMS.repositories.BookingRepository;
import edu.AF.UTMS.repositories.ResourcesRepository;
import edu.AF.UTMS.repositories.RoomsRepository;
import edu.AF.UTMS.repositories.UserRepository;
import edu.AF.UTMS.services.BookingService;
import edu.AF.UTMS.services.CommonDataService;
import edu.AF.UTMS.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.MappingException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    private RoomsRepository roomsRepository;
    @Autowired
    private ResourcesRepository resourcesRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommonDataService commonDataService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private BookingDTOConverter bookingDTOConverter;

    // Checking whether new Bookings conflict with existing Bookings by overlapping time slots with each other for the same Resource or Room.
    public boolean hasBookingConflicts(BookingDTO newBookingDTO) {
        if (newBookingDTO.getRoomId() != null) {
            List<Booking> conflictingBookings = bookingRepository.findByRoomIdAndStartTimeBeforeAndEndTimeAfter(newBookingDTO.getRoomId(), newBookingDTO.getEndTime(), newBookingDTO.getStartTime());

            return !conflictingBookings.isEmpty() ;
        }
        if (newBookingDTO.getResourceId() != null) {
            List<Booking> conflictingBookings = bookingRepository.findByResourceIdAndStartTimeBeforeAndEndTimeAfter(newBookingDTO.getResourceId(), newBookingDTO.getEndTime(), newBookingDTO.getStartTime());

            return !conflictingBookings.isEmpty() ;
        }
        return false;
    }

    // Creating a New Booking
    public BookingDTO addNewBooking(BookingDTO newBookingDTO) {
        if (hasBookingConflicts(newBookingDTO)) {
            throw new IllegalArgumentException("Booking conflicts with existing Booking(s)!");
        } else {
            Booking newBooking = bookingDTOConverter.convertBookingDTOToBooking(newBookingDTO);

            // Verify that if Room/Resource exists (valid), or not
            if (newBookingDTO.getRoomId() != null && newBookingDTO.getResourceId() != null) {
                try {
                    Room room = roomsRepository.findById(newBookingDTO.getRoomId()).get();
                    if (!room.getRoom().equals(newBookingDTO.getRoomName()))
                        throw new IllegalArgumentException("Room name & id do not match!");

                    List<BookingSlots> existingRoomBookedSlots = room.getBookedSlots();

                    BookingSlots roomBookingSlot = new BookingSlots();
                    roomBookingSlot.setStartTime(newBookingDTO.getStartTime());
                    roomBookingSlot.setEndTime(newBookingDTO.getEndTime());

                    // -----------------------------------------------------------------

                    Resource resource = resourcesRepository.findById(newBookingDTO.getResourceId()).get();
                    if (!resource.getName().equals(newBookingDTO.getResourceName()))
                        throw new IllegalArgumentException("Resource name & id do not match!");
                    List<BookingSlots> existingResourceBookedSlots = resource.getBookedSlots();

                    BookingSlots resourceBookingSlot = new BookingSlots();

                    resourceBookingSlot.setStartTime(newBookingDTO.getStartTime());
                    resourceBookingSlot.setEndTime(newBookingDTO.getEndTime());

                    Booking booking = bookingRepository.save(newBooking);

                    // Save Booking Slots inside relevant Rooms data as well
                    roomBookingSlot.setBookingId(booking.getId());

                    existingRoomBookedSlots.add(roomBookingSlot);
                    room.setBookedSlots(existingRoomBookedSlots);
                    roomsRepository.save(room);


                    // Notify all users about the Room booking
                    List<User> userList = userRepository.findAllByUserRole(UserRoles.ADMIN);
                    userList.addAll(userRepository.findAllByUserRole(UserRoles.FACULTY_MEMBER));
                    List<String> usersToNotify = new ArrayList<>();
                    for (User user: userList) {
                        usersToNotify.add(user.getEmail());
                    }
                    emailService.sendMultipleEmails(
                            usersToNotify,
                            "A Room has been Reserved",
                            "Room: "+room.getRoom()+" has been booked.\n"+room.getBookedSlots());


                    // Save Booking Slots inside relevant Resources data
                    resourceBookingSlot.setBookingId(booking.getId());

                    existingResourceBookedSlots.add(resourceBookingSlot);
                    resource.setBookedSlots(existingResourceBookedSlots);
                    resourcesRepository.save(resource);

                    return bookingDTOConverter.convertBookingToBookingDTO(booking);

                } catch (NoSuchElementException e) {
                    System.out.println("Invalid Room ID or Resource ID! : "+e.getMessage());
                    return null;
                } catch (MappingException e) {
                    System.out.println("MappingException --- Cannot Map Objects! : "+e.getMessage());
                }
            }
            else if (newBookingDTO.getRoomId() != null) {
                try {
                    Room room = roomsRepository.findById(newBookingDTO.getRoomId()).get();
                    if (!room.getRoom().equals(newBookingDTO.getRoomName()))
                        throw new IllegalArgumentException("Room name & id do not match!");

                    List<BookingSlots> existingRoomBookedSlots = room.getBookedSlots();

                    BookingSlots roomBookingSlot = new BookingSlots();
                    roomBookingSlot.setStartTime(newBookingDTO.getStartTime());
                    roomBookingSlot.setEndTime(newBookingDTO.getEndTime());

                    Booking bookingToAdd = new Booking();
                    bookingToAdd.setRoomId(newBooking.getRoomId());
                    bookingToAdd.setRoomName(newBooking.getRoomName());
                    bookingToAdd.setCourseOrEvent(newBooking.getCourseOrEvent());
                    bookingToAdd.setStartTime(newBooking.getStartTime());
                    bookingToAdd.setEndTime(newBooking.getEndTime());

                    Booking booking = bookingRepository.save(bookingToAdd);

                    // Save Booking Slots inside relevant Rooms data as well
                    roomBookingSlot.setBookingId(booking.getId());

                    existingRoomBookedSlots.add(roomBookingSlot);
                    room.setBookedSlots(existingRoomBookedSlots);
                    roomsRepository.save(room);

                    // Notify all users about the Room booking
                    List<User> userList = userRepository.findAll();
                    List<String> usersToNotify = new ArrayList<>();
                    for (User user: userList) {
                        usersToNotify.add(user.getEmail());
                    }
                    emailService.sendMultipleEmails(
                            usersToNotify,
                            "A Room has been Reserved",
                            "Room: "+room.getRoom()+" has been booked.\n"+room.getBookedSlots());

                    return bookingDTOConverter.convertBookingToBookingDTO(booking);

                } catch (NoSuchElementException e) {
                    System.out.println("Invalid Room ID! : "+e.getMessage());
                } catch (MappingException e) {
                    System.out.println("MappingException --- Cannot Map Objects! : "+e.getMessage());
                }
            }
            else if (newBookingDTO.getResourceId() != null) {
                try {
                    Resource resource = resourcesRepository.findById(newBookingDTO.getResourceId()).get();
                    if (!resource.getName().equals(newBookingDTO.getResourceName()))
                        throw new IllegalArgumentException("Resource name & id do not match!");
                    List<BookingSlots> existingResourceBookedSlots = resource.getBookedSlots();

                    BookingSlots resourceBookingSlot = new BookingSlots();

                    resourceBookingSlot.setStartTime(newBookingDTO.getStartTime());
                    resourceBookingSlot.setEndTime(newBookingDTO.getEndTime());

                    Booking bookingToAdd = new Booking();
                    bookingToAdd.setResourceId(newBooking.getResourceId());
                    bookingToAdd.setResourceName(newBooking.getResourceName());
                    bookingToAdd.setCourseOrEvent(newBooking.getCourseOrEvent());
                    bookingToAdd.setStartTime(newBooking.getStartTime());
                    bookingToAdd.setEndTime(newBooking.getEndTime());

                    Booking booking = bookingRepository.save(bookingToAdd);

                    // Save Booking Slots inside relevant Resources data
                    resourceBookingSlot.setBookingId(booking.getId());

                    existingResourceBookedSlots.add(resourceBookingSlot);
                    resource.setBookedSlots(existingResourceBookedSlots);
                    resourcesRepository.save(resource);

                    return bookingDTOConverter.convertBookingToBookingDTO(booking);

                } catch (NoSuchElementException e) {
                    System.out.println("Invalid Resource ID! : "+e.getMessage());
                } catch (MappingException e) {
                    System.out.println("MappingException --- Cannot Map Objects! : "+e.getMessage());
                }
            }
            return null;
        }
    }

    // Retrieving all the Bookings data
    public List<BookingDTO> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();

        List<BookingDTO> bookingDTOList = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDTOList.add(bookingDTOConverter.convertBookingToBookingDTO(booking));
        }
        return bookingDTOList;
    }

    // Get a specific Booking information
    public BookingDTO getBooking(String bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId).get();
            return bookingDTOConverter.convertBookingToBookingDTO(booking);
        } catch (NoSuchElementException e) {
            System.out.println("EXCEPTION! - Probably an invalid BookingId! : "+e.getMessage());
            return null;
        }
    }

    // Delete a Booking from the database
    public boolean deleteBooking(String bookingId) {
        if (bookingRepository.existsById(bookingId)){
            bookingRepository.deleteById(bookingId);
            System.out.println("Booking for the ID:-"+bookingId+" is successfully deleted...");
        }
        else
            System.out.println("Booking for the ID:-"+bookingId+" is already removed, or does not exists...");
        return !bookingRepository.existsById(bookingId);
    }
}
