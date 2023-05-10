package ru.practicum.shareit.tools.exception;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(Long id) {
        super("No User with id=" + id);
    }

}
