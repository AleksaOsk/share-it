package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsResponseDto;

import java.util.Collection;

public interface ItemService {
    ItemResponseDto addNewItem(Long userId, ItemReqDto itemReqDto);

    ItemResponseDto updateItem(Long userId, Long id, ItemUpdateRequestDto itemReqDto);

    ItemWithCommentsResponseDto getItem(Long userId, Long id);

    Collection<ItemResponseDto> getAllItemsOwner(Long userId);

    Collection<ItemResponseDto> getItemsByText(String text);

    void deleteItem(Long id);
}
