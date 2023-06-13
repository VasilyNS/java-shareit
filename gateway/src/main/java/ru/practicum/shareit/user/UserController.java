package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody UserDto userDto) {
        log.info("Begin of User creation: {}", userDto.toString());
        return userClient.saveUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Begin of User updating, id={}, {}", id, userDto.toString());
        return userClient.updateUser(id, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("Begin of User getting, id={}", id);
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Begin of User getAll");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void del(@PathVariable Long id) {
        log.info("Begin of User deleting, id={}", id);
        userClient.deleteUser(id);
    }

}
