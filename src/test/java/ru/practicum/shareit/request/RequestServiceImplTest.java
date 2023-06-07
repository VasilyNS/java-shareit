package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class RequestServiceImplTest {

    private UserDto userDto1;
    private UserDto userDto2;
    private UserDto userDto3;
    private User user1;
    private User user2;
    private User user3;

    private Item item1;
    private ItemDto itemDto1;
    private Request request1;
    private RequestDtoIn requestDtoIn1;
    private RequestDto requestDto1;
    private Comment comment1;
    private CommentDto commentDto1;
    private BookingDto bookingDto1;

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
        requestDtoIn1 = new RequestDtoIn("Описание запроса");
        requestDto1 = RequestMapper.toRequestDto(request1);
        item1 = new Item(0L, "Тестовый предмет 1", "Описание 1", true, user2, request1);
        itemDto1 = ItemMapper.toItemDto(item1);
        itemDto1.setRequestId(1L);
        comment1 = new Comment(0L, "Тестовый комментарий", item1, user1, LocalDateTime.now());
        commentDto1 = new CommentDto(0L, "Тестовый комментарий", "", LocalDateTime.now());
        bookingDto1 = new BookingDto(1L, LocalDateTime.now().plusSeconds(3), LocalDateTime.now().plusSeconds(4));
    }

    @Test
    void getRequestsOwnTest() {
        addNewTestItems();
        requestService.saveRequest(requestDtoIn1, 3L);
        itemService.saveItem(itemDto1, 2L);
        bookingService.saveBooking(bookingDto1, 1L);

        // Пользователь с id=3 создал запрос
        List<RequestDto> l = requestService.getRequestsOwn(3L);

        assertEquals(1, l.size());
        assertEquals("Описание запроса", l.get(0).getDescription());
        assertEquals("Описание 1", l.get(0).getItems().get(0).getDescription());

        l = requestService.getRequestsOwn(1L);
        assertEquals(0, l.size());
    }

    @Test
    void getRequestsAllTest() {
        addNewTestItems();
        requestService.saveRequest(requestDtoIn1, 3L);
        itemService.saveItem(itemDto1, 2L);
        bookingService.saveBooking(bookingDto1, 1L);

        // Пользователь с id=2 владелец вещи
        List<RequestDto> l = requestService.getRequestsAll(2L, null, null);

        assertEquals(1, l.size());
        assertEquals("Описание запроса", l.get(0).getDescription());
        assertEquals("Описание 1", l.get(0).getItems().get(0).getDescription());

        l = requestService.getRequestsAll(3L, null, null);
        assertEquals(0, l.size());
    }

    @Test
    void getRequestByIdTest() {
        addNewTestItems();
        requestService.saveRequest(requestDtoIn1, 3L);
        itemService.saveItem(itemDto1, 2L);
        bookingService.saveBooking(bookingDto1, 1L);

        RequestDto r = requestService.getRequestById(1L, 3L);

        assertEquals(1, r.getId());
        assertEquals("Описание запроса", r.getDescription());
        assertEquals("tuser3@qq.com", r.getRequestor().getEmail());
    }

}