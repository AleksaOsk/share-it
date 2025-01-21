package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto addNewBooking(Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto updateBooking(Long userId, Long id, Boolean approved);

    BookingResponseDto getBooking(Long id);

    List<BookingResponseDto> getAllUserBookings(Long userId, String stateParam, Integer from, Integer size);

    void deleteBooking(Long id);

    List<BookingResponseDto> getBookedItemsOwner(Long userId, String stateParam, Integer from, Integer size);
}
