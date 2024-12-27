package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static Item mapToItem(ItemReqDto request) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setIsAvailable(request.getAvailable());
        return item;
    }

    public static ItemResponseDto mapToItemDto(Item item) {
        ItemResponseDto itemDto = new ItemResponseDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getIsAvailable());
        itemDto.setRequest(item.getRequest());

        return itemDto;
    }

    public static Item updateMapToItem(Item item, ItemUpdateRequestDto itemUpdateRequestDto) {
        if (itemUpdateRequestDto.hasName()) {
            item.setName(itemUpdateRequestDto.getName());
        }
        if (itemUpdateRequestDto.hasDescription()) {
            item.setDescription(itemUpdateRequestDto.getDescription());
        }
        if (itemUpdateRequestDto.hasAvailable()) {
            item.setIsAvailable(itemUpdateRequestDto.getAvailable());
        }

        return item;
    }
}
