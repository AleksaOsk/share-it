package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ValidationException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State getState(String state) {
        state = state.toUpperCase();
        return switch (state) {
            case "ALL" -> State.ALL;
            case "CURRENT" -> State.CURRENT;
            case "PAST" -> State.PAST;
            case "FUTURE" -> State.FUTURE;
            case "WAITING" -> State.WAITING;
            case "REJECTED" -> State.REJECTED;
            default -> throw new ValidationException("Такого статуса нет: " + state);
        };
    }

}
