package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.BookingDtoForBookerId;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.tools.Const;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private User user1 = new User(1L, "testuser1", "tuser1@qq.com");
    private UserDto userDto1 = new UserDto(1L, "testuser1", "tuser1@qq.com");

    private Request request1 = new Request(1L, "Описание запроса", user1, LocalDateTime.now());
    private CommentDto commentDto1 = new CommentDto(1L, "", "", LocalDateTime.now());
    private BookingDtoForBookerId bookingDtoForBookerId1 = new BookingDtoForBookerId();

    private Item item1 = new Item(1L, "Тестовый предмет 1", "Описание 1", true, user1, request1);
    private ItemDto itemDto1 = new ItemDto(1L, "Тестовый предмет 1", "Описание 1", true, 1L);
    private ItemDtoDate itemDtoDate1 = new ItemDtoDate(1L, "Тестовый предмет 1", "Описание 1", true,
            List.of(commentDto1), bookingDtoForBookerId1, bookingDtoForBookerId1, 1L);


    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    void saveItemTest() throws Exception {
        when(itemService.saveItem(any(), any())).thenReturn(itemDto1);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                //.andDo(print()) // Дебаг запроса и ответа для отладки
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(itemDto1.getId()))
                .andExpect(jsonPath("name").value(itemDto1.getName()))
                .andExpect(jsonPath("description").value(itemDto1.getDescription()))
                .andExpect(jsonPath("available").value(itemDto1.getAvailable()))
                .andExpect(jsonPath("requestId").value(itemDto1.getRequestId()));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(any(), any(), any())).thenReturn(item1);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                .andExpect(status().isOk());
        // далее все andExpect(jsonPath(... по аналогии
    }

    @Test
    void getItemTest() throws Exception {
        when(itemService.getItemDtoDate(any(), any())).thenReturn(itemDtoDate1);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemByOwnerTest() throws Exception {
        when(itemService.getAllItemByOwner(any())).thenReturn(List.of(itemDtoDate1));

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getItemsByTextTest() throws Exception {
        when(itemService.getItemsByText(any())).thenReturn(List.of(item1));

        mvc.perform(get("/items/search/?text=qqq")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void saveCommentTest() throws Exception {
        when(itemService.saveComment(any(), any(), any())).thenReturn(commentDto1);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                .andExpect(status().isOk());
    }

}
