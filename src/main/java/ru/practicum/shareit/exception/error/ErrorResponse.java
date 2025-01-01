package ru.practicum.shareit.exception.error;


import lombok.Getter;

@Getter
class ErrorResponse {
    private final String error;
    private String description;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public ErrorResponse(String error) {
        this.error = error;
    }
}