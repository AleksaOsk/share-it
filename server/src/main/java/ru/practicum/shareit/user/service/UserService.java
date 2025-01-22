package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

public interface UserService {
    UserResponseDto addNewUser(UserRequestDto userRequestDTO);

    UserResponseDto updateUser(Long id, UserUpdateRequestDto userRequestDTO);

    UserResponseDto getUser(Long id);

    void deleteUser(Long id);
}
