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
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.validator.ItemRequestValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        Optional<User> userOpt = userRepository.findById(userId);
        UserValidator.checkUserId(userOpt);
        User user = userOpt.get();

        Item item = ItemMapper.mapToItem(itemReqDto);

        if (itemReqDto.getRequestId() != null) {
            Optional<ItemRequest> reqOpt = itemRequestRepository.findById(itemReqDto.getRequestId());
            ItemRequestValidator.checkItemRequestId(reqOpt);
            ItemRequest itemRequest = reqOpt.get();
            item.setRequest(itemRequest);
        }

        item.setOwner(user);
        item = itemRepository.save(item);

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemResponseDto updateItem(Long userId, Long id, ItemUpdateRequestDto itemReqDto) {
        log.info("Пришел запрос на обновление вещи с id {} от пользователя с id {}", id, userId);
        UserValidator.checkUserId(userRepository.findById(userId));
        Optional<Item> itemOpt = itemRepository.findById(id);
        ItemValidator.checkItemId(itemOpt);

        Item item = itemOpt.get();
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
        UserValidator.checkUserId(userRepository.findById(userId));
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