package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.tools.Const;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {


    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @MockBean
    ItemService itemService;

    @MockBean
    BookingService bookingService;

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
    void saveBookingTest() throws Exception {
        when(bookingService.saveBooking(any(), any())).thenReturn(booking1);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                //.andDo(print()) // Дебаг запроса и ответа для отладки
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking1.getId()))
                .andExpect(jsonPath("$.start").value("2020-07-07T15:37:23"))
                .andExpect(jsonPath("$.end").value("2120-07-07T15:37:23"))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Тестовый предмет 1"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("testuser1"))
                .andExpect(jsonPath("$.status").value(booking1.getStatus().toString()));
    }

    @Test
    void approveBookingTest() throws Exception {
        when(bookingService.approveBooking(any(), any(), any())).thenReturn(booking1);

        mvc.perform(patch("/bookings/1?approved=true")
                        .content(mapper.writeValueAsString(booking1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                //.andDo(print()) // Дебаг запроса и ответа для отладки
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking1.getId()))
                .andExpect(jsonPath("$.start").value("2020-07-07T15:37:23"))
                .andExpect(jsonPath("$.end").value("2120-07-07T15:37:23"))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Тестовый предмет 1"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("testuser1"))
                .andExpect(jsonPath("$.status").value(booking1.getStatus().toString()));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.getBookingById(any(), any())).thenReturn(booking1);

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(booking1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                //.andDo(print()) // Дебаг запроса и ответа для отладки
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking1.getId()))
                .andExpect(jsonPath("$.start").value("2020-07-07T15:37:23"))
                .andExpect(jsonPath("$.end").value("2120-07-07T15:37:23"))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Тестовый предмет 1"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("testuser1"))
                .andExpect(jsonPath("$.status").value(booking1.getStatus().toString()));
    }

    @Test
    void getAllBookingsForBookerTest() throws Exception {
        when(bookingService.getAllBookings(any(), any(), eq(true), any(), any()))
                .thenReturn(List.of(booking1, booking1, booking1));

        mvc.perform(get("/bookings?state=FUTURE")
                        //.content(mapper.writeValueAsString(booking1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                //.andDo(print()) // Дебаг запроса и ответа для отладки
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[1].item.name").value("Тестовый предмет 1"));
    }

    @Test
    void getAllBookingsForOwnerTest() throws Exception {
        when(bookingService.getAllBookings(any(), any(), eq(false), any(), any()))
                .thenReturn(List.of(booking1, booking1, booking1));

        mvc.perform(get("/bookings/owner?state=FUTURE")
                        //.content(mapper.writeValueAsString(booking1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Const.X_OWNER, "1"))
                //.andDo(print()) // Дебаг запроса и ответа для отладки
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[1].item.name").value("Тестовый предмет 1"));
    }

}
