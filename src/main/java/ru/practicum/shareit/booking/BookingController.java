package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.tools.Const;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking saveBooking(@RequestBody BookingDto bookingDto,
                               @RequestHeader Map<String, String> headers) {
        log.info("Begin of Booking creation: " + bookingDto.toString());
        Long ownerId = getCurUserId(headers);
        return bookingService.saveBooking(bookingDto, ownerId);
    }

    @PatchMapping("/{id}")
    public Booking approveBooking(@PathVariable Long id,
                                  @RequestParam String approved,
                                  @RequestHeader Map<String, String> headers) {
        log.info("Begin of Booking approving, id=" + id);
        Long ownerId = getCurUserId(headers);
        return bookingService.approveBooking(id, ownerId, approved);
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id,
                                  @RequestHeader Map<String, String> headers) {
        log.info("Begin of getting Booking by id, id=" + id);
        Long ownerId = getCurUserId(headers);
        return bookingService.getBookingById(id, ownerId);
    }

    /**
     * Получение списка всех бронирований текущего пользователя
     * Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     * Также он может принимать значения CURRENT (англ. «текущие»),
     * **PAST** (англ. «завершённые»), FUTURE (англ. «будущие»),
     * WAITING (англ. «ожидающие подтверждения»), REJECTED (англ. «отклонённые»).
     * Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
     */
    @GetMapping
    public List<Booking> getAllBookingsForBooker(@RequestParam(defaultValue = "ALL") String state,
                                                 @RequestHeader Map<String, String> headers,
                                                 @RequestParam(required = false) Long from,
                                                 @RequestParam(required = false) Long size) {
        log.info("Begin of getting all Bookings for user as Booker");
        Long bookerId = getCurUserId(headers);
        return bookingService.getAllBookings(bookerId, state, true, from, size);
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя.
     * Запрос имеет смысл для владельца хотя бы одной вещи.
     * Работа параметра state аналогична его работе в предыдущем сценарии.
     */
    @GetMapping("/owner")
    public List<Booking> getAllBookingsForOwner(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader Map<String, String> headers,
                                                @RequestParam(required = false) Long from,
                                                @RequestParam(required = false) Long size) {
        log.info("Begin of getting all Bookings for user as Owner");
        Long ownerId = getCurUserId(headers);
        return bookingService.getAllBookings(ownerId, state, false, from, size);
    }

    private Long getCurUserId(Map<String, String> headers) {
        return Long.parseLong(headers.get(Const.X_OWNER));
    }

}
