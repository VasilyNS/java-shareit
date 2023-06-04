package ru.practicum.shareit.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.RequestDto;
import ru.practicum.shareit.tools.exception.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Валидация статическими методами
 */
@Slf4j
public class Validator {

    public static void userEmailValidation(User user) {
        if (!StringUtils.hasText(user.getEmail()) || !user.getEmail().contains("@")) {
            throw new UserEmailFailException("Email cannot be blank and must contain the '@' symbol");
        }
    }

    public static void allItemValidation(Item item) {
        if (item.getAvailable() == null) {
            throw new ItemValidateFailException("Field 'available' in Item must be not null");
        }

        if (!StringUtils.hasText(item.getName())) {
            throw new ItemValidateFailException("Field 'name' in Item must be not blank");
        }

        if (!StringUtils.hasText(item.getDescription())) {
            throw new ItemValidateFailException("Field 'description' in Item must be not blank");
        }
    }

    public static void bookingDatesValidation(BookingDto bookingDto) {
        LocalDateTime now = LocalDateTime.now();

        if (bookingDto.getEnd() == null) {
            throw new BookingValidateFailException("Booking end date-time is null");
        }

        if (bookingDto.getStart() == null) {
            throw new BookingValidateFailException("Booking start date-time is null");
        }

        if (bookingDto.getEnd().isBefore(now)) {
            throw new BookingValidateFailException("Booking end date-time in past");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BookingValidateFailException("Booking end before start");
        }

        if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new BookingValidateFailException("Booking end equals start");
        }

        if (bookingDto.getStart().isBefore(now)) {
            throw new BookingValidateFailException("Booking start date-time in past");
        }
    }

    public static void commentValidation(Comment comment) {
        if (!StringUtils.hasText(comment.getText())) {
            throw new CommentValidateFailException("Comment field 'text' must be not blank");
        }
    }

    public static void requestValidation(RequestDto requestDto) {
        if (!StringUtils.hasText(requestDto.getDescription())) {
            throw new RequestValidateFailException("Request field 'description' must be not blank");
        }

    }

    public static void pageableValidation(Long from, Long size) {
        if (from != null && size != null) {
            if (from < 0) {
                throw new ValidationException("Pageable validation fail, 'from' must be above 0");
            }
            if (size < 1) {
                throw new ValidationException("Pageable validation fail, 'size' must be above 1");
            }
            if (size > Const.MAX_PAGE_SIZE) {
                throw new ValidationException("Pageable validation fail, 'size' too big, max size = "
                        + Const.MAX_PAGE_SIZE);
            }
        }

    }

}
