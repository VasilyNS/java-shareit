package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User saveUser(@RequestBody UserDto userDto) {
        log.info("Begin of User creation: " + userDto.toString());
        return userService.saveUser(userDto);
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Begin of User updating, id=" + id + ", " + userDto.toString());
        return userService.updateUser(id, userDto);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        log.info("Begin of User getting, id=" + id);
        return userService.getUser(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Begin of User getAll");
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void del(@PathVariable Long id) {
        log.info("Begin of User deleting, id=" + id);
        userService.deleteUser(id);
    }

}
