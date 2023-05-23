package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.tools.Validator;
import ru.practicum.shareit.tools.exception.BookingValidateFailException;
import ru.practicum.shareit.tools.exception.ItemValidateFailException;
import ru.practicum.shareit.tools.exception.NotFoundException;
import ru.practicum.shareit.tools.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Transactional
    public Booking saveBooking(BookingDto bookingDto, Long ownerId) {
        // Если предмет недоступен, выдаем ошибку
        checkBookingItemExist(bookingDto);

        // Запрет на бронирование своих предметов
        checkBookingOwnItems(bookingDto, ownerId);

        // Всевозможные проверки начала и конца бронирования полученного объекта
        Validator.bookingDatesValidation(bookingDto);

        // Заполнение booking из bookingDto
        Booking booking = fillBookingFormBookingDtoForSave(bookingDto, ownerId);

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking approveBooking(Long id, Long ownerId, String approved) {
        Booking booking = checkBookingExist(id);

        // Проверка на повторное подтверждение бронирования
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingValidateFailException("Booking with id=" + id + " already approved");
        }

        // Проверка, что только владелец предмета может подтверждать бронирование
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Only the owner of the item can approve the booking");
        }

        // Замена статуса бронирования
        if (approved.equals("true")) {
            booking.setStatus(BookingStatus.APPROVED);
        } else if (approved.equals("false")) {
            booking.setStatus(BookingStatus.REJECTED);
        } else {
            throw new BookingValidateFailException("Value 'approved' must be only 'true' or 'false'");
        }

        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long id, Long ownerId) {
        Booking booking = checkBookingExist(id);

        // Может быть выполнено либо автором бронирования, либо владельцем вещи
        if (booking.getItem().getOwner().getId().equals(ownerId) ||
                booking.getBooker().getId().equals(ownerId)) {
            return booking;
        } else {
            throw new NotFoundException("Getting Booking By id only for Booker or Owner");
        }
    }

    /**
     * booker = true -> ForBooker
     * booker = false -> ForOwner
     */
    public List<Booking> getAllBookings(Long bookerId, String state, boolean booker) {
        User user = userService.getUser(bookerId);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case "ALL": // Все
                if (booker) {
                    return bookingRepository.findBookingsForBookerStatusAll(user);
                } else {
                    return bookingRepository.findBookingsForOwnerStatusAll(user);
                }
            case "FUTURE": // Будущие
                if (booker) {
                    return bookingRepository.findBookingsForBookerStatusFuture(user, now);
                } else {
                    return bookingRepository.findBookingsForOwnerStatusFuture(user, now);
                }
            case "WAITING": // Ожидающие подтверждения
                if (booker) {
                    return bookingRepository.findBookingsForBookerStatusWaiting(user, now);
                } else {
                    return bookingRepository.findBookingsForOwnerStatusWaiting(user, now);
                }
            case "REJECTED": // Отклонённые
                if (booker) {
                    return bookingRepository.findBookingsForBookerStatusRejected(user, now);
                } else {
                    return bookingRepository.findBookingsForOwnerStatusRejected(user, now);
                }
            case "CURRENT": // Текущие
                if (booker) {
                    return bookingRepository.findBookingsForBookerStatusCurrent(user, now);
                } else {
                    return bookingRepository.findBookingsForOwnerStatusCurrent(user, now);
                }
            case "PAST": // Завершённые
                if (booker) {
                    return bookingRepository.findBookingsForBookerStatusPast(user, now);
                } else {
                    return bookingRepository.findBookingsForOwnerStatusPast(user, now);
                }
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private Booking checkBookingExist(Long id) {
        // При использовании Optional<T> в методе можно упростить запись:
        return bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Booking not found, id=" + id));
    }

    /**
     * Если предмет недоступен, выдаем ошибку
     */
    private void checkBookingItemExist(BookingDto bookingDto) {
        Long itemId = bookingDto.getItemId();
        Item item = itemService.checkItemExist(itemId);
        if (!item.getAvailable()) {
            throw new ItemValidateFailException("Attempting to create a booking for an unavailable item!");
        }
    }

    /**
     * Запрет на бронирование своих предметов
     */
    private void checkBookingOwnItems(BookingDto bookingDto, Long ownerId) {
        Long itemId = bookingDto.getItemId();
        Item item = itemService.checkItemExist(itemId);
        if (item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Attempting to create booking form item's owner");
        }
    }

    /**
     * Заполнение booking из bookingDto
     */
    Booking fillBookingFormBookingDtoForSave(BookingDto bookingDto, Long ownerId) {
        Booking booking = new Booking();
        booking.setItem(itemService.checkItemExist(bookingDto.getItemId()));
        booking.setBooker(userService.getUser(ownerId));
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

}
