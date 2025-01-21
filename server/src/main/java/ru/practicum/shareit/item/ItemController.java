package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private ItemService itemService;
    private CommentService commentService;

    @PostMapping
    public ItemResponseDto addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody ItemReqDto itemReqDto) {
        return itemService.addNewItem(userId, itemReqDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable("itemId") Long id,
                                      @RequestBody ItemUpdateRequestDto itemReqDto) {
        return itemService.updateItem(userId, id, itemReqDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithCommentsResponseDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable("itemId") Long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemResponseDto> getAllItemsOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> getItemsByText(@RequestParam("text") String text) {
        return itemService.getItemsByText(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable("itemId") Long id) {
        itemService.deleteItem(id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addNewComment(@PathVariable("itemId") Long id,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.addNewComment(id, userId, commentRequestDto);
    }

}