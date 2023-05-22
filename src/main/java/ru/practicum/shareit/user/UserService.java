package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    User saveUser(UserDto userDto);

    User updateUser(Long id, UserDto userDto);

    User getUser(Long id);

    List<User> getAllUsers();

    void deleteUser(Long id);

}
