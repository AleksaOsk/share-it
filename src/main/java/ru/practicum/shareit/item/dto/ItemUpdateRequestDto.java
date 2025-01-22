package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemUpdateRequestDto {
    private String name;
    private String description;
    private Boolean available;
}