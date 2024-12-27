package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private ItemService itemService;

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
    public ItemResponseDto getItem(@PathVariable("itemId") Long id) {
        return itemService.getItem(id);
    }

    @GetMapping
    public Collection<ItemResponseDto> getAllItemsOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsOwner(userId);
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> getItemsByText(@RequestParam("text") String text) {
        return itemService.getItemsByText(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable("itemId") Long id) {
        itemService.deleteItem(id);
    }
}