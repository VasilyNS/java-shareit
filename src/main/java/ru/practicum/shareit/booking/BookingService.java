package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    BookingDtoOut saveBooking(BookingDto bookingDto, Long ownerId);

    BookingDtoOut approveBooking(Long id, Long ownerId, String approved);

    BookingDtoOut getBookingById(Long id, Long ownerId);

    List<BookingDtoOut> getAllBookings(Long userId, String state, boolean booker, Long from, Long size);

}
