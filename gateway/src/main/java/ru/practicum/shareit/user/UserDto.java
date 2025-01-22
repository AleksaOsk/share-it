package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class UserDto {
    @Size(max = 200)
    private String name;
    @Size(max = 200)
    @Email(message = "Некорректное указание E-mail")
    @NotBlank(message = "email должен быть указан")
    private String email;
}