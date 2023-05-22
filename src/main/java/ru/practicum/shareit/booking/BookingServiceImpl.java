package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.tools.Validator;
import ru.practicum.shareit.tools.exception.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Transactional
    public Booking saveBooking(BookingDto bookingDto, Long ownerId) {
        Booking booking = new Booking();

        // Если предмет недоступен, выдаем ошибку
        Long itemId = bookingDto.getItemId();
        Item item = itemService.checkItemExist(itemId);
        if (!item.getAvailable()) {
            throw new ItemValidateFailException("Attempting to create a booking for an unavailable item!");
        }

        // Запрет на бронирование своих предметов
        if (item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Attempting to create booking form item's owner");
        }

        // Всевозможные проверки начала и конца бронирования полученного объекта
        Validator.allBookingValidation(bookingDto);

        booking.setItem(itemService.checkItemExist(bookingDto.getItemId()));
        booking.setBooker(userService.getUser(ownerId));
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);

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

    public List<Booking> getAllBookingsForBooker(Long bookerId, String state) {
        User user = userService.getUser(bookerId);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case "ALL": // Все
                return bookingRepository.getAllBookingsForBookerStatusAll(user);

            case "FUTURE": // Будущие
                return bookingRepository.getAllBookingsForBookerStatusFuture(user, now);

            case "WAITING": // Ожидающие подтверждения
                return bookingRepository.getAllBookingsForBookerStatusWaiting(user, now);

            case "REJECTED": // Отклонённые
                return bookingRepository.getAllBookingsForBookerStatusRejected(user, now);

            case "CURRENT": // Текущие
                return bookingRepository.getAllBookingsForBookerStatusCurrent(user, now);

            case "PAST": // Завершённые
                return bookingRepository.getAllBookingsForBookerStatusPast(user, now);

            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public List<Booking> getAllBookingsForOwner(Long ownerId, String state) {
        User user = userService.getUser(ownerId);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case "ALL": // Все
                return bookingRepository.getAllBookingsForOwnerStatusAll(user);

            case "FUTURE": // Будущие
                return bookingRepository.getAllBookingsForOwnerStatusFuture(user, now);

            case "WAITING": // Ожидающие подтверждения
                return bookingRepository.getAllBookingsForOwnerStatusWaiting(user, now);

            case "REJECTED": // Отклонённые
                return bookingRepository.getAllBookingsForOwnerStatusRejected(user, now);

            case "CURRENT": // Текущие
                return bookingRepository.getAllBookingsForOwnerStatusCurrent(user, now);

            case "PAST": // Завершённые
                return bookingRepository.getAllBookingsForOwnerStatusPast(user, now);

            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private Booking checkBookingExist(Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isEmpty()) {
            throw new NotFoundException("Booking not found, id=" + id);
        }
        return booking.get();
    }

}
