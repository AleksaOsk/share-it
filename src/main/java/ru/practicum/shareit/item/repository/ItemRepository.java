package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item addNewItem(Item item);

    Item updateItem(Item item);

    Optional<Item> getItem(Long id);

    Collection<Item> getAllItemsOwner(Long userId);

    Collection<Item> getItemsByText(String text);

    void deleteItem(Long id);
}
