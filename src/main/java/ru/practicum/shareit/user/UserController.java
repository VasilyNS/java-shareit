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
    public User create(@RequestBody UserDto userDto) {
        log.info("Begin of User creation: " + userDto.toString());
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Begin of User updating, id=" + id + ", " + userDto.toString());
        return userService.update(id, userDto);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        log.info("Begin of User getting, id=" + id);
        return userService.get(id);
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Begin of User getAll");
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    public void del(@PathVariable Long id) {
        log.info("Begin of User deleting, id=" + id);
        userService.del(id);
    }

}
