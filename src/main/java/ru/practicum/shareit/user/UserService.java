package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    public User create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return userDao.create(user);
    }

    public User update(int id, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return userDao.update(id, user);
    }

    public User get(int id) {
        return userDao.get(id);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public void del(int id) {
        userDao.del(id);
    }

}
