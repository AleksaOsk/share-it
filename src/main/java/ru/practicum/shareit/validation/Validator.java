package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Validator {

    public static void checkName(String name) {
        if (name == null || name.isBlank() || name.isEmpty()) {
            throw new ValidationException("Имя должно быть указано");
        }
    }

    public static void checkUserId(Optional<User> userOpt) {
        if (userOpt.isEmpty()) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
    }

    public static void checkEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Имейл должен быть указан");
        } else if (!email.contains("@")) {
            throw new ValidationException("Имейл должен содержать символ '@'");
        }
    }

    public static void checkItemId(Optional<Item> itemOpt) {
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("Вещь с таким id не существует");
        }
    }

    public static void checkBookingId(Optional<Booking> bookingOpt) {
        if (bookingOpt.isEmpty()) {
            throw new NotFoundException("Бронирования с таким id не существует");
        }
    }

    public static void checkDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new ValidationException("Описание должно быть указано");
        } else if (description.length() > 200) {
            throw new ValidationException("Описание должно быть не более 200 символов");
        }
    }

    public static void checkAvailable(Boolean available) {
        if (available == null) {
            throw new ValidationException("Доступность вещи должна быть указана");
        }
    }

    public static void checkStartDate(LocalDateTime startDate) {
        if (startDate == null || startDate.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала бронирования вещи должна быть указана");
        }
    }

    public static void checkEndDate(LocalDateTime endDate) {
        if (endDate == null || endDate.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата окончания бронирования вещи должна быть указана");
        }
    }

    public static void checkCorrectBookingDate(LocalDateTime start1, LocalDateTime end1, List<Booking> list) {
        if (!start1.isBefore(end1)) {
            throw new ValidationException("Дата начала бронирования должна быть раньше его окончания");
        }

        if (!list.isEmpty()) {
            for (Booking booking : list) {
                LocalDateTime start2 = booking.getStartDate();
                LocalDateTime end2 = booking.getEndDate();
                if (start1.isEqual(start2)
                    || (start1.isAfter(start2) && start1.isBefore(end2))
                    || (end1.isAfter(start2) && end1.isBefore(end2))
                    || (start2.isAfter(start1) && start2.isBefore(end1))
                ) {
                    throw new ValidationException("Даты заняты для бронирования");
                }
            }
        }
    }

    public static Boolean checkIsOwner(Optional<User> userOpt, Optional<Item> itemOpt) {
        Item item = itemOpt.get();
        User user = userOpt.get();
        if (!user.getId().equals(item.getOwner().getId())) {
            throw new ValidationException("Бронирование не может быть подтверждено");
        }
        return true;
    }

    public static void checkApproved(Boolean approved) {
        if (approved == null) {
            throw new ValidationException("Бронирование должно быть подтверждено или отклонено");
        }
    }

    public static void checkTextComment(String text) {
        if (text == null || text.isBlank()) {
            throw new ValidationException("Текст комментария не может быть пустым");
        }
    }

    public static void checkIsAvailable(Boolean isAvailable) {
        if (!isAvailable) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
    }

    public static void checkBooking(Optional<Booking> bookingOpt) {
        if (bookingOpt.isEmpty()) {
            throw new ValidationException("Нельзя оставить отзыв к вещи, которую не бронировал ранее");
        }
        Booking booking = bookingOpt.get();
        if (booking.getEndDate().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Нельзя оставить отзыв, бронирование еще не окончено");
        }
    }
}