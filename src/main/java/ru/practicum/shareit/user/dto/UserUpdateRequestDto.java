package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserUpdateRequestDto {
    private String name;
    private String email;

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasName() {
        return name != null && !name.isBlank();
    }
}
