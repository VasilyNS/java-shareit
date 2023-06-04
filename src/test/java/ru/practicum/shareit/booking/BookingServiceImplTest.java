package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestDto;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.tools.exception.BookingValidateFailException;
import ru.practicum.shareit.tools.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class BookingServiceImplTest {

    private UserDto userDto1;
    private UserDto userDto2;
    private UserDto userDto3;
    private User user1;
    private User user2;
    private User user3;

    private Item item1;
    private ItemDto itemDto1;
    private Request request1;
    private RequestDto requestDto1;
    private Comment comment1;
    private CommentDto commentDto1;
    private BookingDto bookingDto1;
    private BookingDtoForBookerId bookingDtoForBookerId1;

    private final UserService userService;
    private final ItemService itemService;
    private final RequestService requestService;
    private final BookingService bookingService;

    private void addNewTestItems() {
        userDto1 = new UserDto(0L, "testuser1", "tuser1@qq.com");
        userDto2 = new UserDto(0L, "testuser2", "tuser2@qq.com");
        userDto3 = new UserDto(0L, "testuser3", "tuser3@qq.com");
        user1 = userService.saveUser(userDto1);
        user2 = userService.saveUser(userDto2);
        user3 = userService.saveUser(userDto3);
        request1 = new Request(0L, "Описание запроса", user1, LocalDateTime.now());
        requestDto1 = RequestMapper.toRequestDto(request1);
        item1 = new Item(0L, "Тестовый предмет 1", "Описание 1", true, user2, request1);
        itemDto1 = ItemMapper.toItemDto(item1);
        itemDto1.setRequestId(1L);
        comment1 = new Comment(0L, "Тестовый комментарий", item1, user1, LocalDateTime.now());
        commentDto1 = new CommentDto(0L, "Тестовый комментарий", "", LocalDateTime.now());
        bookingDto1 = new BookingDto(1L, LocalDateTime.now().plusSeconds(3), LocalDateTime.now().plusSeconds(4));
        bookingDtoForBookerId1 = new BookingDtoForBookerId(1L, 1L);
    }

    @Test
    void approveBookingTest() {
        addNewTestItems();
        requestService.saveRequest(requestDto1, 3L);
        itemService.saveItem(itemDto1, 2L);

        // Бронирование на 3 секундны в будущем
        bookingService.saveBooking(bookingDto1, 1L);

        final BookingValidateFailException e1 = assertThrows(BookingValidateFailException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        // Ошибка в переключателе бронирования
                        bookingService.approveBooking(1L, 2L, "ewerwq");
                    }
                });

        final NotFoundException e = assertThrows(NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        // Проверка, что только владелец предмета может подтверждать бронирование
                        bookingService.approveBooking(1L, 3L, "true");
                    }
                });

        assertEquals("Only the owner of the item can approve the booking", e.getMessage());

        bookingService.approveBooking(1L, 2L, "true");

        final BookingValidateFailException e2 = assertThrows(BookingValidateFailException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        // Повторное бронирование
                        bookingService.approveBooking(1L, 2L, "true");
                    }
                });

        assertEquals("Booking with id=1 already approved", e2.getMessage());

    }

    @Test
    void getBookingByIdTest() {
        addNewTestItems();
        requestService.saveRequest(requestDto1, 3L);
        itemService.saveItem(itemDto1, 2L);
        bookingService.saveBooking(bookingDto1, 1L);

        Booking b = bookingService.getBookingById(1L, 1L);

        assertEquals("testuser1", b.getBooker().getName());

        final NotFoundException e = assertThrows(NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        // Ограничение по пользователям
                        bookingService.getBookingById(1L, 3L);
                    }
                });

        assertEquals("Getting Booking By id only for Booker or Owner", e.getMessage());
    }

    @Test
    void getAllBookingsTest() {
        addNewTestItems();
        requestService.saveRequest(requestDto1, 3L);
        itemService.saveItem(itemDto1, 2L);
        bookingService.saveBooking(bookingDto1, 1L);
        List<Booking> l;

        l = bookingService.getAllBookings(1L, "ALL", false, 0L, 350L);
        assertEquals(0, l.size());

        l = bookingService.getAllBookings(1L, "ALL", true, null, null);
        assertEquals(1, l.size());

        l = bookingService.getAllBookings(1L, "FUTURE", false, 0L, 350L);
        assertEquals(0, l.size());

        l = bookingService.getAllBookings(1L, "FUTURE", true, null, null);
        assertEquals(1, l.size());

        l = bookingService.getAllBookings(1L, "WAITING", false, 0L, 350L);
        assertEquals(0, l.size());

        l = bookingService.getAllBookings(1L, "WAITING", true, null, null);
        assertEquals(1, l.size());

        l = bookingService.getAllBookings(1L, "REJECTED", false, 0L, 350L);
        assertEquals(0, l.size());

        l = bookingService.getAllBookings(1L, "REJECTED", true, null, null);
        assertEquals(0, l.size());

        l = bookingService.getAllBookings(1L, "CURRENT", false, 0L, 350L);
        assertEquals(0, l.size());

        l = bookingService.getAllBookings(1L, "CURRENT", true, null, null);
        assertEquals(0, l.size());

        l = bookingService.getAllBookings(1L, "PAST", false, 0L, 350L);
        assertEquals(0, l.size());

        l = bookingService.getAllBookings(1L, "PAST", true, null, null);
        assertEquals(0, l.size());

    }
}