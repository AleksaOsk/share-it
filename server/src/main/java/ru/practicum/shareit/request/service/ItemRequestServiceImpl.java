package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Override
    public ItemRequestDto addNewItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Пришел запрос на создание нового запроса вещи от пользователя с id {} с названием {}",
                userId, itemRequestDto.getDescription());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " не существует"));

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(user);
        itemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestWithItemsDto> getAllItemRequestsOwner(Long userId) {
        log.info("Пришел запрос на получение всех своих запросов вещей от пользователя с id {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " не существует"));
        List<ItemRequestWithItemsDto> listReq = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map((ItemRequest itemRequest) ->
                        ItemRequestMapper.mapToItemRequestWithItemsDto(itemRequest, new ArrayList<>()))
                .toList();

        List<Item> items = itemRepository.findByRequestorId(userId);

        for (ItemRequestWithItemsDto itemsDto : listReq) {
            List<ItemDto> listItemsDto = items.stream()
                    .filter(item -> itemsDto.getId().equals(item.getRequest().getId()))
                    .map(ItemRequestMapper::mapToItemDto)
                    .toList();

            itemsDto.setItems(listItemsDto);
        }

        return listReq;
    }

    @Override
    public ItemRequestWithItemsDto getItemRequest(Long id) {
        log.info("Пришел запрос на получение запроса вещи с id {}", id);
        ItemRequestWithItemsDto itemDto = itemRequestRepository.findById(id)
                .map((ItemRequest itemRequest) ->
                        ItemRequestMapper.mapToItemRequestWithItemsDto(itemRequest, null))
                .orElseThrow(() -> new NotFoundException("Запрос вещи не найден с id = " + id));

        List<ItemDto> items = itemRepository.findByRequestId(id)
                .stream()
                .map(ItemRequestMapper::mapToItemDto)
                .toList();
        itemDto.setItems(items);

        return itemDto;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests() {
        log.info("Пришел запрос на получение всех запросов вещей");
        return itemRequestRepository.findAllByOrderByCreatedDesc()
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }
}
