package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT * FROM items AS i " +
            "WHERE (i.name ILIKE CONCAT('%', ?1, '%') OR i.description ILIKE CONCAT('%', ?1, '%')) " +
            "AND i.is_available = true",
            nativeQuery = true)
    List<Item> findByNameOrDescriptionContainingIgnoreCase(String text);

    List<Item> findByOwnerId(Long id);

    List<Item> findByRequestId(Long id);

    @Query(value = "select i.id, i.name, i.description, i.is_available, i.owner_id, " +
            "ir.id as request_id " +
            "from items as i " +
            "join item_requests as ir on ir.id = i.request_id " +
            "where ir.requestor_id = ?1", nativeQuery = true)
    List<Item> findByRequestorId(Long requestorId);
}
