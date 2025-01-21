package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationException extends RuntimeException {
    private String reason;

    public ValidationException(String reason) {
        this.reason = reason;
    }
}
