package ru.practicum.shareit.tools.exception;

public class RequestNotFoundException extends NotFoundException {

    public RequestNotFoundException(Long id) {
        super("No Request with id=" + id);
    }

}

