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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.Validator;

import java.util.ArrayList;
import java.util.Collection;
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

    @Override
    public ItemResponseDto addNewItem(Long userId, ItemReqDto itemReqDto) {
        log.info("Пришел запрос на создание новой вещи от пользователя с id {}", userId);
        Validator.checkName(itemReqDto.getName());
        Validator.checkDescription(itemReqDto.getDescription());
        Validator.checkAvailable(itemReqDto.getAvailable());
        Optional<User> userOpt = userRepository.findById(userId);
        Validator.checkUserId(userOpt);

        User user = userOpt.get();

        Item item = ItemMapper.mapToItem(itemReqDto);
        item.setOwner(user);
        item = itemRepository.save(item);

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemResponseDto updateItem(Long userId, Long id, ItemUpdateRequestDto itemReqDto) {
        log.info("Пришел запрос на обновление вещи с id {} от пользователя с id {}", id, userId);
        Validator.checkUserId(userRepository.findById(userId));
        Optional<Item> itemOpt = itemRepository.findById(id);
        Validator.checkItemId(itemOpt);

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
    public Collection<ItemResponseDto> getAllItemsOwner(Long userId) {
        log.info("Пришел запрос на получение всех вещей пользователя с id {}", userId);
        Validator.checkUserId(userRepository.findById(userId));
        return itemRepository.findByOwnerId(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemResponseDto> getItemsByText(String text) {
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