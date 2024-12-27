package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final HashMap<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    private Long generateId() {
        return ++id;
    }

    @Override
    public Item addNewItem(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItem(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> getAllItemsOwner(Long userId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId)).collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getItemsByText(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                 item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                                item.getIsAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long id) {
        items.remove(id);
    }
}
