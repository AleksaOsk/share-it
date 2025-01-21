package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    @NotNull(message = "Поле itemId не должно быть пустым")
    private long itemId;
    @NotNull(message = "Поле start не должно быть пустым")
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    @NotNull(message = "Поле end не должно быть пустым")
    private LocalDateTime end;
}