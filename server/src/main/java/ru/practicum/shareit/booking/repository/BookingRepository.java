package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemIdOrderByStartDateAsc(Long itemId);

    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    @Query(value = "SELECT b.* FROM bookings b " +
                   "WHERE b.booker_id = ?1 AND (b.start_date < CURRENT_TIMESTAMP AND b.end_date > CURRENT_TIMESTAMP) " +
                   "ORDER BY b.start_date DESC " +
                   "LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Booking> findCurrentBookings(Long bookerId, Integer size, Integer from);

    @Query(value = "SELECT b.* FROM bookings b " +
                   "WHERE b.booker_id = ?1 AND b.end_date < CURRENT_TIMESTAMP) " +
                   "ORDER BY b.start_date DESC " +
                   "LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Booking> findPastBookings(Long bookerId, Integer size, Integer from);

    @Query(value = "SELECT b.* FROM bookings b " +
                   "WHERE b.booker_id = ?1 AND b.end_date > CURRENT_TIMESTAMP) " +
                   "ORDER BY b.start_date DESC " +
                   "LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Booking> findFutureBookings(Long bookerId, Integer size, Integer from);

    List<Booking> findByBookerIdAndStatus(Long bookerId, State status, Pageable pageable);

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


    @Query(value = "SELECT b.* FROM bookings b " +
                   "JOIN items it ON b.item_id = it.id " +
                   "WHERE it.owner_id = ?1 AND status = ?2 " +
                   "ORDER BY b.start_date DESC " +
                   "LIMIT ?3 OFFSET ?4", nativeQuery = true)
    List<Booking> findBookingsOwnerItems(Long ownerId, State status, Integer size, Integer from);

    @Query(value = "SELECT b.* FROM bookings b " +
                   "JOIN items it ON b.item_id = it.id " +
                   "WHERE it.owner_id = ?1 " +
                   "ORDER BY b.start_date DESC " +
                   "LIMIT ?3 OFFSET ?4", nativeQuery = true)
    List<Booking> findAllBookingsOwnerItems(Long ownerId, Integer size, Integer from);

    @Query(value = "SELECT b.* FROM bookings b " +
                   "JOIN items it ON b.item_id = it.id " +
                   "WHERE it.owner_id = ?1 AND (b.start_date < CURRENT_TIMESTAMP AND b.end_date > CURRENT_TIMESTAMP) " +
                   "ORDER BY b.start_date DESC " +
                   "LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Booking> findCurrentBookingsOwnerItems(Long bookerId, Integer size, Integer from);

    @Query(value = "SELECT b.* FROM bookings b " +
                   "JOIN items it ON b.item_id = it.id " +
                   "WHERE it.owner_id = ?1 AND b.end_date < CURRENT_TIMESTAMP) " +
                   "ORDER BY b.start_date DESC " +
                   "LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Booking> findPastBookingsOwnerItems(Long bookerId, Integer size, Integer from);

    @Query(value = "SELECT b.* FROM bookings b " +
                   "JOIN items it ON b.item_id = it.id " +
                   "WHERE it.owner_id = ?1 AND b.end_date > CURRENT_TIMESTAMP) " +
                   "ORDER BY b.start_date DESC " +
                   "LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Booking> findFutureBookingsOwnerItems(Long bookerId, Integer size, Integer from);

}
