package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserUpdateRequestDto {
    private String name;
    private String email;
}
