package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.tools.Const;
import ru.practicum.shareit.tools.Validator;
import ru.practicum.shareit.tools.exception.BookingValidateFailException;
import ru.practicum.shareit.tools.exception.ItemValidateFailException;
import ru.practicum.shareit.tools.exception.NotFoundException;
import ru.practicum.shareit.tools.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Transactional
    public BookingDtoOut saveBooking(BookingDto bookingDto, Long ownerId) {
        // Если предмет недоступен, выдаем ошибку
        checkBookingItemExist(bookingDto);

        // Запрет на бронирование своих предметов
        checkBookingOwnItems(bookingDto, ownerId);

        // Всевозможные проверки начала и конца бронирования полученного объекта
        Validator.bookingDatesValidation(bookingDto);

        // Заполнение booking из bookingDto
        Booking booking = fillBookingFormBookingDtoForSave(bookingDto, ownerId);

        Booking bookingForReturn = bookingRepository.save(booking);
        return bookingToBookingDtoOut(bookingForReturn);
    }

    @Transactional
    public BookingDtoOut approveBooking(Long id, Long ownerId, String approved) {
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

        Booking bookingForReturn = bookingRepository.save(booking);
        return bookingToBookingDtoOut(bookingForReturn);
    }

    public BookingDtoOut getBookingById(Long id, Long ownerId) {
        Booking booking = checkBookingExist(id);

        // Может быть выполнено либо автором бронирования, либо владельцем вещи
        if (booking.getItem().getOwner().getId().equals(ownerId) ||
                booking.getBooker().getId().equals(ownerId)) {
            return bookingToBookingDtoOut(booking);
        } else {
            throw new NotFoundException("Getting Booking By id only for Booker or Owner");
        }
    }

    /**
     * booker = true -> ForBooker
     * booker = false -> ForOwner
     */
    public List<BookingDtoOut> getAllBookings(Long bookerId, String state, boolean booker, Long from, Long size) {
        User user = userService.getUser(bookerId);
        Validator.pageableValidation(from, size);
        LocalDateTime now = LocalDateTime.now();

        int pageFrom;
        int pageSize;
        if (from == null || size == null) { // Если не задана пагинация, то выбираем все, но всё равно с ограничением!
            pageFrom = 0;
            pageSize = Const.MAX_PAGE_SIZE; // Ограничение выдачи больших списков для защиты от флуда
        } else {
            pageFrom = Math.toIntExact(from / size); // Конвертация начала страницы в номер элемента
            pageSize = Math.toIntExact(size);
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(pageFrom, pageSize, sort);

        List<Booking> lb;

        switch (state) {
            case "ALL": // Все
                if (booker) {
                    lb = bookingRepository.findBookingsForBookerStatusAll(user, page);
                    return bookingListToBookingDtoOut(lb);
                } else {
                    lb = bookingRepository.findBookingsForOwnerStatusAll(user, page);
                    return bookingListToBookingDtoOut(lb);
                }
            case "FUTURE": // Будущие
                if (booker) {
                    lb = bookingRepository.findBookingsForBookerStatusFuture(user, now);
                    return bookingListToBookingDtoOut(lb);
                } else {
                    lb = bookingRepository.findBookingsForOwnerStatusFuture(user, now);
                    return bookingListToBookingDtoOut(lb);
                }
            case "WAITING": // Ожидающие подтверждения
                if (booker) {
                    lb = bookingRepository.findBookingsForBookerStatusWaiting(user, now);
                    return bookingListToBookingDtoOut(lb);
                } else {
                    lb = bookingRepository.findBookingsForOwnerStatusWaiting(user, now);
                    return bookingListToBookingDtoOut(lb);
                }
            case "REJECTED": // Отклонённые
                if (booker) {
                    lb = bookingRepository.findBookingsForBookerStatusRejected(user, now);
                    return bookingListToBookingDtoOut(lb);
                } else {
                    lb = bookingRepository.findBookingsForOwnerStatusRejected(user, now);
                    return bookingListToBookingDtoOut(lb);
                }
            case "CURRENT": // Текущие
                if (booker) {
                    lb = bookingRepository.findBookingsForBookerStatusCurrent(user, now);
                    return bookingListToBookingDtoOut(lb);
                } else {
                    lb = bookingRepository.findBookingsForOwnerStatusCurrent(user, now);
                    return bookingListToBookingDtoOut(lb);
                }
            case "PAST": // Завершённые
                if (booker) {
                    lb = bookingRepository.findBookingsForBookerStatusPast(user, now);
                    return bookingListToBookingDtoOut(lb);
                } else {
                    lb = bookingRepository.findBookingsForOwnerStatusPast(user, now);
                    return bookingListToBookingDtoOut(lb);
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
    private Booking fillBookingFormBookingDtoForSave(BookingDto bookingDto, Long ownerId) {
        Booking booking = new Booking();
        booking.setItem(itemService.checkItemExist(bookingDto.getItemId()));
        booking.setBooker(userService.getUser(ownerId));
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    List<BookingDtoOut> bookingListToBookingDtoOut(List<Booking> lb) {
        List<BookingDtoOut> listOut = new ArrayList<>();
        for (Booking booking : lb) {
            listOut.add(bookingToBookingDtoOut(booking));
        }
        return listOut;
    }

    BookingDtoOut bookingToBookingDtoOut(Booking booking) {
        BookingDtoOut bookingDtoOut = new BookingDtoOut();

        bookingDtoOut.setId(booking.getId());
        bookingDtoOut.setStart(booking.getStart());
        bookingDtoOut.setEnd(booking.getEnd());
        bookingDtoOut.setStatus(booking.getStatus());

        UserDto booker = new UserDto();
        booker.setId(booking.getBooker().getId());
        bookingDtoOut.setBooker(booker);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());
        bookingDtoOut.setItem(itemDto);

        return bookingDtoOut;
    }


}
