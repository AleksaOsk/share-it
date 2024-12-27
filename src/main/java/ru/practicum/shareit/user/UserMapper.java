package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static User mapToUser(UserRequestDto request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        return user;
    }

    public static UserResponseDto mapToUserDto(User user) {
        UserResponseDto userDTO = new UserResponseDto();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());

        return userDTO;
    }

    public static User updateMapToUser(User user, UserUpdateRequestDto userUpdateRequestDTO) {
        if (userUpdateRequestDTO.hasName()) {
            user.setName(userUpdateRequestDTO.getName());
        }
        if (userUpdateRequestDTO.hasEmail()) {
            user.setEmail(userUpdateRequestDTO.getEmail());
        }

        return user;
    }
}
