package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemIdOrderByStartDateAsc(Long itemId);

    List<Booking> findByBookerId(Long bookerId);

    Optional<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    @Query(value = "SELECT b.* FROM bookings b " +
                   "JOIN items it ON b.item_id = it.id " +
                   "WHERE it.owner_id = ?1 AND b.start_date < CURRENT_TIMESTAMP " +
                   "ORDER BY b.start_date DESC", nativeQuery = true)
    Booking findLastBooking(Long itemId);

    @Query(value = "SELECT b.* FROM bookings b " +
                   "JOIN items it ON b.item_id = it.id " +
                   "WHERE it.owner_id = ?1 AND b.start_date > CURRENT_TIMESTAMP " +
                   "ORDER BY b.start_date ASC", nativeQuery = true)
    Booking findNextBooking(Long itemId);
}
