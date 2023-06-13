package ru.practicum.shareit.tools.exception;

public class ItemNotFoundException extends NotFoundException {

    public ItemNotFoundException(Long id) {
        super("No Item with id=" + id);
    }

}