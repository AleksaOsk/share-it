package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addNewItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemsDto> getAllItemRequestsOwner(Long userId);

    ItemRequestWithItemsDto getItemRequest(Long id);

    List<ItemRequestDto> getAllItemRequests();
}
