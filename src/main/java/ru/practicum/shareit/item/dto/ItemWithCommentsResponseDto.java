package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Data
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
