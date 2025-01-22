package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemWithCommentsResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
    private List<Comment> comments;
    private Booking lastBooking;
    private Booking nextBooking;
}
