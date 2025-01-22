package ru.practicum.shareit.item.validator;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public class ItemValidator {
    public static void checkItemId(Optional<Item> itemOpt) {
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("Вещь с таким id не существует");
        }
    }
}
