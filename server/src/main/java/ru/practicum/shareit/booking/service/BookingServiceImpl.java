package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.validator.BookingValidator;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Override
    public BookingResponseDto addNewBooking(Long userId, BookingRequestDto bookingRequestDto) {
        log.info("Пришел запрос на создание нового бронирования на вещь с id {} от пользователя с id {}",
                bookingRequestDto.getItemId(), userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " не существует"));
        Long itemId = bookingRequestDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id = " + itemId + " не существует"));
        List<Booking> list = new ArrayList<>(
                bookingRepository.findByItemIdOrderByStartDateAsc(itemId));
        BookingValidator.checkCorrectBookingDate(bookingRequestDto.getStart(), bookingRequestDto.getEnd(), list);

        BookingValidator.checkIsAvailable(item.getIsAvailable());
        item.setIsAvailable(false);
        itemRepository.save(item);

        Booking booking = BookingMapper.mapToBooking(bookingRequestDto, item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        booking = bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingResponseDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        log.info("Пришел запрос на обновление бронирования с id {} от пользователя с id {}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с id = " + bookingId + " не существует"));
        Long itemId = booking.getItem().getId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не существует"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Пользователя с id = " + userId + " не существует"));

        if (BookingValidator.checkIsOwner(user, item)) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            booking = bookingRepository.save(booking);
            return BookingMapper.mapToBookingDto(booking);
        }
        return getBooking(bookingId);
    }

    @Override
    public BookingResponseDto getBooking(Long id) {
        log.info("Пришел запрос на получение бронирования с id {}", id);
        return bookingRepository.findById(id)
                .map(BookingMapper::mapToBookingDto)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено с id = " + id));
    }

    @Override
    public List<BookingResponseDto> getAllUserBookings(Long userId, String stateParam, Integer from, Integer size) {
        log.info("Пришел запрос от пользователя с id {} на получение бронирований со статусом {} начиная с {} " +
                "бронирования в количестве {}", userId, stateParam, from, size);
        State state = State.getState(stateParam);
        Pageable pageable = PageRequest.of(from, size, Sort.by("startDate").descending());
        List<Booking> bookings;
        switch (state) {
            case WAITING, REJECTED -> bookings = bookingRepository.findByBookerIdAndStatus(userId, state, pageable);
            case CURRENT -> bookings = bookingRepository.findCurrentBookings(userId, size, from);
            case PAST -> bookings = bookingRepository.findPastBookings(userId, size, from);
            case FUTURE -> bookings = bookingRepository.findFutureBookings(userId, size, from);
            default -> bookings = bookingRepository.findByBookerId(userId, pageable);
        }
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookedItemsOwner(Long userId, String stateParam, Integer from, Integer size) {
        log.info("Пришел запрос от пользователя с id {} на получение бронирований своих вещей со статусом {} начиная " +
                "с {} бронирования в количестве {}", userId, stateParam, from, size);
        State state = State.getState(stateParam);
        List<Booking> bookings;
        switch (state) {
            case WAITING, REJECTED -> bookings = bookingRepository.findBookingsOwnerItems(userId, state, size, from);
            case CURRENT -> bookings = bookingRepository.findCurrentBookingsOwnerItems(userId, size, from);
            case PAST -> bookings = bookingRepository.findPastBookingsOwnerItems(userId, size, from);
            case FUTURE -> bookings = bookingRepository.findFutureBookingsOwnerItems(userId, size, from);
            default -> bookings = bookingRepository.findAllBookingsOwnerItems(userId, size, from);
        }
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBooking(Long id) {
        log.info("Пришел запрос на удаление вещи с id {}", id);
        bookingRepository.deleteById(id);
    }
}