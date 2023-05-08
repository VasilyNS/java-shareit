package ru.practicum.shareit.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.tools.exception.ItemValidateFailException;
import ru.practicum.shareit.tools.exception.UserEmailFailException;
import ru.practicum.shareit.user.User;

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

}
