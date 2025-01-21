package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BookingValidator {
    public static void checkBookingId(Optional<Booking> bookingOpt) {
        if (bookingOpt.isEmpty()) {
            throw new NotFoundException("Бронирования с таким id не существует");
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
