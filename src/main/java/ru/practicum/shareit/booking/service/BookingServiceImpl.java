package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        Optional<User> userOpt = userRepository.findById(userId);
        Validator.checkUserId(userOpt);
        Optional<Item> itemOpt = itemRepository.findById(bookingRequestDto.getItemId());
        Validator.checkItemId(itemOpt);
        Validator.checkStartDate(bookingRequestDto.getStart());
        Validator.checkEndDate(bookingRequestDto.getEnd());
        List<Booking> list = new ArrayList<>(
                bookingRepository.findByItemIdOrderByStartDateAsc(bookingRequestDto.getItemId()));
        Validator.checkCorrectBookingDate(bookingRequestDto.getStart(), bookingRequestDto.getEnd(), list);

        User user = userOpt.get();
        Item item = itemOpt.get();

        Validator.checkIsAvailable(item.getIsAvailable());
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
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Validator.checkBookingId(bookingOpt);
        Booking booking = bookingOpt.get();
        Optional<Item> itemOpt = itemRepository.findById(booking.getItem().getId());
        Validator.checkItemId(itemOpt);
        Validator.checkApproved(approved);
        Optional<User> userOpt = userRepository.findById(userId);

        if (Validator.checkIsOwner(userOpt, itemOpt)) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
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
    public List<BookingResponseDto> getAllUserBookings(Long userId) {
        log.info("Пришел запрос на получение всех бронирований от пользователя с id {}", userId);
        return bookingRepository.findByBookerId(userId)
                .stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBooking(Long id) {
        log.info("Пришел запрос на удаление вещи с id {}", id);
        bookingRepository.deleteById(id);
    }
}