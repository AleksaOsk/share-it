package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
public class BookingController {
    private BookingService bookingService;

    @PostMapping
    public BookingResponseDto addNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.addNewBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable("bookingId") Long bookingId,
                                            @RequestParam("approved") Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable("bookingId") Long id) {
        return bookingService.getBooking(id);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookedItemsOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.getBookedItemsOwner(userId, stateParam, from, size);
    }

    @GetMapping
    public List<BookingResponseDto> getAllUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.getAllUserBookings(userId, stateParam, from, size);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable("bookingId") Long id) {
        bookingService.deleteBooking(id);
    }
}

