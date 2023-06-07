package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.tools.exception.CommentValidateFailException;
import ru.practicum.shareit.tools.exception.ItemValidateFailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

/**
 * Пример интеграционного теста для разных взаимосвязанных сущностей и сервисов
 */
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class ItemServiceImplTest {

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
    void saveItemTest() {
        addNewTestItems();
        requestService.saveRequest(requestDtoIn1, 3L);
        itemService.saveItem(itemDto1, 2L);

        assertEquals("Тестовый предмет 1", itemService.checkItemExist(1L).getName());

        // Проверка валидации полей предмета
        itemDto1.setName("");

        final ItemValidateFailException e1 = assertThrows(ItemValidateFailException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        itemService.saveItem(itemDto1, 2L);
                    }
                });

        assertEquals("Field 'name' in Item must be not blank", e1.getMessage());
    }

    @Test
    void updateItem() {
        addNewTestItems();
        requestService.saveRequest(requestDtoIn1, 3L);
        itemService.saveItem(itemDto1, 2L);

        assertEquals("Тестовый предмет 1", itemService.checkItemExist(1L).getName());

        itemDto1.setName("Тестовый предмет UPDATE");
        itemDto1.setDescription("Описание UPDATE");
        itemDto1.setAvailable(false);
        itemService.updateItem(itemDto1, 2L, 1L);

        assertEquals("Тестовый предмет UPDATE", itemService.checkItemExist(1L).getName());
    }

    @Test
    void getItemDtoDateTest() {
        addNewTestItems();
        requestService.saveRequest(requestDtoIn1, 3L);
        itemService.saveItem(itemDto1, 2L);

        assertEquals("Тестовый предмет 1", itemService.getItemDtoDate(1L, 2L).getName());
    }

    @Test
    void getAllItemByOwnerTest() {
        addNewTestItems();
        requestService.saveRequest(requestDtoIn1, 3L);
        itemService.saveItem(itemDto1, 2L);

        List<ItemDtoDate> l = itemService.getAllItemByOwner(2L);
        assertEquals(1, l.size());
    }

    @Test
    void getItemsByTextTest() {
        addNewTestItems();
        requestService.saveRequest(requestDtoIn1, 3L);
        itemService.saveItem(itemDto1, 2L);

        List<Item> l = itemService.getItemsByText("преДмет");
        assertEquals(1, l.size());
    }

    @Test
    void saveCommentTest() throws InterruptedException {
        addNewTestItems();
        requestService.saveRequest(requestDtoIn1, 3L);
        itemService.saveItem(itemDto1, 2L);

        final CommentValidateFailException e = assertThrows(CommentValidateFailException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        itemService.saveComment(comment1, 1L, 1L);
                    }
                });

        // Без бронирования нельзя сохрянать комменты! Бронирование на 3 секундны в будущем
        bookingService.saveBooking(bookingDto1, 1L);

        // Переносимся на 7 секунд в будущее чтобы можно было сохранить комментарий
        TimeUnit.SECONDS.sleep(7);
        itemService.saveComment(comment1, 1L, 1L);
        comment1.getItem();

        assertEquals("Тестовый комментарий", itemService.getItemDtoDate(1L, 2L).getComments().get(0).getText());
    }


}