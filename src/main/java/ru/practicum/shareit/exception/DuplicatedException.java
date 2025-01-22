package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuplicatedException extends RuntimeException {
    private String reason;

    public DuplicatedException(String reason) {
        this.reason = reason;
    }
}
