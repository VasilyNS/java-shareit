package ru.practicum.shareit.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.tools.exception.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class Validator {

    public static void userEmailValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new UserEmailFailException("Email cannot be blank and must contain the '@' symbol");
        }
    }

    public static void allItemValidation(Item item) {
        if (item.getAvailable() == null) {
            throw new ItemValidateFailException("Field 'available' in Item must be not null");
        }

        if (item.getName() == null || item.getName().isBlank()) {
            throw new ItemValidateFailException("Field 'name' in Item must be not blank");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ItemValidateFailException("Field 'description' in Item must be not blank");
        }
    }

    public static void allBookingValidation(BookingDto bookingDto) {
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

        if (comment.getText() == null || comment.getText().isBlank()) {
            throw new CommentValidateFailException("Comment field 'text' must be not blank");
        }
    }

}
