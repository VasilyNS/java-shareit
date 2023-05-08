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
    void InitEach() {
        userDao = new UserDaoImpl();
        itemDao = new ItemDaoImpl(userDao);

        user = new User(0, "Пользователь 1", "user1@user.com");
        item = new Item(0, "Дрель", "Простая ДРЕЛЬ!", true, 1, null);
    }

    @Test
    void testUserCreateAndGet() {
        List<User> l = userDao.findAll();
        assertEquals(0, l.size());

        userDao.create(user);

        l = userDao.findAll();
        assertEquals(1, l.size());

        User checkUser = userDao.get(1);
        assertEquals(1, checkUser.getId());
        assertEquals("Пользователь 1", checkUser.getName());
        assertEquals("user1@user.com", checkUser.getEmail());
    }

    @Test
    void testUserUpdate() {
        userDao.create(user);

        User checkUser = userDao.get(1);
        assertEquals(1, checkUser.getId());
        assertEquals("Пользователь 1", checkUser.getName());
        assertEquals("user1@user.com", checkUser.getEmail());

        User userUpd = new User(0, "Пользователь UPD", "user1UPD@user.com");
        userDao.update(1, userUpd);

        User checkUserUpd = userDao.get(1);
        assertEquals(1, checkUserUpd.getId());
        assertEquals("Пользователь UPD", checkUserUpd.getName());
        assertEquals("user1UPD@user.com", checkUserUpd.getEmail());
    }

    @Test
    void testUserfindAll() {
        userDao.create(user);
        User user2 = new User(0, "Пользователь 2", "user2@user.com");
        userDao.create(user2);

        List<User> l = userDao.findAll();
        assertEquals(2, l.size());

        User checkUser = l.get(1);
        assertEquals(2, checkUser.getId());
        assertEquals("Пользователь 2", checkUser.getName());
        assertEquals("user2@user.com", checkUser.getEmail());
    }

    @Test
    void testUserDel() {
        userDao.create(user);
        User user2 = new User(0, "Пользователь 2", "user2@user.com");
        userDao.create(user2);

        List<User> l = userDao.findAll();
        assertEquals(2, l.size());

        userDao.del(2);
        l = userDao.findAll();
        assertEquals(1, l.size());

        userDao.del(1);
        l = userDao.findAll();
        assertEquals(0, l.size());
    }

    @Test
    void testItemCreateGetAndFindAllByOwner() {
        List<Item> l = itemDao.findAllByOwner(1);
        assertEquals(0, l.size());

        userDao.create(user);
        itemDao.create(item, 1);

        l = itemDao.findAllByOwner(1);
        assertEquals(1, l.size());

        Item checkItem = itemDao.get(1);
        assertEquals(1, checkItem.getId());
        assertEquals("Дрель", checkItem.getName());
    }

    @Test
    void testItemUpdate() {
        List<Item> l = itemDao.findAllByOwner(1);
        assertEquals(0, l.size());

        userDao.create(user);
        itemDao.create(item, 1);

        Item itemUpd = new Item(0, "ДрельUPD", "Простая ДРЕЛЬ_UPD!!!", false, 1, null);
        itemDao.update(itemUpd, 1, 1);

        Item checkItemUpd = itemDao.get(1);
        assertEquals(1, checkItemUpd.getId());
        assertEquals("ДрельUPD", checkItemUpd.getName());
        assertEquals(false, checkItemUpd.getAvailable());
    }

    @Test
    void testItemFindByText() {
        userDao.create(user);

        Item item1 = new Item(0, "Дрель1", "Простая ДРЕЛ Ь!", true, 1, null);
        Item item2 = new Item(0, "Дрель2", "Простая Д РЕЛЬ!", true, 1, null);
        Item item3 = new Item(0, "Дрель3", "Простая ДРЕ ЛЬ!", true, 1, null);
        Item item4 = new Item(0, "Дрель4", "Простая ДР ЕЛЬ!", false, 1, null);

        itemDao.create(item1, 1);
        itemDao.create(item2, 1);
        itemDao.create(item3, 1);
        itemDao.create(item4, 1);

        List<Item> l = itemDao.findByText("дреЛь");
        assertEquals(3, l.size());
    }

}
