package ru.practicum.shareit.user;

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
import ru.practicum.shareit.tools.exception.UserEmailFailException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private User user1 = new User(1L, "testuser1", "tuser1@qq.com");
    private UserDto userDto1 = new UserDto(1L, "testuser1", "tuser1@qq.com");

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    void saveUserTest() throws Exception {
        when(userService.saveUser(any())).thenReturn(user1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.name").value(userDto1.getName()))
                .andExpect(jsonPath("$.email").value(userDto1.getEmail()));
    }

    /**
     * Проверка ошибок для контроллера
     */
    @Test
    void saveUserValidationTest() throws Exception {
        when(userService.saveUser(any()))
                .thenThrow(new UserEmailFailException("Email cannot be blank and must contain the '@' symbol"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(any(), any())).thenReturn(user1);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.name").value(userDto1.getName()))
                .andExpect(jsonPath("$.email").value(userDto1.getEmail()));
    }

    @Test
    void getUserTest() throws Exception {
        when(userService.getUser(any())).thenReturn(user1);

        mvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.name").value(userDto1.getName()))
                .andExpect(jsonPath("$.email").value(userDto1.getEmail()));
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(user1, user1, user1));

        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //.andDo(print()) // Дебаг запроса и ответа для отладки
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(userDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto1.getName()))
                .andExpect(jsonPath("$[0].email").value(userDto1.getEmail()));
    }

    @Test
    void deleteUserTest() throws Exception {
        // when(userService.deleteUser(any())).thenReturn(...); // там void, нечего возвращать

        mvc.perform(delete("/users/1")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        System.out.println();
    }

}
