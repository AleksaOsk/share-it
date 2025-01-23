package ru.practicum.shareit.user.validator;

import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class UserValidator {
    public static void checkEmail(String email, Optional<User> user) {
        email = email.toLowerCase();
        if (user.isPresent() && user.get().getEmail().toLowerCase().equals(email)) {
            throw new ConflictException("Имейл уже используется");
        }
    }
}
