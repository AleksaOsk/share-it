package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemDto itemReqDto) {
        log.info("Создание вещи " + itemReqDto);
        return client.addNewItem(userId, itemReqDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("itemId") Long id,
                                             @Valid @RequestBody ItemDto itemReqDto) {
        log.info("Обновление вещи с id = " + id + " " + itemReqDto + " user id = " + userId);
        return client.updateItem(userId, id, itemReqDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("itemId") Long itemId) {
        log.info("Получение вещи с id " + itemId);
        return client.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех вещей пользователя с id " + userId);
        return client.getAllItemsOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(@RequestParam("text") String text) {
        log.info("Поиск вещей с текстом = " + text);
        return client.getItemsByText(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable("itemId") Long id) {
        log.info("Удаление вещи c id = " + id);
        client.deleteItem(id);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(@PathVariable("itemId") Long id,
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария " + commentDto + " к вещи " + id + " от пользователя с id " + userId);
        return client.addNewComment(id, userId, commentDto);
    }
}
