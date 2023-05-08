package ru.practicum.shareit.user;

import java.util.List;

public interface UserDao {

    User create(User user);

    User update(int id, User user);

    User get(int id);

    List<User> findAll();

    void del(int id);

}
