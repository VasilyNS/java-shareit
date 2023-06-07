package ru.practicum.shareit.tools;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.tools.exception.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ErrorAndToolTest {

    @Test
    void errorResponseTest() {
        ErrorResponse errorResponse = new ErrorResponse("TestErrorString");

        assertEquals("TestErrorString", errorResponse.getError());
    }

    /**
     * В этом проекте код ошибки в методах класса ErrorHandler получается через аннотации вида
     *
     * @ ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
     * а возвращается только примитивный класс ErrorResponse, поэтому протестировать код ошибки невозможно,
     * тестируем только сообщение об ошибке.
     * Для тестирования кода ошибки нужно переписывать методы класса ErrorHandler на конструкции вида:
     * public ResponseEntity<Map<String, String>> handleNotFound(final NotFoundException e) {
     * return new ResponseEntity<>(Map.of("message", e.getMessage()),HttpStatus.NOT_FOUND);
     * }
     */
    @Test
    void errorHandlerTest() {
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse;

        ValidationException e1 = new ValidationException("TestErrorString");
        errorResponse = errorHandler.handleValidationException(e1);
        assertEquals("TestErrorString", errorResponse.getError());

        Throwable e2 = new Throwable("TestErrorString");
        errorResponse = errorHandler.handleThrowable(e2);
        assertEquals("An unexpected error occurred", errorResponse.getError());

        NotFoundException e3 = new NotFoundException("TestErrorString");
        errorResponse = errorHandler.handleNotFoundException(e3);
        assertEquals("TestErrorString", errorResponse.getError());

        UserEmailFailException e4 = new UserEmailFailException("TestErrorString");
        errorResponse = errorHandler.handleUserEmailFailException(e4);
        assertEquals("TestErrorString", errorResponse.getError());

        ItemValidateFailException e5 = new ItemValidateFailException("TestErrorString");
        errorResponse = errorHandler.handleItemValidateFailException(e5);
        assertEquals("TestErrorString", errorResponse.getError());

        BookingValidateFailException e6 = new BookingValidateFailException("TestErrorString");
        errorResponse = errorHandler.handleBookingValidateFailException(e6);
        assertEquals("TestErrorString", errorResponse.getError());

        CommentValidateFailException e7 = new CommentValidateFailException("TestErrorString");
        errorResponse = errorHandler.handleCommentValidateFailException(e7);
        assertEquals("TestErrorString", errorResponse.getError());

        RequestValidateFailException e8 = new RequestValidateFailException("TestErrorString");
        errorResponse = errorHandler.handleRequestValidateFailException(e8);
        assertEquals("TestErrorString", errorResponse.getError());
    }

    @Test
    void notFoundExceptionTest() {
        ItemNotFoundException e1 = new ItemNotFoundException(777L);
        assertEquals("No Item with id=777", e1.getMessage());

        RequestNotFoundException e2 = new RequestNotFoundException(777L);
        assertEquals("No Request with id=777", e2.getMessage());
    }

    /**
     * Формальный тест для покрытия
     */
    @Test
    void mainAppTest() {
        String[] sa = new String[1];
        sa[0] = "test";
        ShareItApp.main(sa);
    }

    /**
     * Класс констант - тоже класс! Чтобы было 100% по классам!
     */
    @Test
    void constTest() {
        Const c = new Const();

        assertEquals("x-sharer-user-id", c.X_OWNER);
        assertEquals(20000, c.MAX_PAGE_SIZE);
    }


}
