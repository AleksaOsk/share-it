package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Создание запроса пользователем {}, с описанием {}", userId, itemRequestDto.getDescription());
        return client.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запросы для пользователя с id " + userId);
        return client.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable("requestId") Long requestId) {
        log.info("Поиск всех запросов с id " + requestId);
        return client.getRequestById(requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests() {
        log.info("Поиск всех запросов");
        return client.getAllRequests();
    }
}