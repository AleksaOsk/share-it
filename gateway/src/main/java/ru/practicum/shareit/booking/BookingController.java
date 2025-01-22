package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient client;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("PЗапрос бронирования для пользователя {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return client.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return client.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Запрос бронирования {}, для userId={}", bookingId, userId);
        return client.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}") //Подтверждение или отклонение запроса на бронирование
    public ResponseEntity<Object> addApprove(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam(name = "approved")
                                             @NotNull(message = "Бронирование должно быть подтверждено или отклонено")
                                             Boolean approved) {
        log.info("Запрос бронирования {}, для userId={} со статусом подверждения {}", bookingId, userId, approved);
        return client.createApprove(userId, bookingId, approved);
    }

    @GetMapping("/owner") //поиск бронирований для хозяина вещей
    public ResponseEntity<Object> searchBookingsForOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Поиск бронирования для хозяина вещей с id {}", ownerId);
        return client.searchBookingsForOwner(ownerId);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable("bookingId") Long id) {
        client.deleteBooking(id);
    }
}