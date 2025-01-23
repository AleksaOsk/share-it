package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private BookingRepository bookingRepository;
    private ItemRequestRepository itemRequestRepository;

    @Override
    public ItemResponseDto addNewItem(Long userId, ItemReqDto itemReqDto) {
        log.info("Пришел запрос на создание новой вещи от пользователя с id {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " не существует"));

        Item item = ItemMapper.mapToItem(itemReqDto);

        if (itemReqDto.getRequestId() != null) {
            ItemRequest req = itemRequestRepository.findById(itemReqDto.getRequestId())
                    .orElseThrow(()
                            -> new NotFoundException("Запроса с id = " + itemReqDto.getRequestId() + " не существует"));
            item.setRequest(req);
        }

        item.setOwner(user);
        item = itemRepository.save(item);

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemResponseDto updateItem(Long userId, Long id, ItemUpdateRequestDto itemReqDto) {
        log.info("Пришел запрос на обновление вещи с id {} от пользователя с id {}", id, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " не существует"));
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + id + " не существует"));

        Item itemNew = ItemMapper.updateMapToItem(item, itemReqDto);
        item = itemRepository.save(itemNew);

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemWithCommentsResponseDto getItem(Long userId, Long id) {
        log.info("Пришел запрос на получение вещи с id {}", id);
        ItemWithCommentsResponseDto itemResponseDto = itemRepository.findById(id)
                .map(ItemMapper::mapToItemWithCommentsDto)
                .orElseThrow(() -> new NotFoundException("Вещь не найден с id = " + id));
        itemResponseDto.setComments(commentRepository.findByItemId(id));
        itemResponseDto.setLastBooking(bookingRepository.findLastBooking(id));
        itemResponseDto.setNextBooking(bookingRepository.findNextBooking(id));
        return itemResponseDto;
    }

    @Override
    public List<ItemResponseDto> getAllItemsOwner(Long userId) {
        log.info("Пришел запрос на получение всех вещей пользователя с id {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " не существует"));
        return itemRepository.findByOwnerId(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> getItemsByText(String text) {
        log.info("Пришел запрос на получение всех вещей с названием {}", text);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository
                .findByNameOrDescriptionContainingIgnoreCase(text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long id) {
        log.info("Пришел запрос на удаление вещи с id {}", id);
        itemRepository.deleteById(id);
    }
}