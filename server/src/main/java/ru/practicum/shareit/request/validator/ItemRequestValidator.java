package ru.practicum.shareit.request.validator;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Optional;

public class ItemRequestValidator {
    public static void checkItemRequestId(Optional<ItemRequest> itemRequestOpt) {
        if (itemRequestOpt.isEmpty()) {
            throw new NotFoundException("Запроса вещи с таким id не существует");
        }
    }
}
