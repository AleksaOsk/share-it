package ru.practicum.shareit.item.dto;

import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemReqDto {
    private String name;
    private String description;
    private Boolean available;
}
