package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    Booking saveBooking(BookingDto bookingDto, Long ownerId);

    Booking approveBooking(Long id, Long ownerId, String approved);

    Booking getBookingById(Long id, Long ownerId);

    List<Booking> getAllBookingsForBooker(Long bookerId, String state);

    List<Booking> getAllBookingsForOwner(Long ownerId, String state);

}
