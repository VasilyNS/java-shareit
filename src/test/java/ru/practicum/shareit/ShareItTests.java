package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDao;
import ru.practicum.shareit.item.ItemDaoImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;
import ru.practicum.shareit.user.UserDaoImpl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@RequiredArgsConstructor
class ShareItTests {

    private UserDao userDao;
    private ItemDao itemDao;

    private User user;
    private Item item;

    @BeforeEach
    void initEach() {
        userDao = new UserDaoImpl();
        itemDao = new ItemDaoImpl(userDao);

        user = new User(0L, "Пользователь 1", "user1@user.com");
        item = new Item(0L, "Дрель", "Простая ДРЕЛЬ!", true, 1L, null);
    }

    @Test
    void testUserCreateAndGet() {
        List<User> l = userDao.getAll();
        assertEquals(0, l.size());

        userDao.create(user);

        l = userDao.getAll();
        assertEquals(1, l.size());

        User checkUser = userDao.get(1L);
        assertEquals(1, checkUser.getId());
        assertEquals("Пользователь 1", checkUser.getName());
        assertEquals("user1@user.com", checkUser.getEmail());
    }

    @Test
    void testUserUpdate() {
        userDao.create(user);

        User checkUser = userDao.get(1L);
        assertEquals(1, checkUser.getId());
        assertEquals("Пользователь 1", checkUser.getName());
        assertEquals("user1@user.com", checkUser.getEmail());

        User userUpd = new User(0L, "Пользователь UPD", "user1UPD@user.com");
        userDao.update(1L, userUpd);

        User checkUserUpd = userDao.get(1L);
        assertEquals(1, checkUserUpd.getId());
        assertEquals("Пользователь UPD", checkUserUpd.getName());
        assertEquals("user1UPD@user.com", checkUserUpd.getEmail());
    }

    @Test
    void testUsergetAll() {
        userDao.create(user);
        User user2 = new User(0L, "Пользователь 2", "user2@user.com");
        userDao.create(user2);

        List<User> l = userDao.getAll();
        assertEquals(2, l.size());

        User checkUser = l.get(1);
        assertEquals(2, checkUser.getId());
        assertEquals("Пользователь 2", checkUser.getName());
        assertEquals("user2@user.com", checkUser.getEmail());
    }

    @Test
    void testUserDel() {
        userDao.create(user);
        User user2 = new User(0L, "Пользователь 2", "user2@user.com");
        userDao.create(user2);

        List<User> l = userDao.getAll();
        assertEquals(2, l.size());

        userDao.del(2L);
        l = userDao.getAll();
        assertEquals(1, l.size());

        userDao.del(1L);
        l = userDao.getAll();
        assertEquals(0, l.size());
    }

    @Test
    void testItemCreateGetAndgetAllByOwner() {
        List<Item> l = itemDao.getAllByOwner(1L);
        assertEquals(0, l.size());

        userDao.create(user);
        itemDao.create(item, 1L);

        l = itemDao.getAllByOwner(1L);
        assertEquals(1L, l.size());

        Item checkItem = itemDao.get(1L);
        assertEquals(1, checkItem.getId());
        assertEquals("Дрель", checkItem.getName());
    }

    @Test
    void testItemUpdate() {
        List<Item> l = itemDao.getAllByOwner(1L);
        assertEquals(0, l.size());

        userDao.create(user);
        itemDao.create(item, 1L);

        Item itemUpd = new Item(0L, "ДрельUPD", "Простая ДРЕЛЬ_UPD!!!", false, 1L, null);
        itemDao.update(itemUpd, 1L, 1L);

        Item checkItemUpd = itemDao.get(1L);
        assertEquals(1, checkItemUpd.getId());
        assertEquals("ДрельUPD", checkItemUpd.getName());
        assertEquals(false, checkItemUpd.getAvailable());
    }

    @Test
    void testItemgetByText() {
        userDao.create(user);

        Item item1 = new Item(0L, "Дрель1", "Простая ДРЕЛ Ь!", true, 1L, null);
        Item item2 = new Item(0L, "Дрель2", "Простая Д РЕЛЬ!", true, 1L, null);
        Item item3 = new Item(0L, "Дрель3", "Простая ДРЕ ЛЬ!", true, 1L, null);
        Item item4 = new Item(0L, "Дрель4", "Простая ДР ЕЛЬ!", false, 1L, null);

        itemDao.create(item1, 1L);
        itemDao.create(item2, 1L);
        itemDao.create(item3, 1L);
        itemDao.create(item4, 1L);

        List<Item> l = itemDao.getByText("дреЛь");
        assertEquals(3, l.size());
    }

}
