package ru.practicum.shareit.mock.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker, owner;
    private Item item1;
    private Booking booking;
    private BookingRequestDto bookingRequestDto1;
    private ItemRequest itemRequest;


    @BeforeEach
    void setUp() {
        booker = new User(1L, "booker", "description1");
        owner = new User(2L, "owner", "description2");
        itemRequest = new ItemRequest(1L, booker, "request1",
                LocalDateTime.of(2025, 11, 10, 12, 00, 00));
        item1 = new Item(1L, "item name1", "item description1", true, owner, itemRequest);

        bookingRequestDto1 = new BookingRequestDto(LocalDateTime.of(2025, 12, 10, 12, 00, 00),
                LocalDateTime.of(2025, 12, 12, 12, 00, 00),
                item1.getId());
        booking = new Booking(
                1L,
                bookingRequestDto1.getStart(),
                bookingRequestDto1.getEnd(),
                item1, booker, Status.WAITING);
    }

    @Test
    void createBooking() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingResponseDto savedBookingDto = bookingService.addNewBooking(booker.getId(), bookingRequestDto1);
        assertEquals(1, savedBookingDto.getItem().getId());
        assertEquals(1, savedBookingDto.getBooker().getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBookingWhenStartAndEndAreEqual() {
        bookingRequestDto1.setEnd(bookingRequestDto1.getStart());
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(bookingRepository.findByItemIdOrderByStartDateAsc(anyLong())).thenReturn(List.of());
        assertThrows(ValidationException.class, () -> bookingService.addNewBooking(booker.getId(), bookingRequestDto1));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingWhenItemNotFound() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.addNewBooking(booker.getId(), bookingRequestDto1));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingWhenItemNotAvailable() {
        item1.setIsAvailable(false);
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        assertThrows(ValidationException.class, () -> bookingService.addNewBooking(booker.getId(), bookingRequestDto1));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createApprove() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto approvedBookingDto = bookingService.updateBooking(owner.getId(), booking.getId(), true);

        assertEquals(Status.APPROVED, approvedBookingDto.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createRejected() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto approvedBookingDto = bookingService.updateBooking(owner.getId(), booking.getId(), false);

        assertEquals(Status.REJECTED, approvedBookingDto.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createApproveWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(booker.getId(), 1L, true));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createApproveWhenUserIsNotOwner() {
        User otherUser = new User(3L, "otherUser", "otherUser@mail.ru");
        item1.setOwner(otherUser);
        booking.setItem(item1);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(booker.getId(), booking.getId(), true));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void searchBookingsForOwnerWithDefaultState() {
        String stateParam = "all";
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from, size, Sort.by("startDate").descending());
        when(bookingRepository.findByBookerId(booker.getId(), pageable)).thenReturn(List.of(booking));
        List<BookingResponseDto> bookings = bookingService.getAllUserBookings(booker.getId(), stateParam, from, size);

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), booker.getId());
        verify(bookingRepository).findByBookerId(booker.getId(), pageable);
    }

    @Test
    void searchBookingsForBookedItemsOwnerDefaultState() {
        String stateParam = "all";
        Integer from = 0;
        Integer size = 10;
        when(bookingRepository.findAllBookingsOwnerItems(owner.getId(), size, from)).thenReturn(List.of(booking));
        List<BookingResponseDto> bookings = bookingService.getBookedItemsOwner(owner.getId(), stateParam, from, size);

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getItem().getOwner().getId(), owner.getId());
        verify(bookingRepository).findAllBookingsOwnerItems(owner.getId(), size, from);
    }

    @Test
    void searchBooking() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingResponseDto foundBookingDto = bookingService.getBooking(booking.getId());
        assertEquals(booking.getId(), foundBookingDto.getId());
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    void searchBookingWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(booking.getId()));
    }

    @Test
    void createBookingWhenUserNotFound() {
        when(userRepository.findById(booker.getId())).thenThrow(new NotFoundException("Пользователь не найден"));

        assertThrows(NotFoundException.class, () -> bookingService.addNewBooking(booker.getId(), bookingRequestDto1));
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}
