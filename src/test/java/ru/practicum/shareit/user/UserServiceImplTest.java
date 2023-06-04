package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.tools.exception.UserEmailFailException;
import ru.practicum.shareit.tools.exception.UserNotFoundException;
import ru.practicum.shareit.tools.exception.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

/**
 * UNIT-тесты SpringBoot, уровень сервиса, запись в БД и интеграционное тестирование
 */
// Используется SpringBoot для тестирования
@SpringBootTest
// Для корректной обработки бинов при тестах (@Autowired)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
// После каждого тестового метода откат (транзакции) БД к предыдущему значению
// из пакета org.springframework.transaction.annotation.Transactional !!!
@Transactional
// Дополнительная обновление Spring ApplicationContext, должен быть перед каждым методом
// как @DirtiesContext либо перед всем классом с параметром AFTER_EACH_TEST_METHOD
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
//@TestPropertySource(properties = { "db.name=test"})
class UserServiceImplTest {

    private final UserService userService;

    private UserDto tUser1Dto;
    private UserDto tUser2Dto;
    private UserDto tUser3Dto;

    private void addNew3TestUsers() {
        tUser1Dto = new UserDto(0L, "testuser1","tuser1@qq.com");
        tUser2Dto = new UserDto(0L, "testuser2","tuser2@qq.com");
        tUser3Dto = new UserDto(0L, "testuser3","tuser3@qq.com");
        userService.saveUser(tUser1Dto);
        userService.saveUser(tUser2Dto);
        userService.saveUser(tUser3Dto);
    }

    @Test
    void saveUserTest() {
        addNew3TestUsers();

        User tUser = userService.getUser(3L);

        assertEquals(3L, tUser.getId());
        assertEquals("testuser3", tUser.getName());
        assertEquals("tuser3@qq.com", tUser.getEmail());

        // Тест ошибки на дубль электронной почты
        final ValidationException e = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userService.saveUser(tUser3Dto);
                    }
                });

        assertEquals("Duplicate email! email= tuser3@qq.com", e.getMessage());

        // Тест ошибки на неверную электронной почту
        final UserEmailFailException e2 = assertThrows(UserEmailFailException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        tUser3Dto.setEmail("qqq");
                        userService.saveUser(tUser3Dto);
                    }
                });

        assertEquals("Email cannot be blank and must contain the '@' symbol", e2.getMessage());
    }

    @Test
    void updateUserTest() {
        addNew3TestUsers();
        tUser3Dto = new UserDto(0L, "testuser3upd","tuser3@upd.com");
        userService.updateUser(3L, tUser3Dto);

        User tUser = userService.getUser(3L);

        assertEquals(3L, tUser.getId());
        assertEquals("testuser3upd", tUser.getName());
        assertEquals("tuser3@upd.com", tUser.getEmail());

        tUser3Dto = new UserDto(0L, "testuser3upd","tuser1@qq.com");


        final ValidationException e = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userService.updateUser(3L, tUser3Dto);
                    }
                });

        assertEquals("Trying to update with duplicate email! user_id=3, email= tuser1@qq.com", e.getMessage());
    }


        @Test
    void getUserTest() {
        // Тестирование ошибки при попытке получить несуществующего пользователя
        final UserNotFoundException e = assertThrows(UserNotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userService.getUser(1L);
                    }
                });

        assertEquals("No User with id=1", e.getMessage());
    }

    @Test
    void getAllUsersTest() {
        List<User> l = userService.getAllUsers();

        assertEquals(0, l.size());

        addNew3TestUsers();

        l = userService.getAllUsers();
        User tUser = l.get(2); // Отчет с нуля, это get из List, т.е. элемент с id=3!

        assertEquals(3, l.size());

        assertEquals(3L, tUser.getId());
        assertEquals("testuser3", tUser.getName());
        assertEquals("tuser3@qq.com", tUser.getEmail());
    }

    @Test
    void deleteUserTest() {
        List<User> l = userService.getAllUsers();
        assertEquals(0, l.size());

        addNew3TestUsers();

        l = userService.getAllUsers();
        assertEquals(3, l.size());

        userService.deleteUser(2L); // Удаление пользователя с id=2

        l = userService.getAllUsers();
        assertEquals(2, l.size());

        // Тест, что нельзя получить удаленного пользователя по id
        final UserNotFoundException e = assertThrows(UserNotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userService.getUser(2L);
                    }
                });

        assertEquals("No User with id=2", e.getMessage());
    }

}