package ru.practicum.shareit.mock.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingResponseDto> json;

    @Test
    void testBookingDto() throws Exception {
        User user = new User(1L, "name u1", "u1@mail.ru");
        User booker = new User(2L, "name b2", "b2@mail.ru");
        Item item = new Item(1L, "item name", "item description", true, user, null);
        BookingResponseDto bookingResponseDto = new BookingResponseDto(
                1L,
                LocalDateTime.of(2025, 02, 12, 12, 00, 00),
                LocalDateTime.of(2025, 02, 17, 12, 00, 00),
                item, booker, Status.WAITING);
        JsonContent<BookingResponseDto> result = json.write(bookingResponseDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-02-12T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-02-17T12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("name b2");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("b2@mail.ru");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item name");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("item description");
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.owner.name").isEqualTo("name u1");
        assertThat(result).extractingJsonPathStringValue("$.item.owner.email").isEqualTo("u1@mail.ru");
//поля присутствуют в JSON после сериализации.
        assertThat(result.getJson()).contains("\"id\":1");
        assertThat(result.getJson()).contains("\"start\":\"2025-02-12T12:00:00\"");
        assertThat(result.getJson()).contains("\"end\":\"2025-02-17T12:00:00\"");

    }

    @Test
    void deserializeBookingDto() throws IOException {
        String jsonString = """
                {
                 "id": 1,
                 "start": "2025-02-12T12:00:00",
                 "end": "2025-02-17T12:00:00",
                 "item": {
                     "id": 1,
                     "name": "item1 name",
                     "description": "item1 description",
                     "isAvailable": true,
                     "owner": {
                         "id": 1,
                         "name": "name u1",
                         "email": "u1@mail.ru"
                     },
                     "request": null
                 },
                 "booker": {
                     "id": 2,
                     "name": "name b2",
                     "email": "b2@mail.ru"
                 },
                 "status": "WAITING"
                }
                """;
        BookingResponseDto deserializedBookingDto = json.parse(jsonString).getObject();


        assertEquals(null, deserializedBookingDto.getId()); //readOnly
        assertEquals(LocalDateTime.of(2025, 02, 12, 12, 0, 0), deserializedBookingDto.getStart());
        assertEquals(LocalDateTime.of(2025, 02, 17, 12, 0, 0), deserializedBookingDto.getEnd());
        assertEquals(1L, deserializedBookingDto.getItem().getId());

        User booker = deserializedBookingDto.getBooker();
        assertEquals(2L, booker.getId());
        assertEquals("name b2", booker.getName());
        assertEquals("b2@mail.ru", booker.getEmail());

        Item item = deserializedBookingDto.getItem();
        assertEquals(1L, item.getId());
        assertEquals("item1 name", item.getName());
        assertEquals("item1 description", item.getDescription());
        assertTrue(item.getIsAvailable());

        User owner = item.getOwner();
        assertEquals(1L, owner.getId());
        assertEquals("name u1", owner.getName());
        assertEquals("u1@mail.ru", owner.getEmail());
    }
}