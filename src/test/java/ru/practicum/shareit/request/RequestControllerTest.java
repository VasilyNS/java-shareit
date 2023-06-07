package ru.practicum.shareit.request;

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
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDtoForBookerId;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemDtoDate;
import ru.practicum.shareit.tools.Const;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @Autowired
    private MockMvc mvc;

    private User user1 = new User(1L, "testuser1", "tuser1@qq.com");
    private UserDto userDto1 = new UserDto(1L, "testuser1", "tuser1@qq.com");


    private CommentDto commentDto1 = new CommentDto(1L, "", "", LocalDateTime.now());
    private BookingDtoForBookerId bookingDtoForBookerId1 = new BookingDtoForBookerId();
    private ItemDto itemDto1 = new ItemDto(1L, "Тестовый предмет 1", "Описание 1", true, 1L);
    private Request request1 = new Request(1L, "Описание запроса", user1,
            LocalDateTime.of(2020, 07, 07, 15, 37, 23));
    private RequestDto requestDto1 = new RequestDto(1L, "Описание запроса", userDto1,
            LocalDateTime.of(2020, 07, 07, 15, 37, 23),
            List.of(itemDto1));
    private Item item1 = new Item(1L, "Тестовый предмет 1", "Описание 1", true, user1, request1);
    private ItemDtoDate itemDtoDate1 = new ItemDtoDate(1L, "Тестовый предмет 1", "Описание 1", true,
            List.of(commentDto1), bookingDtoForBookerId1, bookingDtoForBookerId1, 1L);
    private Booking booking1 =
            new Booking(1L,
                    LocalDateTime.of(2020, 07, 07, 15, 37, 23),
                    LocalDateTime.of(2120, 07, 07, 15, 37, 23),
                    item1, user1, BookingStatus.WAITING);


    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    void saveRequestTest() throws Exception {
        when(requestService.saveRequest(any(), any())).thenReturn(requestDto1);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                //.andDo(print()) // Дебаг запроса и ответа для отладки
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request1.getId()))
                .andExpect(jsonPath("$.description").value("Описание запроса"))
                .andExpect(jsonPath("$.requestor.email").value("tuser1@qq.com"))
                .andExpect(jsonPath("$.created").value("2020-07-07T15:37:23"));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(requestService.getRequestById(any(), any())).thenReturn(requestDto1);

        mvc.perform(get("/requests/1")
                        //.content(mapper.writeValueAsString(request1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request1.getId()))
                .andExpect(jsonPath("$.description").value("Описание запроса"))
                .andExpect(jsonPath("$.requestor.email").value("tuser1@qq.com"))
                .andExpect(jsonPath("$.created").value("2020-07-07T15:37:23"));
    }

    @Test
    void getRequestsOwnTest() throws Exception {
        when(requestService.getRequestsOwn(any())).thenReturn(List.of(requestDto1, requestDto1, requestDto1));

        mvc.perform(get("/requests")
                        //.content(mapper.writeValueAsString(request1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[1].description").value("Описание запроса"));
    }

    @Test
    void getRequestsAllTest() throws Exception {
        when(requestService.getRequestsAll(any(), any(), any())).thenReturn(List.of(requestDto1, requestDto1, requestDto1));

        mvc.perform(get("/requests/all")
                        //.content(mapper.writeValueAsString(request1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[1].description").value("Описание запроса"));
    }

}
