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
    public User update(@PathVariable int id, @RequestBody UserDto userDto) {
        log.info("Begin of User updating, id=" + id + ", " + userDto.toString());
        return userService.update(id, userDto);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        log.info("Begin of User getting, id=" + id);
        return userService.get(id);
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Begin of User findAll");
        return userService.findAll();
    }


    @DeleteMapping("/{id}")
    public void del(@PathVariable int id) {
        log.info("Begin of User deleting, id=" + id);
        userService.del(id);
    }

}
