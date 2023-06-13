package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.tools.exception.ValidationException;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
//@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookingDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);

        return bookingClient.saveBooking(userId, requestDto);
    }

    /**
     * Модификация метода из
     *
     * @ PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
     * @ Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
     */
    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                              @RequestParam(name = "from", defaultValue = "0") Long from,
                                              @RequestParam(name = "size", defaultValue = "10") Long size) {

        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));

        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                      @RequestParam(name = "from", defaultValue = "0") Long from,
                                                      @RequestParam(name = "size", defaultValue = "10") Long size) {

        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));

        return bookingClient.getBookingsForOwner(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);

        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> approveBooking(@PathVariable Long id,
                                                 @RequestParam String approved,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Begin of Booking approving, id={}", id);

        return bookingClient.approveBooking(id, userId, approved);
    }

}
