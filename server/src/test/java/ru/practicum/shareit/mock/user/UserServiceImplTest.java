package ru.practicum.shareit.mock.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user, updatedUser;
    private UserRequestDto userRequestDto1;
    private UserUpdateRequestDto userUpdateRequestDto;
    private UserResponseDto userResponseDto1, userResponseDto2;

    @BeforeEach
    void setUp() {
        userRequestDto1 = new UserRequestDto("user name", "user@mail.ru");
        user = new User(1L, userRequestDto1.getName(), userRequestDto1.getEmail());
        userResponseDto1 = new UserResponseDto(user.getId(), user.getName(), user.getEmail());

        userUpdateRequestDto = new UserUpdateRequestDto("update name", "update@mail.ru");
        updatedUser = new User(1L, userUpdateRequestDto.getName(), userUpdateRequestDto.getEmail());
        userResponseDto2 = new UserResponseDto(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail());
    }

    @Test
    void createUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponseDto createdUser = userService.addNewUser(userRequestDto1);
        assertEquals(userResponseDto1.getId(), createdUser.getId());
        assertEquals(userResponseDto1.getEmail(), createdUser.getEmail());
        assertEquals(userResponseDto1.getName(), createdUser.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserWhenEmailAlreadyRegistered() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.findByEmailIgnoreCase(updatedUser.getEmail()))
                .thenThrow(new ConflictException("Имейл уже используется"));
        assertThrows(ConflictException.class, () -> userService.updateUser(user.getId(), userUpdateRequestDto));
        verify(userRepository).findByEmailIgnoreCase(updatedUser.getEmail());
    }

    @Test
    void updateUserWhenUserDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.updateUser(updatedUser.getId(), userUpdateRequestDto));
        verify(userRepository).findById(user.getId()); //метод был вызван
        verify(userRepository, never()).save(any(User.class)); //метод не был вызван
    }

    @Test
    void getUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserResponseDto actualUserDto = userService.getUser(user.getId());
        assertEquals(user.getId(), actualUserDto.getId());
        assertEquals(user.getName(), actualUserDto.getName());
        assertEquals(user.getEmail(), actualUserDto.getEmail());
    }

    @Test
    void getUserWhenUserDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUser(user.getId()));
        verify(userRepository).findById(user.getId());
    }

    @Test
    void deleteUser() {
        userService.deleteUser(user.getId());
        verify(userRepository).deleteById(user.getId());
    }
}