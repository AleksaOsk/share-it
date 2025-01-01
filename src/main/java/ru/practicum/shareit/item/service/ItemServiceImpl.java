package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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

    @Override
    public ItemResponseDto addNewItem(Long userId, ItemReqDto itemReqDto) {
        log.info("Пришел запрос на создание новой вещи с названием {} от пользователя с id {}",
                itemReqDto.getName(), userId);
        checkUserId(userId);
        checkName(itemReqDto.getName());
        checkDescription(itemReqDto.getDescription());
        checkAvailable(itemReqDto.getAvailable());

        Optional<User> userOpt = userRepository.getUser(userId);
        User user = userOpt.get();

        Item item = ItemMapper.mapToItem(itemReqDto);
        item.setOwner(user);
        item = itemRepository.addNewItem(item);

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemResponseDto updateItem(Long userId, Long id, ItemUpdateRequestDto itemReqDto) {
        log.info("Пришел запрос на обновление вещи с id {} от пользователя с id {}", id, userId);
        checkUserId(userId);
        checkId(id);

        Optional<Item> itemOpt = itemRepository.getItem(id);
        Item item = itemOpt.get();
        Item itemNew = ItemMapper.updateMapToItem(item, itemReqDto);
        item = itemRepository.updateItem(itemNew);

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemResponseDto getItem(Long id) {
        log.info("Пришел запрос на получение вещи с id {}", id);
        return itemRepository.getItem(id)
                .map(ItemMapper::mapToItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь не найден с id = " + id));
    }

    @Override
    public Collection<ItemResponseDto> getAllItemsOwner(Long userId) {
        log.info("Пришел запрос на получение всех вещей пользователя с id {}", userId);
        checkUserId(userId);
        return itemRepository.getAllItemsOwner(userId)
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
        return itemRepository.getItemsByText(text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long id) {
        log.info("Пришел запрос на удаление вещи с id {}", id);
        itemRepository.deleteItem(id);
    }

    private void checkName(String name) {
        if (name == null || name.isBlank() || name.isEmpty()) {
            throw new ValidationException("Имя должно быть указано");
        }
    }

    private void checkId(long id) {
        if (itemRepository.getItem(id).isEmpty()) {
            throw new NotFoundException("Вещь с таким id не существует");
        }
    }

    private void checkDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new ValidationException("Описание должно быть указано");
        } else if (description.length() > 200) {
            throw new ValidationException("Описание должно быть не более 200 символов");
        }
    }

    private void checkAvailable(Boolean available) {
        if (available == null) {
            throw new ValidationException("Доступность вещи должна быть указана");
        }
    }

    private void checkUserId(long id) {
        if (userRepository.getUser(id).isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не существует");
        }
    }
}