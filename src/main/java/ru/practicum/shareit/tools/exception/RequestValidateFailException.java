package ru.practicum.shareit.tools.exception;

public class RequestValidateFailException extends RuntimeException {

    public RequestValidateFailException(String message) {
        super(message);
    }

}
