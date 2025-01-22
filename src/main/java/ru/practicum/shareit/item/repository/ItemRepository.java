package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT * FROM items AS i " +
                   "WHERE (i.name ILIKE ?1 OR i.description ILIKE ?1) " +
                   "AND i.is_available = true",
            nativeQuery = true)
    List<Item> findByNameOrDescriptionContainingIgnoreCase(String text);

    List<Item> findByOwnerId(Long id);
}
