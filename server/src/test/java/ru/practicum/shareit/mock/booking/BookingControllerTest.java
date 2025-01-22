package ru.practicum.shareit.mock.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @MockBean
    private UserService userService;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemService itemService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;
    private Booking booking1, booking2;
    private BookingRequestDto bookingDtoRequest1, bookingDtoRequest2;
    private BookingResponseDto bookingDtoResponse1, bookingDtoResponse2;
    private Item item1, item2;
    private ItemReqDto itemRequest1, itemRequest2;
    private ItemResponseDto itemResponse1, itemResponse2;
    private User user, booker;
    private UserRequestDto userRequest, bookerRequest;
    private UserResponseDto userResponse, bookerResponse;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule()); //для преобразования LocalDateTime

        userRequest = new UserRequestDto("name u1", "u1@mail.ru");
        bookerRequest = new UserRequestDto("name b1", "b1@mail.ru");
        userResponse = new UserResponseDto(1L, userRequest.getName(), userRequest.getEmail());
        bookerResponse = new UserResponseDto(2L, bookerRequest.getName(), bookerRequest.getEmail());

        user = UserMapper.mapToUser(userRequest);
        user.setId(userResponse.getId());
        booker = UserMapper.mapToUser(bookerRequest);
        booker.setId(bookerResponse.getId());

        itemRequest1 = new ItemReqDto("item1 name", "item1 description", true, null);
        itemRequest2 = new ItemReqDto("item2 name", "item2 description", true, null);
        itemResponse1 = new ItemResponseDto(
                1L, itemRequest1.getName(), itemRequest1.getDescription(), itemRequest1.getAvailable(), null);
        itemResponse2 = new ItemResponseDto(
                2L, itemRequest2.getName(), itemRequest2.getDescription(), itemRequest2.getAvailable(), null);

        item1 = ItemMapper.mapToItem(itemRequest1);
        item1.setId(itemResponse1.getId());
        item2 = ItemMapper.mapToItem(itemRequest2);
        item2.setId(itemResponse2.getId());

        bookingDtoRequest1 = new BookingRequestDto(
                LocalDateTime.of(2025, 11, 10, 12, 00, 00),
                LocalDateTime.of(2025, 11, 15, 12, 00, 00),
                1L);
        bookingDtoRequest2 = new BookingRequestDto(
                LocalDateTime.of(2025, 12, 12, 12, 00, 00),
                LocalDateTime.of(2025, 12, 17, 12, 00, 00),
                2L);
        bookingDtoResponse1 = new BookingResponseDto(
                1L, bookingDtoRequest1.getStart(), bookingDtoRequest1.getEnd(), item1, booker, Status.WAITING);
        bookingDtoResponse2 = new BookingResponseDto(
                2L, bookingDtoRequest2.getStart(), bookingDtoRequest2.getEnd(), item2, booker, Status.WAITING);

        booking1 = BookingMapper.mapToBooking(bookingDtoRequest1, item1);
        booking1.setId(bookingDtoResponse1.getId());
        booking2 = BookingMapper.mapToBooking(bookingDtoRequest2, item2);
        booking2.setId(bookingDtoResponse2.getId());

    }

    @Test
    void addNewBooking() throws Exception {

        when(bookingService.addNewBooking(anyLong(), any(BookingRequestDto.class)))
                .thenReturn(bookingDtoResponse1);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));


        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDtoResponse1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoResponse1.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoResponse1.getEnd().format(formatter))))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoResponse1.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.booker.name", is(bookingDtoResponse1.getBooker().getName())))
                .andExpect(jsonPath("$.item.name", is(bookingDtoResponse1.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDtoResponse1.getItem().getDescription())));

    }

    @Test
    void searchBookingForOwner() throws Exception {
        when(bookingService.getBookedItemsOwner(anyLong(), any(), any(), any()))
                .thenReturn(List.of(bookingDtoResponse1, bookingDtoResponse2));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))) // Проверка размера списка
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].start", is(bookingDtoResponse1.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDtoResponse1.getEnd().format(formatter))))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].start", is(bookingDtoResponse2.getStart().format(formatter))))
                .andExpect(jsonPath("$[1].end", is(bookingDtoResponse2.getEnd().format(formatter))));
    }

    @Test
    void searchBookingByIdForOwnerOrForBooker() throws Exception {
        when(bookingService.getBooking(anyLong()))
                .thenReturn(bookingDtoResponse1);
        mvc.perform(get("/bookings/{bookingId}", bookingDtoResponse1.getId())
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoResponse1.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoResponse1.getEnd().format(formatter))))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoResponse1.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.booker.name", is(bookingDtoResponse1.getBooker().getName())))
                .andExpect(jsonPath("$.item.name", is(bookingDtoResponse1.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDtoResponse1.getItem().getDescription())));
    }

    @Test
    void getInvalidBooking() throws Exception {
        when(bookingService.getBooking(anyLong()))
                .thenThrow(NotFoundException.class);
        mvc.perform(get("/bookings/{bookingId}", booking1.getId())
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void addApprove() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoResponse1);

        mvc.perform(patch("/bookings/{bookingId}", bookingDtoResponse1.getId())
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoResponse1.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDtoResponse1.getEnd().format(formatter))))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoResponse1.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.booker.name", is(bookingDtoResponse1.getBooker().getName())))
                .andExpect(jsonPath("$.item.name", is(bookingDtoResponse1.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDtoResponse1.getItem().getDescription())));
    }

    @Test
    void searchBookingForUserWithState() throws Exception {
        when(bookingService.getAllUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Arrays.asList(bookingDtoResponse1, bookingDtoResponse2));

        mvc.perform(get("/bookings?state=ALL")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))) // Проверка размера списка
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }
}
